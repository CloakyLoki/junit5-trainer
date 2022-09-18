package com.dmdev.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LocalDateFormatterTest {

    @Test
    @DisplayName("Date Format must be like yyyy-MM-dd")
    void throwExceptionIfBadDateFormat() {
        String badTestDate = "18.09.2022";
        String goodTestDate = "2022-09-18";

        Assertions.assertFalse(LocalDateFormatter.isValid(badTestDate));
        Assertions.assertTrue(LocalDateFormatter.isValid(goodTestDate));
    }
}
