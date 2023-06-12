package nl.inholland.bankingapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.AccountGET_DTO;
import nl.inholland.bankingapi.model.dto.AccountPUT_DTO;
import nl.inholland.bankingapi.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
@ContextConfiguration(classes = {AccountController.class})
@EnableMethodSecurity(prePostEnabled = true)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;
    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void getAllAccounts() throws Exception {
        when(accountService.getAllAccounts(null, null, null, null, null, null, null, null))
                .thenReturn(List.of(
                        new AccountGET_DTO(1, 1, "NL21INHO0123400081", 100.00, 0.0, AccountType.SAVINGS, true)
                ));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                        .with(user("employee@email.com").password("1234").roles("EMPLOYEE"))) // Provide authentication details
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].accountId", is(1)));
    }

    @Test
    void shouldReturnUnauthorizedWithInvalidTokenForGetAccountById() throws Exception {
        when(accountService.getAccountById(1))
                .thenReturn((
                        new AccountGET_DTO(1, 1, "NL21INHO0123400081", 100.00, 0.0, AccountType.SAVINGS, true)
                ));
        String accountId = "1";
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/accounts/" + accountId)) // Use a wrong role
                .andExpect(status().isUnauthorized()) // Expect a 401 Forbidden status code
                .andDo(print());
    }

    @Test
    void shouldReturnForbiddenToGetAccountByIdWithNotEmployeeRoleForGetAccountById() throws Exception {
        when(accountService.getAccountById(1))
                .thenReturn((
                        new AccountGET_DTO(1, 1, "NL21INHO0123400081", 100.00, 0.0, AccountType.SAVINGS, true)
                ));
        String accountId = "1";
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/accounts/" + accountId)
                                .with(user("customer@email.com").password("1234").roles("CUSTOMER"))) // Use a wrong role
                .andExpect(status().isForbidden()) // Expect a 401 Forbidden status code
                .andDo(print());
    }

    @Test
    void shouldReturnOkToGetAccountByIdWithEmployeeRoleForGetAccountById() throws Exception {
        when(accountService.getAccountById(1))
                .thenReturn((
                        new AccountGET_DTO(1, 1, "NL21INHO0123400081", 100.00, 0.0, AccountType.SAVINGS, true)
                ));
        String accountId = "1";
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/accounts/" + accountId)
                                .with(user("customer@email.com").password("1234").roles("EMPLOYEE"))) // Use a wrong role
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").exists())
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundToGetAccountByIdWithEmployeeRoleForGetAccountById() throws Exception {
        when(accountService.getAccountById(1))
                .thenReturn((
                        new AccountGET_DTO(1, 1, "NL21INHO0123400081", 100.00, 0.0, AccountType.SAVINGS, true)
                ));
        when(accountService.getAccountById(2))
                .thenThrow(new EntityNotFoundException("Account not found"));
        String accountId = "2";
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/accounts/" + accountId)
                                .with(user("customer@email.com").password("1234").roles("EMPLOYEE")))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void ShouldCreateUser() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"iban\":\"NL21INHO0123400081\",\"balance\":100.00,\"absoluteLimit\":0.0,\"accountType\":\"SAVINGS\",\"active\":true}")
                        .with(csrf())
                        .with(user("employee@email.com").password("1234").roles("EMPLOYEE")))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void ShouldReturnForbiddenWithNotEmployeeForCreateUser() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"iban\":\"NL21INHO0123400081\",\"balance\":100.00,\"absoluteLimit\":0.0,\"accountType\":\"SAVINGS\",\"active\":true}")
                        .with(csrf())
                        .with(user("employee@email.com").password("1234").roles("CUSTOMER")))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void shouldReturnOkForPutAccount() throws Exception {
        AccountPUT_DTO account = new AccountPUT_DTO(300.00, true);
        Account modifiedAccount = new Account(new User(), "NL21INHO0123400081", 300.00, account.absoluteLimit(), AccountType.SAVINGS, true);

        when(accountService.disableAccount(1, account)).thenReturn(modifiedAccount);

        String accountId = "1";

        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account))
                        .with(csrf())
                        .with(user("employee@email.com").password("1234").roles("EMPLOYEE")))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldReturnForbiddenWithNotEmployeeForPutAccount() throws Exception {
        AccountPUT_DTO account = new AccountPUT_DTO(300.00, true);
        Account modifiedAccount = new Account(new User(), "NL21INHO0123400081", 300.00, account.absoluteLimit(), AccountType.SAVINGS, true);

        when(accountService.disableAccount(1, account)).thenReturn(modifiedAccount);

        String accountId = "1";

        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/" + accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(account))
                        .with(csrf())
                        .with(user("employee@email.com").password("1234").roles("CUSTOMER")))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}

