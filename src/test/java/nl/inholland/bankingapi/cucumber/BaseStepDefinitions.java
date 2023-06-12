package nl.inholland.bankingapi.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.spring.CucumberContextConfiguration;
import nl.inholland.bankingapi.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@CucumberContextConfiguration
public class BaseStepDefinitions {

    public static final String VALID_CUSTOMER = "customer@email.com";
    public static final String VALID_EMPLOYEE = "employee@email.com";
    public static final String VALID_PASSWORD = "1234";
    public static final String INVALID_USERNAME = "bla";
    public static final String INVALID_PASSWORD = "invalid";
    public static final String EMPLOYEE_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJlbXBsb3llZUBlbWFpbC5jb20iLCJhdXRoIjoiUk9MRV9FTVBMT1lFRSIsImlhdCI6MTY4NjUyNjE0MiwiZXhwIjoxNjg2NTI5NzQyfQ.dBEo2cFsrF0XNuAIWT0iVy1rvlp-vV8D8LBzMPQLSlJ3tqR67y5Y8dqmzWpj0ed4rTJGzoikaeOeLH1oZswgVgq3DKSAG5l1MG9HBjsVg3sRYNFz7A8P-ytqIZQpRRhw1t0AFOIYXNGMZluLxdvUYbsWJ67FmWOBkXrXV1PsJ-xk48OoLX4K9NA4ilRo4iis3kQEDZ-_MNFwR459JQd64W4ipM5pjxo0Uyy2pyYJURCOXSUC7mKvUwBHEXh_Rbbj1HZtb84DTDVn-ZdPUGITLl2S5Bluou8WWX1mDFLpl-PfH1YgQKHzaMskaal6uvjxbiIWT_3QJEYH57WO237LDA";
   public static final String INVALID_TOKEN = "invalid_token";
    public static final String CUSTOMER_TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJjdXN0b21lckBlbWFpbC5jb20iLCJhdXRoIjoiUk9MRV9DVVNUT01FUiIsImlhdCI6MTY4NjM0NzI0OSwiZXhwIjoyMDQ2MzQ3MjQ5fQ.C5ykh3MMK6jB-uNrVbXlVMb4spoJm9IHj3Is9MA-GtF9TzY_SIX0PU6qw3i1a-nfFDAWyee5S2vGhtCEODmtd1g6YY11LS4elAGHjy6rR1dFFnHBhWZEXIttuVzc8lwGHYYg-I7s4vuMR7InS0PDASMVbjXk3A69Vgu00uQPVmLd6fFQ5_hWFWVmHgikVQEGzjP56PofuXswYzhXoWATHGC3hymc8ki_VDxxomSXSihpVdR1jmp8eVnt556iW7VcXED0f33icDD9SWqaAkIqf5Ki5W9DScL79UXRxmWRh1l5EenkaKJx3zP-4F5Iz0Em5gtVr9RRtVqS_1c5oDmt1w";
    public final HttpHeaders httpHeaders = new HttpHeaders();
    private ResponseEntity<String> response;

    private TransactionPOST_DTO transactionPOSTDto;
    protected TestRestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    private String token;
    private LoginRequestDTO loginRequestDTO;

    private String getToken(LoginRequestDTO loginDTO) throws JsonProcessingException {
        response = restTemplate
                .exchange("/auth/login",
                        HttpMethod.POST,
                        new HttpEntity<>(objectMapper.writeValueAsString(loginDTO), httpHeaders), String.class);
        TokenDTO tokenDTO = objectMapper.readValue(response.getBody(), TokenDTO.class);
        return tokenDTO.jwt();
    }

}
