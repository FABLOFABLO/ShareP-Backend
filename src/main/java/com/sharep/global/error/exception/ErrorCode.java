package com.sharep.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    PROMPT_NOT_FOUND(404, "해당 프롬프트 게시글을 찾을 수 없습니다."),
    SIGNUP_NOT_FOUND(409, "회원가입을 할 수 없습니다."),
    USERID_NOT_FOUND(404, "해당 유저아이디를 가진 유저를 찾을 수 없습니다."),
    ALREADY_LIKED(409, "이미 해당 게시글에 좋아요를 눌렀습니다."),
    ALREADY_UNLIKED(409, "이미 해당 게시글에 좋아요를 취소했습니다."),
    ALREADY_FOLLOWED(409, "이미 해당 유저를 팔로우하고 있습니다."),
    ALREADY_UNFOLLOWED(409, "이미 해당 유저 팔로우를 취소했습니다."),
    FOLLOWER_NOT_FOUND(404, "해당 유저를 팔로워하는 사람을 찾을 수 없습니다."),
    FOLLOWING_NOT_FOUND(404, "해당 유저가 팔로잉하는 사람을 찾을 수 없습니다.");

    private final Integer errorCode;
    private final String message;
}
