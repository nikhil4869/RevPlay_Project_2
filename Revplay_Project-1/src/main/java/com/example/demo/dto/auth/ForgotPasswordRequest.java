package com.example.demo.dto.auth;

public class ForgotPasswordRequest {

    private String email;
    private String dateOfBirth;
    private String newPassword;

    public ForgotPasswordRequest() {}

    public String getEmail() {
        return email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
