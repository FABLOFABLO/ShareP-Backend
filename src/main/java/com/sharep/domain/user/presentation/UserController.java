package com.sharep.domain.user.presentation;

import com.sharep.domain.user.presentation.dto.request.LoginRequest;
import com.sharep.domain.user.presentation.dto.request.LoginIdChangeRequest;
import com.sharep.domain.user.presentation.dto.request.NicknameChangeRequest;
import com.sharep.domain.user.presentation.dto.request.PasswordChangeRequest;
import com.sharep.domain.user.presentation.dto.response.*;
import com.sharep.domain.user.service.*;
import com.sharep.domain.user.presentation.dto.request.TokenReissueRequest;
import com.sharep.domain.user.presentation.dto.request.UserSignupRequest;
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
    private final MyPageService myPageService;
    private final UserProfileReadService userProfileReadService;
    private final LoginIdChangeService loginIdChangeService;
    private final PasswordChangeService passwordChangeService;
    private final NicknameChangeService nicknameChangeService;

    @PatchMapping("/nickname")
    @ResponseStatus(HttpStatus.CREATED)
    public void changeNickname(@AuthenticationPrincipal AuthDetails currentUser,
                                                 @Valid @RequestBody NicknameChangeRequest request) {
        nicknameChangeService.execute(currentUser.getUser(), request);
    }

    @PatchMapping("/id")
    @ResponseStatus(HttpStatus.CREATED)
    public void changeLoginId(@AuthenticationPrincipal AuthDetails currentUser,
                                               @Valid @RequestBody LoginIdChangeRequest request) {
        loginIdChangeService.execute(currentUser.getUser(), request);
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.CREATED)
    public void changePassword(@AuthenticationPrincipal AuthDetails currentUser,
                               @Valid @RequestBody PasswordChangeRequest request) {
        passwordChangeService.execute(currentUser.getUser(), request);
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public LoginResponse reissue(@Valid @RequestBody TokenReissueRequest request) {
        return tokenReissueService.execute(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@AuthenticationPrincipal AuthDetails currentUser,
                       Authentication authentication) {
        logoutService.execute(
                currentUser.getUsername(),
                (AccessTokenDetails) authentication.getDetails()
        );
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Valid @RequestBody UserSignupRequest request) {
        userSignupService.execute(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return loginService.execute(request);
    }

    @GetMapping("/mypage")
    @ResponseStatus(HttpStatus.OK)
    public MyPageResponse readMyPage(@AuthenticationPrincipal AuthDetails currentUser) {
        return myPageService.execute(currentUser.getUser().getId());
    }

    @GetMapping("/{user-id}")
    @ResponseStatus(HttpStatus.OK)
    public UserProfileResponse readProfile(@AuthenticationPrincipal AuthDetails currentUser,
                                           @PathVariable("user-id") Long userId) {
        return userProfileReadService.execute(currentUser.getUser().getId(), userId);
    }
}
