package com.dmdev.service.integration;

import com.dmdev.dao.UserDao;
import com.dmdev.dto.CreateUserDto;
import com.dmdev.exception.ValidationException;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.UserMapper;
import com.dmdev.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserServiceIT extends IntegrationTestBase {

    UserService userService = UserService.getInstance();
    UserDao userDao = UserDao.getInstance();
    UserMapper mapper = UserMapper.getInstance();

    static Stream<CreateUserDto> userDataGenerator() {
        var createUserDto = CreateUserDto.builder()
                .name("userName")
                .role("USER")
                .gender("MALE")
                .email("userEmail")
                .password("userPassword")
                .birthday("1985-01-07")
                .build();

        var createAdminDto = CreateUserDto.builder()
                .name("adminName")
                .role("ADMIN")
                .gender("FEMALE")
                .email("adminEmail")
                .password("adminPassword")
                .birthday("1987-07-07")
                .build();

        return Stream.of(createUserDto, createAdminDto);
    }

    @Nested
    @DisplayName("New user creation test")
    public class createTest {

        @ParameterizedTest
        @MethodSource("com.dmdev.service.integration.UserServiceIT#userDataGenerator")
        @DisplayName("Create new DB record and return dto with actual data")
        void create(CreateUserDto createdDto) {
            var actualDto = userService.create(createdDto);
            var dbUserOptional = userDao.findByEmailAndPassword(createdDto.getEmail(), createdDto.getPassword());

            assertThat(dbUserOptional).isPresent();

            var user = dbUserOptional.get();
            var expectedDto = mapper.map(user);

            assertThat(actualDto).isEqualTo(expectedDto);
        }

        @Test
        @DisplayName("Error, if date format is different from (yyyy-MM-dd)")
        void createFailIfBirthdayDateBadFormat() {
            var createdDto = CreateUserDto.builder()
                    .name("userName")
                    .role("USER")
                    .gender("MALE")
                    .email("userEmail")
                    .password("userPassword")
                    .birthday("1985.01.07")
                    .build();

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> userService.create(createdDto));
            var errorsList = exception.getErrors();
            assertEquals(1, errorsList.size());

            var actualErrorCode = errorsList.get(0).getCode();
            assertEquals("invalid.birthday", actualErrorCode);

            var actualErrorMessage = errorsList.get(0).getMessage();
            assertEquals("Birthday is invalid", actualErrorMessage);
        }

        @Test
        @DisplayName("Error, if GENDER is incorrect")
        void createFailIfGenderIsIncorrect() {
            var createdDto = CreateUserDto.builder()
                    .name("userName")
                    .role("USER")
                    .gender("dummy")
                    .email("userEmail")
                    .password("userPassword")
                    .birthday("1985-01-07")
                    .build();

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> userService.create(createdDto));
            var errorsList = exception.getErrors();
            assertEquals(1, errorsList.size());

            var actualErrorCode = errorsList.get(0).getCode();
            assertEquals("invalid.gender", actualErrorCode);

            var actualErrorMessage = errorsList.get(0).getMessage();
            assertEquals("Gender is invalid", actualErrorMessage);
        }

        @Test
        @DisplayName("Error, if ROLE is incorrect")
        void createFailIfRoleIsIncorrect() {
            var createdDto = CreateUserDto.builder()
                    .name("userName")
                    .role("dummy")
                    .gender("FEMALE")
                    .email("userEmail")
                    .password("userPassword")
                    .birthday("1985-01-07")
                    .build();

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> userService.create(createdDto));
            var errorsList = exception.getErrors();
            assertEquals(1, errorsList.size());

            var actualErrorCode = errorsList.get(0).getCode();
            assertEquals("invalid.role", actualErrorCode);

            var actualErrorMessage = errorsList.get(0).getMessage();
            assertEquals("Role is invalid", actualErrorMessage);
        }
    }

    @Nested
    @DisplayName("Login test")
    public class loginTest {

        @Test
        @DisplayName("Successful login with correct userdata")
        void loginSuccess() {
            String email = "ivan@gmail.com";
            String password = "111";
            var maybeUser = userDao.findByEmailAndPassword(email, password);

            assertThat(maybeUser).isPresent();

            var expectedUser = maybeUser.get();
            var expectedUserDto = mapper.map(expectedUser);
            var maybeActualUserDto = userService.login(email, password);
            var actualUserDto = maybeActualUserDto.get();

            assertThat(expectedUserDto).isEqualTo(actualUserDto);
        }

        @Test
        @DisplayName("Login failed with absent email")
        void loginFailIfEmailDoesntExist() {
            String email = "dummy";
            String password = "111";

            var maybeUser = userDao.findByEmailAndPassword(email, password);
            assertThat(maybeUser).isEmpty();

            var maybeActualUserDto = userService.login(email, password);
            assertThat(maybeActualUserDto).isEmpty();
        }

        @Test
        @DisplayName("Login failed with incorrect password")
        void loginFailIfPasswordIsWrong() {
            String email = "ivan@gmail.com";
            String password = "dummy";

            var maybeUser = userDao.findByEmailAndPassword(email, password);
            assertThat(maybeUser).isEmpty();

            var maybeActualUserDto = userService.login(email, password);
            assertThat(maybeActualUserDto).isEmpty();
        }

        @Test
        @DisplayName("Login failed if email and password do not exist")
        void loginFailIfEmailAndPasswordDoesntExist() {
            String email = "dummy";
            String password = "dummy";

            var maybeUser = userDao.findByEmailAndPassword(email, password);
            assertThat(maybeUser).isEmpty();

            var maybeActualUserDto = userService.login(email, password);
            assertThat(maybeActualUserDto).isEmpty();
        }
    }
}
