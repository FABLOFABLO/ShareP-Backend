package com.sharep.domain.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true)
    private String loginId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "prompt_count", nullable = false)
    private Long promptCount;

    @Column(name = "follower_count", nullable = false)
    private Long followerCount;

    @Column(name = "following_count", nullable = false)
    private Long followingCount;

    @Builder
    private User(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = "프롬프트 마스터";
        this.promptCount = 0L;
        this.followerCount = 0L;
        this.followingCount = 0L;
    }

    public void PromptAdd() {
        this.promptCount++;
    }

    public void PromptDelete() {
        this.promptCount--;
    }

    public void FollowerAdd() {
        this.followerCount++;
    }

    public void FollowerDelete() {
        this.followerCount--;
    }

    public void FollowingAdd() {
        this.followingCount++;
    }

    public void FollowingDelete() {
        this.followingCount--;
    }
}