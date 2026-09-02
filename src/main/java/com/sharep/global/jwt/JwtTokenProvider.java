package com.sharep.global.jwt;


import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ser.impl.UnknownSerializer;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperty jwtProperty;
    private final AuthDetailsService authDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperty.getSecretKey());
        this.key = keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String accountId) {
        return generateToken(accountId, "access", jwtProperty.getAccessExp());
    }

    public String generateRefreshToken(String accountId) {
        String refreshToken =
                generateToken(accountId, "refresh", jwtProperty.getRefreshExp());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .accountId(accountId)
                        .token(refreshToken)
                        .ttl(jwtProperty.getRefreshExp())
                        .build()
        );

        return refreshToken;
    }

    private String generateToken(String subject, String type, Long exp) {
        return Jwts.builder()
                .setSubject(subject)
                .setHeaderParam("type", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(jwtProperty.getHeader());

        if (bearer != null && bearer.startsWith(jwtProperty.getPrefix())) {
            return bearer.substring(7);
        }
        return null;
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails =
                authDetailsService.loadUserByUsername(getSubject(token));

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("Expired JWT token");
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid JWT token");
        }
    }

    private String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
