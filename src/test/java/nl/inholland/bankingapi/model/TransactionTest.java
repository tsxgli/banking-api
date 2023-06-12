package nl.inholland.bankingapi.model;

import jakarta.servlet.http.HttpServletRequest;
import nl.inholland.bankingapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TransactionTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    private Transaction transaction;
    private Account fromAccount;
    private Account toAccount;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        User loggedInUser = new User();
        when(userService.getLoggedInUser(request)).thenReturn(loggedInUser);
        user = new User();
        fromAccount = new Account(user, "NL21INHO0123400081", 90000.00, 0.00, AccountType.CURRENT, true);
        toAccount = new Account(user, "NL21INHO0123400083", 9000.00, 0.00, AccountType.CURRENT, true);
        transaction = new Transaction(fromAccount, toAccount, 100.0, LocalDateTime.now(), TransactionType.DEPOSIT, user);
    }

    @Test
    void testGetters() {
        assertEquals(fromAccount.getUser().getId(), userService.getLoggedInUser(request).getId());
        assertEquals(toAccount.getUser().getId(), transaction.getPerformingUser().getId());
        assertEquals(100.0, transaction.getAmount());
        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertNotNull(transaction.getTimestamp());
        assertEquals(user, transaction.getPerformingUser());
    }

    @Test
    void newTransactionShouldHaveAccountFrom() {
        assertNotNull(transaction.getFromIban());
    }

    @Test
    void newTransactionShouldHaveAccountTo() {
        assertNotNull(transaction.getToIban());
    }

    @Test
    void newTransactionAmountShouldBePositive() {
        assertTrue(transaction.getAmount() > 0);
    }
    @Test
    void newTransactionTimeStampShouldBeLocalDateTime() {
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void newTransactionShouldHaveType() {
        assertNotNull(transaction.getType());
    }
    @Test
    void newTransactionShouldHavePerformingUser() {
        assertNotNull(transaction.getPerformingUser());
    }

    // Add more test methods as needed
}
