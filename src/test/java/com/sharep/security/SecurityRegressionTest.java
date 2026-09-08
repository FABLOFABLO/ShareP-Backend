package com.sharep.security;

import com.sharep.domain.prompt.domain.Prompt;
import com.sharep.domain.prompt.domain.repository.PromptRepository;
import com.sharep.domain.prompt.presentation.PromptController;
import com.sharep.domain.prompt.service.PromptCreateService;
import com.sharep.domain.prompt.service.PromptDeleteService;
import com.sharep.domain.prompt.service.PromptReadAllService;
import com.sharep.domain.prompt.service.PromptReadDetailService;
import com.sharep.domain.prompt.service.PromptSearchService;
import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.UserController;
import com.sharep.domain.user.service.LoginService;
import com.sharep.domain.user.service.UserSignupService;
import com.sharep.global.auth.AuthDetailsService;
import com.sharep.global.config.FilterConfig;
import com.sharep.global.config.SecurityConfig;
import com.sharep.global.error.GlobalExceptionHandler;
import com.sharep.global.error.SecurityExceptionHandler;
import com.sharep.global.jwt.JwtProperty;
import com.sharep.global.jwt.JwtTokenFilter;
import com.sharep.global.jwt.JwtTokenProvider;
import com.sharep.global.refresh.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Base64;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringJUnitWebConfig(SecurityRegressionTest.TestConfig.class)
class SecurityRegressionTest {

    @Autowired WebApplicationContext context;
    @Autowired UserRepository userRepository;
    @Autowired PromptRepository promptRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired JwtTokenProvider tokens;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired FilterConfig filterConfig;
    @Autowired JwtTokenFilter jwtTokenFilter;

    private MockMvc mvc;
    private String accessToken;

    @BeforeEach
    void setUp() {
        reset(userRepository, promptRepository, refreshTokenRepository);
        User user = User.builder()
                .loginId("tester")
                .password(passwordEncoder.encode("Abcdefgh!"))
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        when(userRepository.findByLoginId("tester")).thenReturn(Optional.of(user));
        accessToken = tokens.generateAccessToken("tester");
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void promptRoutesRequireAuthentication() throws Exception {
        mvc.perform(post("/prompt").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.errorCode").value(401));
        mvc.perform(delete("/prompt/10")).andExpect(status().isUnauthorized());
        mvc.perform(get("/prompt")).andExpect(status().isUnauthorized());
        mvc.perform(get("/prompt/10")).andExpect(status().isUnauthorized());
        verifyNoInteractions(promptRepository);
    }

    @Test
    void refreshTokenCannotAuthenticateApiRequest() throws Exception {
        String refresh = tokens.generateRefreshToken("tester");
        mvc.perform(post("/prompt").header("Authorization", "Bearer " + refresh)
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(promptRepository);
    }

    @Test
    void malformedTokenStopsBeforeController() throws Exception {
        mvc.perform(delete("/prompt/10").header("Authorization", "Bearer malformed"))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(promptRepository);
    }

    @Test
    void authorComesFromAuthenticatedUserEvenWhenBodySpecifiesAnotherAuthor() throws Exception {
        mvc.perform(post("/prompt").header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"title","description":"description",
                                 "prompt":"content","tag":"test","author":999}
                                """))
                .andExpect(status().isCreated());
        ArgumentCaptor<Prompt> saved = ArgumentCaptor.forClass(Prompt.class);
        verify(promptRepository).save(saved.capture());
        assertThat(saved.getValue().getAuthor()).isEqualTo(1L);
    }

    @Test
    void anotherUsersPromptCannotBeDeleted() throws Exception {
        Prompt prompt = promptOwnedBy(2L);
        when(promptRepository.findById(10L)).thenReturn(Optional.of(prompt));
        mvc.perform(delete("/prompt/10").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden()).andExpect(jsonPath("$.errorCode").value(403));
        verify(promptRepository, never()).delete(any(Prompt.class));
    }

    @Test
    void ownerCanDeletePrompt() throws Exception {
        Prompt prompt = promptOwnedBy(1L);
        when(promptRepository.findById(10L)).thenReturn(Optional.of(prompt));
        mvc.perform(delete("/prompt/10").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());
        verify(promptRepository).delete(prompt);
    }

    @Test
    void missingPromptStillReturns404() throws Exception {
        mvc.perform(delete("/prompt/10").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.errorCode").value(404));
    }

    @Test
    void loginRejectsBlankCredentialsBeforeCallingService() throws Exception {
        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content(""" 
                                {"loginId":"","password":""}
                                """))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errorCode").value(400));
        verifyNoInteractions(userRepository, refreshTokenRepository);
    }

    @Test
    void invalidCredentialsReturn401() throws Exception {
        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"tester","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.errorCode").value(401));
        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"missing","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized());
        verifyNoInteractions(refreshTokenRepository);
    }

    @Test
    void loginWorksAtCorrectRouteAndIgnoresStaleBearerHeader() throws Exception {
        mvc.perform(post("/user/login").header("Authorization", "Bearer expired")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"tester","password":"Abcdefgh!"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void publicLoginMatcherRespectsContextPath() throws Exception {
        mvc.perform(post("/api/user/login").contextPath("/api")
                        .header("Authorization", "Bearer expired")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"tester","password":"Abcdefgh!"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void duplicateSignupUsesHttp409() throws Exception {
        when(userRepository.existsByLoginId("tester")).thenReturn(true);
        mvc.perform(post("/user/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"tester","password":"Abcdefgh!"}
                                """))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.errorCode").value(409));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signupStoresEncodedPassword() throws Exception {
        mvc.perform(post("/user/signup").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"loginId":"newuser","password":"Abcdefgh!"}
                                """))
                .andExpect(status().isCreated());
        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).hasSize(60);
        assertThat(passwordEncoder.matches("Abcdefgh!", saved.getValue().getPassword())).isTrue();
    }

    @Test
    void jwtFilterIsNotRegisteredAsASecondContainerFilter() {
        assertThat(filterConfig.jwtFilterRegistration(jwtTokenFilter).isEnabled()).isFalse();
    }

    private Prompt promptOwnedBy(long author) {
        return Prompt.builder().title("title").prompt("content")
                .tag("test").author(author).build();
    }

    @Configuration
    @EnableWebMvc
    @EnableWebSecurity
    @Import({SecurityConfig.class, FilterConfig.class, JwtTokenFilter.class,
            JwtTokenProvider.class, AuthDetailsService.class, SecurityExceptionHandler.class,
            GlobalExceptionHandler.class, PromptController.class, UserController.class,
            PromptCreateService.class, PromptDeleteService.class, PromptReadAllService.class,
            PromptReadDetailService.class, PromptSearchService.class, LoginService.class, UserSignupService.class})
    static class TestConfig {
        @Bean UserRepository userRepository() { return mock(UserRepository.class); }
        @Bean PromptRepository promptRepository() { return mock(PromptRepository.class); }
        @Bean RefreshTokenRepository refreshTokenRepository() {
            return mock(RefreshTokenRepository.class);
        }
        @Bean JwtProperty jwtProperty() {
            return new JwtProperty(Base64.getEncoder().encodeToString(new byte[32]),
                    3_600_000L, 1_209_600_000L, "Authorization", "Bearer");
        }
    }
}
