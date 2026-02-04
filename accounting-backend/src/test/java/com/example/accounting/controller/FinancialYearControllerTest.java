package com.example.accounting.controller;

import com.example.accounting.dto.FinancialYearDto;
import com.example.accounting.exception.ResourceNotFoundException;
import com.example.accounting.security.UserPrincipal;
import com.example.accounting.service.AccountService;
import com.example.accounting.service.FinancialYearService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FinancialYearController.class)
public class FinancialYearControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FinancialYearService financialYearService;

    @MockBean
    private AccountService accountService;

    @BeforeEach
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void create_whenUnauthenticated_returns401() throws Exception {
        FinancialYearDto dto = new FinancialYearDto();
        dto.setYearStart(2024);
        dto.setYearEnd(2025);

        mockMvc.perform(post("/api/v1/accounts/1/years")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"yearStart\":2024,\"yearEnd\":2025}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void create_whenAccountNotOwned_returns404() throws Exception {
        // set authentication with principal id 99
        UserPrincipal principal = new UserPrincipal(99, "test", "pwd");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        // accountService will not return account for this user
        Mockito.when(accountService.findByIdAndUserId(Mockito.eq(1), Mockito.eq(99))).thenReturn(java.util.Optional.empty());

        mockMvc.perform(post("/api/v1/accounts/1/years")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"yearStart\":2024,\"yearEnd\":2025}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
