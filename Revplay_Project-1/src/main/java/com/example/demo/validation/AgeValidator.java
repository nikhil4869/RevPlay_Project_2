package com.example.demo.validation;

import java.time.LocalDate;

public class AgeValidator {

    public static boolean isAdult(LocalDate dob) {
        return dob != null && dob.isBefore(LocalDate.now().minusYears(13));
    }
}
