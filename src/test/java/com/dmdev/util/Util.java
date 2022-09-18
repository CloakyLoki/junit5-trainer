package com.dmdev.util;

import com.dmdev.entity.Gender;
import com.dmdev.entity.Role;
import com.dmdev.entity.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class Util {

    static User userIvan = User.builder()
            .id(1)
            .name("Ivan")
            .password("111")
            .role(Role.ADMIN)
            .gender(Gender.MALE)
            .birthday(LocalDate.of(1990, 1, 10))
            .email("ivan@gmail.com")
            .build();

    static User userPetr = User.builder()
            .id(2)
            .name("Petr")
            .password("123")
            .role(Role.USER)
            .gender(Gender.MALE)
            .birthday(LocalDate.of(1995, 10, 19))
            .email("petr@gmail.com")
            .build();

    static User userSveta = User.builder()
            .id(3)
            .name("Sveta")
            .password("321")
            .role(Role.USER)
            .gender(Gender.FEMALE)
            .birthday(LocalDate.of(2001, 12, 23))
            .email("sveta@gmail.com")
            .build();

    static User userVlad = User.builder()
            .id(4)
            .name("Vlad")
            .password("456")
            .role(Role.USER)
            .gender(Gender.MALE)
            .birthday(LocalDate.of(1984, 3, 14))
            .email("vlad@gmail.com")
            .build();

    static User userKate = User.builder()
            .id(5)
            .name("Kate")
            .password("777")
            .role(Role.ADMIN)
            .gender(Gender.FEMALE)
            .birthday(LocalDate.of(1989, 8, 9))
            .email("kate@gmail.com")
            .build();

    static Map<Integer, User> userById = Map.of(
            userIvan.getId(), userIvan,
            userPetr.getId(), userPetr,
            userSveta.getId(), userSveta,
            userVlad.getId(), userVlad,
            userKate.getId(), userKate

    );

    public static Collection<User> getExpectedUsers() {
        return userById.values();
    }

    public static User getExpectedUserById(Integer id) {
        return userById.get(id);
    }
}
