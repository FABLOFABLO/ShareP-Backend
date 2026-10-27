package com.sharep.global.config;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.presentation.UserPageController;
import com.sharep.domain.user.presentation.dto.response.MyPageResponse;
import com.sharep.domain.user.presentation.dto.response.UserProfileResponse;
import com.sharep.domain.user.service.UserPageReadService;
import com.sharep.global.auth.AuthDetails;
import com.sharep.global.error.SecurityExceptionHandler;
import com.sharep.global.jwt.JwtTokenFilter;
import com.sharep.global.jwt.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserPageController.class)
@Import({SecurityConfig.class, JwtTokenFilter.class, SecurityExceptionHandler.class,
        UserPageSecurityTest.WebSecurityTestConfiguration.class})
class UserPageSecurityTest {

    @TestConfiguration(proxyBeanMethods = false)
    @EnableWebSecurity
    static class WebSecurityTestConfiguration {
    }

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private JwtTokenProvider tokenProvider;

    @MockitoBean
    private UserPageReadService pageReadService;

    private RequestPostProcessor authenticatedUser() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);
        AuthDetails details = new AuthDetails(user);
        return authentication(new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities()));
    }

    @Test
    void authenticatedUserCanReadBothPages() throws Exception {
        when(pageReadService.readMyPage(7L))
                .thenReturn(new MyPageResponse(7L, "mine", 0, 0, List.of(), List.of()));
        when(pageReadService.readProfile(7L, 8L))
                .thenReturn(new UserProfileResponse(8L, "other", 0, 0, false, List.of()));

        mvc.perform(get("/user/mypage").with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("mine"));
        mvc.perform(get("/user/8").with(authenticatedUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("other"));

        verify(pageReadService).readMyPage(7L);
        verify(pageReadService).readProfile(7L, 8L);
    }

    @Test
    void anonymousUserCannotReadPages() throws Exception {
        mvc.perform(get("/user/mypage")).andExpect(status().isUnauthorized());
        mvc.perform(get("/user/8")).andExpect(status().isUnauthorized());
    }

    @Test
    void unrelatedUserRouteRemainsDenied() throws Exception {
        mvc.perform(get("/user/mypage/extra").with(authenticatedUser()))
                .andExpect(status().isForbidden());
    }
}
