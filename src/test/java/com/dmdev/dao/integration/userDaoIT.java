package com.dmdev.dao.integration;

import com.dmdev.dao.UserDao;
import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.util.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class userDaoIT extends IntegrationTestBase {

    UserDao userDao = UserDao.getInstance();

    @Test
    @DisplayName("Userlist has correct size and content")
    void findAllReturnsAllValues() {
        var actualList = userDao.findAll();

        assertThat(actualList.size()).isEqualTo(Util.getExpectedUsers().size());
        assertThat(actualList).containsAll(Util.getExpectedUsers());
    }

    @Nested
    @DisplayName("Find by ID")
    public class findByIdTest {
        @Test
        @DisplayName("ID is not null, negative or empty")
        void findByIdReturnsNothingByBadId() {
            assertThat(userDao.findById(-1)).isEmpty();
            assertThat(userDao.findById(Integer.MAX_VALUE)).isEmpty();
            assertThat(userDao.findById(null)).isEmpty();
        }

        @Test
        @DisplayName("User with Id=1 is correct")
        void findByIdReturnsProperUser() {
            assertThat(userDao.findById(1)).isEqualTo(Optional.of(Util.getExpectedUserById(1)));
        }
    }

    @Nested
    @DisplayName("Find by Email and Password")
    class FindByEmailAndPassword {
        @Test
        @DisplayName("Empty return if email-password combination doesn't exist")
        void emptyReturnIfBadEmailOrPassword() {
            var expectedUser = Util.getExpectedUserById(1);
            String expectedEmail = expectedUser.getEmail();
            String expectedPassword = expectedUser.getPassword();

            assertThat(userDao.findByEmailAndPassword(null, "dummy")).isEmpty();
            assertThat(userDao.findByEmailAndPassword("dummy", null)).isEmpty();
            assertThat(userDao.findByEmailAndPassword("", expectedPassword)).isEmpty();
            assertThat(userDao.findByEmailAndPassword(expectedEmail, "")).isEmpty();
        }

        @Test
        @DisplayName("Search by email and password works properly")
        void findByEmailAndPasswordReturnsCorrectUser() {
            var expectedUser = Util.getExpectedUserById(1);
            String expectedEmail = expectedUser.getEmail();
            String expectedPassword = expectedUser.getPassword();

            assertThat(userDao.findByEmailAndPassword(expectedEmail, expectedPassword))
                    .isPresent();
            assertThat(userDao.findByEmailAndPassword(expectedEmail, expectedPassword))
                    .isEqualTo(Optional.of(expectedUser));
        }
    }

    @Nested
    @DisplayName("Delete User")
    class DeleteUser {

        @Test
        void deleteUserSuccess() {
            var beforeDeleteList = userDao.findAll();

            Assertions.assertAll(
                    () -> assertThat(beforeDeleteList.size()).isEqualTo(Util.getExpectedUsers().size()),
                    () -> assertThat(beforeDeleteList).containsAll(Util.getExpectedUsers()),
                    () -> assertThat(userDao.findById(1)).isPresent(),
                    () -> assertThat(userDao.delete(1)).isTrue()
            );
            var afterDeleteList = userDao.findAll();

            Assertions.assertAll(
                    () -> assertThat(afterDeleteList.size()).isEqualTo(Util.getExpectedUsers().size() - 1),
                    () -> assertThat(userDao.findById(1)).isEmpty()
            );
        }

        @Test
        void deleteUserFail() {
            var beforeDeleteList = userDao.findAll();

            Assertions.assertAll(
                    () -> assertThat(beforeDeleteList.size()).isEqualTo(Util.getExpectedUsers().size()),
                    () -> assertThat(beforeDeleteList).containsAll(Util.getExpectedUsers()),
                    () -> assertThat(userDao.findById(1)).isPresent(),
                    () -> assertThat(userDao.delete(Integer.MAX_VALUE)).isFalse()
            );

            var afterDeleteList = userDao.findAll();

            assertThat(afterDeleteList.size()).isEqualTo(Util.getExpectedUsers().size());
            assertThat(userDao.findById(1)).isPresent();
        }
    }

    @Test
    void updateUserSuccess() {
        User testUserIvan = User.builder()
                .id(1)
                .name("IvanIvanych")
                .password("111222")
                .role(Role.USER)
                .gender(Gender.FEMALE)
                .birthday(LocalDate.of(1991, 1, 10))
                .email("ivan_IVANYCH@gmail.com")
                .build();

        var actualUserIvan = userDao.findById(testUserIvan.getId());
        assertThat(Optional.of(testUserIvan)).isNotEqualTo(actualUserIvan);

        userDao.update(testUserIvan);
        var updatedUserIvan = userDao.findById(testUserIvan.getId());
        assertThat(Optional.of(testUserIvan)).isEqualTo(updatedUserIvan);
    }

    @Test
    void saveUserSuccess() {
        User testUserIvan = User.builder()
                .id(Integer.MAX_VALUE)
                .name("IvanIvanych")
                .password("111222")
                .role(Role.USER)
                .gender(Gender.FEMALE)
                .birthday(LocalDate.of(1991, 1, 10))
                .email("ivan_IVANYCH@gmail.com")
                .build();

        Assertions.assertAll(
                () -> assertThat(userDao.findById(testUserIvan.getId())).isEmpty(),
                () -> assertThat(userDao.save(testUserIvan)).isEqualTo(testUserIvan),
                () -> assertThat(userDao.findById(testUserIvan.getId())).isPresent()
        );
    }
}


