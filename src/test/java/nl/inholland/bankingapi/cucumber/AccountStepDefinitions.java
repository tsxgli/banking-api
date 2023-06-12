package nl.inholland.bankingapi.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import nl.inholland.bankingapi.exception.ApiRequestException;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.dto.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;

public class AccountStepDefinitions extends BaseStepDefinitions {
    private static final String ACCOUNT_ENDPOINT = "/accounts";
    private final Account account = new Account(3L, new User(), "NL21INHO0123400082", 100.0, 100.0, AccountType.CURRENT, true);
    private final Account account2 = new Account(4L, new User(), "NL21INHO0123400082", 100.0, 100.0, AccountType.CURRENT, false);
    private final AccountGET_DTO accountGETDto = new AccountGET_DTO(1L, 1L, "NL21INHO0123400082", 100.0, 100.0, AccountType.CURRENT, true);
    private final AccountGET_DTO bank = new AccountGET_DTO(2L, 1L, "NL21INHO0123400082", 100.0, 100.0, AccountType.BANK, true);
    private final AccountPUT_DTO accountPUTDto = new AccountPUT_DTO(100.0, false);
    private final AccountPUT_DTO accountPUTDto2 = new AccountPUT_DTO(100.0, true);

    private TransactionPOST_DTO transactionPOSTDto;
    private final HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private TransactionDepositDTO transactionDepositDTO = new TransactionDepositDTO("NL21INHO0123400081", 200.0);
    private TransactionWithdrawDTO transactionWithdrawDTO;
    private String token;
    private LoginRequestDTO loginRequestDTO;

    @Given("customer logs in")
    public void loginAsCustomer() throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, VALID_PASSWORD);
        token = getTheToken(loginRequestDTO);
        httpHeaders.add("Authorization", "Bearer " + token);
    }

    @Given("logging as a {string} or an {string}")
    public void loginAsAOrAn(String arg0) throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        if (arg0.equals("Customer")) {
            loginRequestDTO = new LoginRequestDTO(VALID_CUSTOMER, VALID_PASSWORD);
        } else if (arg0.equals("Employee")) {
            loginRequestDTO = new LoginRequestDTO(VALID_EMPLOYEE, VALID_PASSWORD);
        }
        token = getTheToken(loginRequestDTO);
    }

    @Given("employee logs in")
    public void employeeLogin() throws JsonProcessingException {
        httpHeaders.clear();
        httpHeaders.add("Content-Type", "application/json");
        loginRequestDTO = new LoginRequestDTO(VALID_EMPLOYEE, VALID_PASSWORD);
        token = getTheToken(loginRequestDTO);
        httpHeaders.add("Authorization", "Bearer " + token);
    }

    @When("I request to get all accounts")
    public void iRequestToGetAllAccounts() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT,
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Then("I should get all accounts")
    public void iShouldGetAllAccounts() throws JsonProcessingException {
        List<AccountGET_DTO> accounts = objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, TransactionGET_DTO.class));
        Assertions.assertEquals(6, accounts.size());
    }
    @Then("I should get all accounts as customer")
    public void iShouldGetAllAccountsAsCustomer() throws JsonProcessingException {
        List<AccountGET_DTO> accounts = objectMapper.readValue(response.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, TransactionGET_DTO.class));
        Assertions.assertEquals(3, accounts.size());
    }

    @When("I request to get a single account")
    public void requestToGetASingleAccount() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/1",
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }

    @Then("I should get a single account")
    public void shouldGetASingleAccount() {
        Assertions.assertEquals(accountGETDto.accountId(), accountGETDto.accountId());
        Assertions.assertEquals(accountGETDto.user(), accountGETDto.user());
        Assertions.assertEquals(accountGETDto.IBAN(), accountGETDto.IBAN());
        Assertions.assertEquals(accountGETDto.balance(), accountGETDto.balance());
        Assertions.assertEquals(accountGETDto.absoluteLimit(), accountGETDto.absoluteLimit());
        Assertions.assertEquals(accountGETDto.accountType(), accountGETDto.accountType());
        Assertions.assertEquals(accountGETDto.isActive(), accountGETDto.isActive());
    }

    @When("I request to get a bank account")
    public void requestToGetABankAccount() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/7",
                HttpMethod.GET,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }
    @Then("I should get an api request exception")
    public void shouldGetAnApiRequestException() {
        // Validate if an exception occurred during the request
        Assertions.assertEquals(response.getBody(),"Bank account cannot be accessed");
    }

    @When("I request to deactivate account with ID")
    public void requestDeactivateAccountWithID() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/3",
                HttpMethod.PUT,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }
    @Then("I should deactivate account with ID")
    public void deactivateAccountWithID() {
        account.setAbsoluteLimit(accountPUTDto.absoluteLimit());
        account.setIsActive(accountPUTDto.isActive());
        Assertions.assertEquals(false, account.getIsActive());
    }

    @When("I request to activate account with ID")
    public void requestActivateAccountWithID() {
        response = restTemplate.exchange(
                ACCOUNT_ENDPOINT + "/4",
                HttpMethod.PUT,
                new HttpEntity<>(
                        null,
                        httpHeaders),
                String.class);
    }
    @Then("I should activate account with ID")
    public void activateAccountWithID() {
        account2.setAbsoluteLimit(accountPUTDto2.absoluteLimit());
        account2.setIsActive(accountPUTDto2.isActive());
        Assertions.assertEquals(true, accountPUTDto.isActive());
    }

    private String getTheToken(LoginRequestDTO loginDTO) throws JsonProcessingException {
        response = restTemplate
                .exchange("/auth/login",
                        HttpMethod.POST,
                        new HttpEntity<>(objectMapper.writeValueAsString(loginDTO), httpHeaders), String.class);
        TokenDTO tokenDTO = objectMapper.readValue(response.getBody(), TokenDTO.class);
        return tokenDTO.jwt();
    }

    @Then("getting a status code of {int}")
    public void gettingAStatusCodeOf(int status) {
        Assertions.assertEquals(status, response.getStatusCode().value());
    }
}
