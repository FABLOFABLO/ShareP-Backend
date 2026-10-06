package com.sharep.domain.user.presentation;

import com.sharep.domain.user.presentation.dto.response.MyPageResponse;
import com.sharep.domain.user.presentation.dto.response.UserProfileResponse;
import com.sharep.domain.user.service.UserPageReadService;
import com.sharep.global.auth.AuthDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserPageController {

    private final UserPageReadService userPageReadService;

    @GetMapping("/mypage")
    public MyPageResponse readMyPage(@AuthenticationPrincipal AuthDetails currentUser) {
        return userPageReadService.readMyPage(currentUser.getUser().getId());
    }

    @GetMapping("/{user-id}")
    public UserProfileResponse readProfile(@AuthenticationPrincipal AuthDetails currentUser,
                                           @PathVariable("user-id") Long userId) {
        return userPageReadService.readProfile(currentUser.getUser().getId(), userId);
    }
}
