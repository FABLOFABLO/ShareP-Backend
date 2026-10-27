package com.sharep.domain.user.domain;

import jakarta.persistence.LockModeType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserConcurrentUpdateTest {

    @Test
    void counterUpdateCannotRestoreOldPasswordOrCredentialStamp() {
        try (Fixture fixture = new Fixture()) {
            String oldStamp = fixture.credentialStamp();

            fixture.changeCredentialsWhileCounterRequestIsOpen(user -> user.changePassword("new-hash"));

            try (Session session = fixture.factory.openSession()) {
                User user = session.find(User.class, fixture.userId);
                assertEquals("new-hash", user.getPassword());
                assertNotEquals(oldStamp, user.getCredentialStamp());
                assertEquals(1L, user.getFollowerCount());
            }
        }
    }

    @Test
    void counterUpdateCannotRestoreOldLoginIdOrCredentialStamp() {
        try (Fixture fixture = new Fixture()) {
            String oldStamp = fixture.credentialStamp();

            fixture.changeCredentialsWhileCounterRequestIsOpen(user -> user.changeLoginId("new-login"));

            try (Session session = fixture.factory.openSession()) {
                User user = session.find(User.class, fixture.userId);
                assertEquals("new-login", user.getLoginId());
                assertNotEquals(oldStamp, user.getCredentialStamp());
                assertEquals(1L, user.getFollowerCount());
            }
        }
    }

    private static final class Fixture implements AutoCloseable {
        private final StandardServiceRegistry registry;
        private final SessionFactory factory;
        private final Long userId;

        private Fixture() {
            registry = new StandardServiceRegistryBuilder()
                    .applySetting("hibernate.connection.driver_class", "org.h2.Driver")
                    .applySetting("hibernate.connection.url", "jdbc:h2:mem:review_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1")
                    .applySetting("hibernate.connection.username", "sa")
                    .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                    .build();
            factory = new MetadataSources(registry)
                    .addAnnotatedClass(User.class)
                    .buildMetadata()
                    .buildSessionFactory();

            try (Session session = factory.openSession()) {
                session.beginTransaction();
                User user = User.builder().loginId("old-login").password("old-hash").build();
                user.changePassword("old-hash");
                session.persist(user);
                session.getTransaction().commit();
                userId = user.getId();
            }
        }

        private String credentialStamp() {
            try (Session session = factory.openSession()) {
                return session.find(User.class, userId).getCredentialStamp();
            }
        }

        private void changeCredentialsWhileCounterRequestIsOpen(Consumer<User> change) {
            try (Session counterRequest = factory.openSession()) {
                counterRequest.beginTransaction();
                User staleUser = counterRequest.find(User.class, userId);

                try (Session accountRequest = factory.openSession()) {
                    accountRequest.beginTransaction();
                    User lockedUser = accountRequest.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
                    change.accept(lockedUser);
                    accountRequest.getTransaction().commit();
                }

                staleUser.FollowerAdd();
                counterRequest.getTransaction().commit();
            }
        }

        @Override
        public void close() {
            factory.close();
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
