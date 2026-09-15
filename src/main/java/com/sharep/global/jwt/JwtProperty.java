package com.sharep.global.jwt;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Validated
@Getter
@AllArgsConstructor
public class JwtProperty {

    @NotBlank
    private final String secretKey;

    // Expiration settings are milliseconds; Redis TTL is converted to seconds.
    @NotNull
    @Positive
    private final Long accessExp;

    @NotNull
    @Min(1000)
    private final Long refreshExp;

    @NotBlank
    private final String header;

    @NotBlank
    private final String prefix;
}
