package com.example.accounting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountDto {
    private Integer id;

    private Integer userId; // optional - server will use authenticated user when creating

    @NotBlank
    private String accountName;
}
