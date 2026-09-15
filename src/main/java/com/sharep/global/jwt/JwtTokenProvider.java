package com.sharep.global.jwt;

import com.sharep.global.auth.AuthDetailsService;
import com.sharep.global.refresh.RefreshToken;
import com.sharep.global.refresh.RefreshTokenRepository;
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
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperty jwtProperty;
    private final AuthDetailsService authDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperty.getSecretKey()));
    }

    public String generateAccessToken(String loginId) {
        return generateToken(loginId, "access", jwtProperty.getAccessExp());
    }

    public String generateRefreshToken(String loginId) {
        String token = generateToken(loginId, "refresh", jwtProperty.getRefreshExp());
        refreshTokenRepository.save(
                RefreshToken.builder()
                        .accountId(loginId)
                        .token(token)
                        .ttl(TimeUnit.MILLISECONDS.toSeconds(jwtProperty.getRefreshExp()))
                        .build()
        );
        return token;
    }

    private String generateToken(String subject, String type, long expirationMillis) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header().add("type", type).and()
                .subject(subject)
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
        UserDetails user = authDetailsService.loadUserByUsername(claims.getSubject());

        if (!user.isEnabled() || !user.isAccountNonLocked()
                || !user.isAccountNonExpired() || !user.isCredentialsNonExpired()) {
            throw new BadCredentialsException("Account unavailable");
        }

        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
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
                    || claims.getExpiration() == null) {
                throw new BadCredentialsException("Invalid access token");
            }
            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid or expired JWT token", e);
        }
    }
}
