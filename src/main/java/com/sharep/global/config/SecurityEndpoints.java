package com.sharep.global.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class SecurityEndpoints {
    public static final RequestMatcher PUBLIC_ENDPOINTS = new OrRequestMatcher(
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/user/signup"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/user/login"),
            PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/user/reissue")
    );

    private SecurityEndpoints() {
    }
}
