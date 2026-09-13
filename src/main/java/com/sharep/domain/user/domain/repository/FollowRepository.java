package com.sharep.domain.user.domain.repository;

import com.sharep.domain.user.domain.Follow;
import com.sharep.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByFollowerAndFollowing(User follower, User following);
    List<Follow> findAllByFollower(User follower);
    List<Follow> findAllByFollowing(User following);
}
