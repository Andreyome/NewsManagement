package com.mjc.school.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserSignIn(@Size(min = 5, max = 50, message = "Username should be between 5 and 50 characters long.")
                        @NotBlank
                        String username,
                        @Size(min = 5, max = 50, message = "Password should be between 5 and 50 characters long.")
                        @NotBlank
                        String password) {
}
