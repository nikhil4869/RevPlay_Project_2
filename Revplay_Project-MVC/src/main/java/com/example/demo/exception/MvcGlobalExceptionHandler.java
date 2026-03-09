package com.example.demo.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class MvcGlobalExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleClientError(HttpClientErrorException ex,
                                    RedirectAttributes redirectAttributes) {

        String errorMessage = extractMessage(ex.getResponseBodyAsString());

        // If 403 → redirect login
        if (ex.getStatusCode().value() == 403) {
            redirectAttributes.addFlashAttribute("error", "Access denied. Please login.");
            return "redirect:/login";
        }

        // For other errors, we might want to go to a general error page or just back
        redirectAttributes.addFlashAttribute("error", errorMessage);
        return "redirect:/login"; // Defaulting to login is safer than register if unauthorized
    }

    private String extractMessage(String responseBody) {

        if (responseBody == null || responseBody.isEmpty()) {
            return "Invalid credentials";
        }

        // Try to extract message between quotes
        int messageIndex = responseBody.indexOf("\"message\"");
        if (messageIndex != -1) {
            int start = responseBody.indexOf(":", messageIndex) + 1;
            int firstQuote = responseBody.indexOf("\"", start);
            int secondQuote = responseBody.indexOf("\"", firstQuote + 1);

            if (firstQuote != -1 && secondQuote != -1) {
                return responseBody.substring(firstQuote + 1, secondQuote);
            }
        }

        // If backend returns plain text
        return responseBody.replace("\"", "");
    }
}