package com.example.demo.util;

import java.time.LocalDate;
import java.time.Period;

public class DateUtil {

    public static int calculateAge(LocalDate dob) {
        return Period.between(dob, LocalDate.now()).getYears();
    }
}
