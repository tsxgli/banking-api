package nl.inholland.bankingapi;

import nl.inholland.bankingapi.jwt.JwtTokenProvider;
import nl.inholland.bankingapi.service.TransactionService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

@TestConfiguration
public class ApiTestConfiguration {
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    public TransactionService transactionService() {
        return Mockito.mock(TransactionService.class);
    }
}
