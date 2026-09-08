package com.sharep.security;

import com.sharep.domain.user.domain.User;
import com.sharep.domain.user.domain.repository.UserRepository;
import com.sharep.domain.user.presentation.dto.request.UserSignupRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class SignupValidationTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @ParameterizedTest
    @CsvSource({
            "abcde,Abcdefgh!,true",
            "abcd,Abcdefgh!,false",
            "abcde12345678901,Abcdefgh!,false",
            "abcdefghijklmnopqrstu,Abcdefgh!,false",
            "abcde,Abcdefg!,false",
            "abcde,Abcdefgh1!,false",
            "abcde,Abcdefgh,false",
            "abcde_123,Abcdefgh!,false"
    })
    void preservesSignupCharacterPolicy(String loginId, String password, boolean valid) {
        assertThat(validator.validate(request(loginId, password)).isEmpty()).isEqualTo(valid);
    }

    @Test
    void maximumAllowedInputValidatesWithoutBacktrackingDelay() {
        UserSignupRequest request = request("a".repeat(20) + "1".repeat(10),
                "a".repeat(30) + "!".repeat(20));
        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            for (int i = 0; i < 100; i++) {
                assertThat(validator.validate(request)).isEmpty();
            }
        });
    }

    @Test
    void tooManySpecialCharactersAndMissingFieldsAreRejected() {
        assertThat(validator.validate(request("tester", "a".repeat(8) + "!".repeat(21))))
                .isNotEmpty();
        assertThat(validator.validate(request(null, null))).isNotEmpty();
    }

    @Test
    void encodedPasswordPassesEntityValidation() {
        String hash = new BCryptPasswordEncoder().encode("Abcdefgh!");
        User user = User.builder().loginId("tester").password(hash).build();
        assertThat(hash).hasSize(60);
        assertThat(validator.validate(user)).isEmpty();
    }

    @Test
    void allDeclaredUserRepositoryQueriesCanBeParsed() {
        for (Method method : UserRepository.class.getDeclaredMethods()) {
            new PartTree(method.getName(), User.class);
        }
    }

    private UserSignupRequest request(String loginId, String password) {
        UserSignupRequest request = new UserSignupRequest();
        ReflectionTestUtils.setField(request, "loginId", loginId);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }
}

