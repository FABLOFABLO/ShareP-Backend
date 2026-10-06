package com.sharep.domain.follow.domain.repository;

import com.sharep.domain.follow.domain.Follow;
import org.springframework.data.repository.Repository;

public interface FollowRepository extends Repository<Follow, Long> {

    long countByFollowee_Id(Long userId);

    long countByFollower_Id(Long userId);

    boolean existsByFollower_IdAndFollowee_Id(Long followerId, Long followeeId);
}
