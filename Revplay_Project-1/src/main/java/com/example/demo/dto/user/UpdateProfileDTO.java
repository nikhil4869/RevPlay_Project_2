package com.example.demo.dto.user;

import java.time.LocalDate;

public class UpdateProfileDTO {

    private String name;
    private LocalDate dateOfBirth;

    public String getName() { return name; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
}