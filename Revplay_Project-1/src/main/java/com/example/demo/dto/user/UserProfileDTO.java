package com.example.demo.dto.user;

import java.time.LocalDate;

public class UserProfileDTO {

    private Long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private String role;

    public UserProfileDTO(Long id, String name, String email,
                          LocalDate dateOfBirth, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getRole() { return role; }
}