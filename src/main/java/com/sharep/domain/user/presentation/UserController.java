package com.sharep.domain.user.presentation;

import com.sharep.domain.user.presentation.dto.request.LoginRequest;
import com.sharep.domain.user.presentation.dto.request.UserSignupRequest;
import com.sharep.domain.user.presentation.dto.response.LoginResponse;
import com.sharep.domain.user.service.LoginService;
import com.sharep.domain.user.service.LogoutService;
import com.sharep.domain.user.service.UserSignupService;
import com.sharep.global.auth.AuthDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserSignupService userSignupService;
    private final LoginService loginService;
    private final LogoutService logoutService;

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@AuthenticationPrincipal AuthDetails currentUser) {
        logoutService.logout(currentUser.getUsername());
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody UserSignupRequest request){
        userSignupService.signUp(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request){
        return loginService.login(request);
    }

}
