package com.sharep.domain.user.presentation;

import com.sharep.domain.user.presentation.dto.request.LoginRequest;
import com.sharep.domain.user.presentation.dto.request.LoginIdChangeRequest;
import com.sharep.domain.user.presentation.dto.request.NicknameChangeRequest;
import com.sharep.domain.user.presentation.dto.request.PasswordChangeRequest;
import com.sharep.domain.user.presentation.dto.response.LoginIdChangeResponse;
import com.sharep.domain.user.presentation.dto.response.NicknameChangeResponse;
import com.sharep.domain.user.service.UserAccountService;
import com.sharep.domain.user.presentation.dto.request.TokenReissueRequest;
import com.sharep.domain.user.presentation.dto.request.UserSignupRequest;
import com.sharep.domain.user.presentation.dto.response.LoginResponse;
import com.sharep.domain.user.service.LoginService;
import com.sharep.domain.user.service.LogoutService;
import com.sharep.domain.user.service.TokenReissueService;
import com.sharep.domain.user.service.UserSignupService;
import com.sharep.global.auth.AuthDetails;
import com.sharep.global.jwt.AccessTokenDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserSignupService userSignupService;
    private final LoginService loginService;
    private final LogoutService logoutService;
    private final TokenReissueService tokenReissueService;
    private final UserAccountService userAccountService;

    @PatchMapping("/nickname")
    public NicknameChangeResponse changeNickname(@AuthenticationPrincipal AuthDetails currentUser,
                                                 @Valid @RequestBody NicknameChangeRequest request) {
        return userAccountService.changeNickname(currentUser.getUser(), request);
    }

    @PatchMapping("/id")
    public LoginIdChangeResponse changeLoginId(@AuthenticationPrincipal AuthDetails currentUser,
                                               @Valid @RequestBody LoginIdChangeRequest request) {
        return userAccountService.changeLoginId(currentUser.getUser(), request);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@AuthenticationPrincipal AuthDetails currentUser,
                               @Valid @RequestBody PasswordChangeRequest request) {
        userAccountService.changePassword(currentUser.getUser(), request);
    }

    @PostMapping("/reissue")
    public LoginResponse reissue(@Valid @RequestBody TokenReissueRequest request) {
        return tokenReissueService.reissue(request);
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal AuthDetails currentUser,
                       Authentication authentication) {
        logoutService.logout(
                currentUser.getUsername(),
                (AccessTokenDetails) authentication.getDetails()
        );
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody UserSignupRequest request) {
        userSignupService.signUp(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return loginService.login(request);
    }

}
