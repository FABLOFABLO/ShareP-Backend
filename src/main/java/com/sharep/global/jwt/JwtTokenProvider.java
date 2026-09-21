package com.sharep.global.jwt;

import com.sharep.global.auth.AuthDetailsService;
import com.sharep.global.auth.AuthDetails;
import com.sharep.domain.user.domain.User;
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
import java.util.Objects;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

    public String generateAccessToken(User user) {
        return generateToken(user, "access", jwtProperty.getAccessExp());
    }

    public String createRefreshToken(User user) {
        return generateToken(
                user,
                "refresh",
                jwtProperty.getRefreshExp()
        );
    }
    public String generateRefreshToken(User user) {
        String token = createRefreshToken(user);
        refreshTokenStore.save(
                user.getLoginId(),
                token,
                jwtProperty.getRefreshExp()
        );

        return token;
    }

    private String generateToken(User user, String type, long expirationMillis) {
        Instant now = Instant.now();
        return Jwts.builder()
                .header().add("type", type).and()
                .subject(user.getLoginId())
                .claim("uid", user.getId().toString())
                .claim("credentials", Objects.toString(user.getCredentialStamp(), ""))
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

        UserDetails user = validateCurrentUser(claims);

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

            return validateCurrentUser(claims).getUsername();

        }catch (JwtException | IllegalArgumentException e){
            throw new BadCredentialsException(
                    "Invalid or expired refresh token", e);
        }
    }

    private AuthDetails validateCurrentUser(Claims claims) {
        AuthDetails user;
        try {
            user = authDetailsService.loadUserByUsername(claims.getSubject());
        } catch (UsernameNotFoundException exception) {
            throw new BadCredentialsException("Account unavailable", exception);
        }
        if (!user.getUser().getId().toString().equals(claims.get("uid", String.class))
                || !Objects.toString(user.getUser().getCredentialStamp(), "")
                .equals(claims.get("credentials", String.class))) {
            throw new BadCredentialsException("Credentials changed");
        }
        return user;
    }
}
