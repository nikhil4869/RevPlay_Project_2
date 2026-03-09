package com.example.demo.validation;

import java.time.LocalDate;

public class AgeValidator {

    public static boolean isAdult(LocalDate dob) {
        return dob != null && dob.isBefore(LocalDate.now().minusYears(13).plusDays(1));
    }

    public static boolean isNotFutureDate(LocalDate dob) {
        return dob != null && dob.isBefore(LocalDate.now());
    }
}
