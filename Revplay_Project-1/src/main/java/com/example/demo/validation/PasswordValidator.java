package com.example.demo.validation;

public class PasswordValidator {

    public static boolean isStrong(String password) {
        return password != null
                && password.length() >= 6
                && password.matches(".*[A-Z].*")
                && password.matches(".*[0-9].*");
    }
}
