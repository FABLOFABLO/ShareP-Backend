package com.sharep.global.jwt;

import com.sharep.global.auth.AuthDetailsService;
import com.sharep.global.logout.AccessTokenBlacklist;
import com.sharep.global.refresh.RefreshTokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperty jwtProperty;
    private final AuthDetailsService authDetailsService;
    private final RefreshTokenStore refreshTokenStore;
    private final AccessTokenBlacklist accessTokenBlacklist;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperty.getSecretKey()));
    }

    public String generateAccessToken(String loginId) {
        return generateToken(loginId, "access", jwtProperty.getAccessExp());
    }

    public String createRefreshToken(String loginId) {
        return generateToken(
                loginId,
                "refresh",
                jwtProperty.getRefreshExp()
        );
    }
    public String generateRefreshToken(String loginId) {
        String token = createRefreshToken(loginId);
        refreshTokenStore.save(
                loginId,
                token,
                jwtProperty.getRefreshExp()
        );

        return token;
    }

    private String generateToken(String subject, String type, long expirationMillis) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header().add("type", type).and()
                .subject(subject)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMillis)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(jwtProperty.getHeader());
        if (header == null) {
            return null;
        }

        String prefix = jwtProperty.getPrefix().stripTrailing() + " ";
        if (!header.regionMatches(true, 0, prefix, 0, prefix.length())) {
            throw new BadCredentialsException("Invalid authorization header");
        }

        String token = header.substring(prefix.length());
        if (token.isBlank()) {
            throw new BadCredentialsException("Empty bearer token");
        }
        return token;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseAccessClaims(token);

        if (accessTokenBlacklist.isBlocked(claims.getId())) {
            throw new BadCredentialsException("Logged out access token");
        }

        UserDetails user = authDetailsService.loadUserByUsername(claims.getSubject());

        if (!user.isEnabled() || !user.isAccountNonLocked()
                || !user.isAccountNonExpired() || !user.isCredentialsNonExpired()) {
            throw new BadCredentialsException("Account unavailable");
        }

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }
    public String getAccessTokenId(String token) {
        Claims claims = parseAccessClaims(token);
        return claims.getId();
    }

    public long getAccessTokenRemainingMillis(String token) {
        Claims claims = parseAccessClaims(token);

        return claims.getExpiration().getTime()
                - System.currentTimeMillis();
    }
    private Claims parseAccessClaims(String token) {
        try {
            Jws<Claims> jwt = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            Claims claims = jwt.getPayload();

            if (!"access".equals(jwt.getHeader().get("type"))
                    || claims.getSubject() == null || claims.getSubject().isBlank()
                    || claims.getExpiration() == null
                    || claims.getId() == null || claims.getId().isBlank()) {
                throw new BadCredentialsException("Invalid access token");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid or expired JWT token", e);
        }
    }
    public String getLoginIdFromRefreshToken(String token) {
        try {
            Jws<Claims> jwt = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            Claims claims = jwt.getPayload();

            if (!"refresh".equals(jwt.getHeader().get("type"))
                ||claims.getSubject() == null
                ||claims.getSubject().isBlank()
                ||claims.getExpiration() == null){
                throw new BadCredentialsException("Invalid refresh token");
            }

            return claims.getSubject();

        }catch (JwtException | IllegalArgumentException e){
            throw new BadCredentialsException(
                    "Invalid or expired refresh token", e);
        }
    }
}
