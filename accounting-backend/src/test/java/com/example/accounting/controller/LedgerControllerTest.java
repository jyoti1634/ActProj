package com.example.accounting.controller;

import com.example.accounting.dto.LedgerDto;
import com.example.accounting.security.UserPrincipal;
import com.example.accounting.service.LedgerService;
import com.example.accounting.service.MonthService;
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

@WebMvcTest(LedgerController.class)
public class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LedgerService ledgerService;

    @MockBean
    private MonthService monthService;

    @BeforeEach
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void create_whenUnauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/v1/months/1/ledger")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"particularCsh\":\"Test\",\"cashAmt\":100}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void create_whenMonthNotOwned_returns404() throws Exception {
        UserPrincipal principal = new UserPrincipal(99, "test", "pwd");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        Mockito.when(monthService.findByIdAndYearAccountUserId(Mockito.eq(1), Mockito.eq(99))).thenReturn(java.util.Optional.empty());

        mockMvc.perform(post("/api/v1/months/1/ledger")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"particularCsh\":\"Test\",\"cashAmt\":100}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
