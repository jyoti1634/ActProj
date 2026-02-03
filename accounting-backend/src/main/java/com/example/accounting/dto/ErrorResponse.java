package com.example.accounting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple error response used by authentication endpoints for clear reason codes.
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String reason;
    private String message;
}
