package com.sharep.security;

import com.sharep.global.auth.AuthDetailsService;
import com.sharep.global.error.SecurityExceptionHandler;
import com.sharep.global.jwt.JwtProperty;
import com.sharep.global.jwt.JwtTokenFilter;
import com.sharep.global.jwt.JwtTokenProvider;
import com.sharep.global.refresh.RefreshToken;
import com.sharep.global.refresh.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtTokenProviderTest {
    private static final byte[] KEY = new byte[32];
    private AuthDetailsService users;
    private RefreshTokenRepository refreshTokens;
    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        users = mock(AuthDetailsService.class);
        refreshTokens = mock(RefreshTokenRepository.class);
        provider = new JwtTokenProvider(new JwtProperty(
                Base64.getEncoder().encodeToString(KEY), 3_600_000L,
                1_209_600_000L, "Authorization", "Bearer"), users, refreshTokens);
        provider.init();
        when(users.loadUserByUsername("tester"))
                .thenReturn(User.withUsername("tester").password("hash").roles("USER").build());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void accessTokenAuthenticates() {
        var auth = provider.getAuthentication(provider.generateAccessToken("tester"));
        assertThat(auth.isAuthenticated()).isTrue();
        assertThat(auth.getName()).isEqualTo("tester");
    }

    @Test
    void refreshTokenIsRejectedBeforeUserLookup() {
        String token = provider.generateRefreshToken("tester");
        assertThatThrownBy(() -> provider.getAuthentication(token))
                .isInstanceOf(BadCredentialsException.class);
        verifyNoInteractions(users);
    }

    @Test
    void redisTtlUsesSecondsWithoutChangingJwtExpiry() {
        long before = System.currentTimeMillis();
        String token = provider.generateRefreshToken("tester");
        ArgumentCaptor<RefreshToken> saved = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokens).save(saved.capture());
        assertThat(saved.getValue().getTtl()).isEqualTo(1_209_600L);
        var claims = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(KEY))
                .build().parseSignedClaims(token).getPayload();
        assertThat(claims.getExpiration().getTime() - before)
                .isBetween(1_209_598_000L, 1_209_602_000L);
    }

    @Test
    void expiredAndIncorrectlySignedTokensAreRejected() {
        String expired = Jwts.builder().header().add("type", "access").and()
                .subject("tester").expiration(new Date(System.currentTimeMillis() - 10_000))
                .signWith(Keys.hmacShaKeyFor(KEY)).compact();
        byte[] otherKey = new byte[32];
        otherKey[0] = 1;
        String wrongSignature = Jwts.builder().header().add("type", "access").and()
                .subject("tester").expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(otherKey)).compact();
        assertThatThrownBy(() -> provider.getAuthentication(expired))
                .isInstanceOf(BadCredentialsException.class);
        assertThatThrownBy(() -> provider.getAuthentication(wrongSignature))
                .isInstanceOf(BadCredentialsException.class);
        verifyNoInteractions(users);
    }

    @Test
    void missingTypeOrExpiryIsRejected() {
        String missingType = Jwts.builder().subject("tester")
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(KEY)).compact();
        String missingExpiry = Jwts.builder().header().add("type", "access").and()
                .subject("tester").signWith(Keys.hmacShaKeyFor(KEY)).compact();
        assertThatThrownBy(() -> provider.getAuthentication(missingType))
                .isInstanceOf(BadCredentialsException.class);
        assertThatThrownBy(() -> provider.getAuthentication(missingExpiry))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void bearerPrefixRequiresDelimiterAndNonemptyToken() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertThat(provider.resolveToken(request)).isNull();
        request.addHeader("Authorization", "Bearer token");
        assertThat(provider.resolveToken(request)).isEqualTo("token");
        request.removeHeader("Authorization");
        request.addHeader("Authorization", "Bearertoken");
        assertThatThrownBy(() -> provider.resolveToken(request))
                .isInstanceOf(BadCredentialsException.class);
        request.removeHeader("Authorization");
        request.addHeader("Authorization", "Bearer ");
        assertThatThrownBy(() -> provider.resolveToken(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void databaseFailureIsNotSilentlyTurnedIntoAnonymousRequest() {
        when(users.loadUserByUsername("tester"))
                .thenThrow(new DataAccessResourceFailureException("database unavailable"));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/prompt");
        request.setServletPath("/prompt");
        request.addHeader("Authorization", "Bearer " + provider.generateAccessToken("tester"));
        JwtTokenFilter filter = new JwtTokenFilter(provider, new SecurityExceptionHandler());

        assertThatThrownBy(() -> filter.doFilter(request, new MockHttpServletResponse(),
                (req, res) -> { throw new AssertionError("Controller must not be called"); }))
                .isInstanceOf(DataAccessResourceFailureException.class);
    }
}

