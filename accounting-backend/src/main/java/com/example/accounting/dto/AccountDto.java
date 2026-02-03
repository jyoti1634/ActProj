package com.example.accounting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountDto {
    private Integer id;

    @NotNull
    private Integer userId;

    @NotBlank
    private String accountName;
}
