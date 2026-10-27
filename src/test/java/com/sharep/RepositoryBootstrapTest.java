package com.sharep;

import com.sharep.domain.follow.domain.repository.FollowRepository;
import com.sharep.domain.prompt.domain.repository.PromptLikeRepository;
import com.sharep.domain.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:repository_bootstrap;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class RepositoryBootstrapTest {

    @Autowired
    private FollowRepository follows;

    @Autowired
    private PromptLikeRepository likes;

    @Autowired
    private UserRepository users;

    @Test
    void derivedAndNamedQueriesAreUsable() {
        assertEquals(0L, follows.countByFollowing_Id(-1L));
        assertFalse(follows.existsByFollower_IdAndFollowing_Id(-1L, -2L));
        assertTrue(likes.findLikedPromptsByUserId(-1L).isEmpty());
        assertTrue(users.findByIdForUpdate(-1L).isEmpty());
    }
}
