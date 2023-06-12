package nl.inholland.bankingapi.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    private Account validAccount;
    @BeforeEach
    void setUp() {
        validAccount = new Account(new User(), "NL21INHO0123400081", 100.00,0.0, AccountType.SAVINGS, true);
    }
    @Test
    void creatingValidAccountShouldResultInValidObject(){
        Assertions.assertNotNull(validAccount);
    }

    @Test
    void getUser() {
        Assertions.assertNotNull(validAccount.getUser());
    }

    @Test
    void getIBAN() {
        Assertions.assertEquals("NL21INHO0123400081", validAccount.getIBAN());
    }

    @Test
    void getBalance() {
        Assertions.assertEquals(100.00, validAccount.getBalance());
    }

    @Test
    void getAbsoluteLimit() {
        Assertions.assertEquals(0.0, validAccount.getAbsoluteLimit());
    }

    @Test
    void getAccountType() {
        Assertions.assertEquals(AccountType.SAVINGS, validAccount.getAccountType());
    }

    @Test
    void getIsActive() {
        Assertions.assertTrue(validAccount.getIsActive());
    }

    @Test
    void setUser() {
        Account newAccount = new Account();
        newAccount.setUser(new User());
        Assertions.assertNotNull(newAccount.getUser());
    }

    @Test
    void setIBAN() {
        validAccount.setIBAN("NL21INHO0954411190");
        Assertions.assertEquals("NL21INHO0954411190", validAccount.getIBAN());
    }

    @Test
    void setBalance() {
        validAccount.setBalance(900.00);
        Assertions.assertEquals(900.00, validAccount.getBalance());
    }

    @Test
    void setAbsoluteLimit() {
        validAccount.setAbsoluteLimit(100.00);
        Assertions.assertEquals(100.00, validAccount.getAbsoluteLimit());
    }

    @Test
    void setAccountType() {
        validAccount.setAccountType(AccountType.CURRENT);
        Assertions.assertEquals(AccountType.CURRENT, validAccount.getAccountType());
    }

    @Test
    void setIsActive() {
        validAccount.setIsActive(false);
        Assertions.assertFalse(validAccount.getIsActive());
    }

    @Test
    void isAccountActive(){
        Account newAccount = new Account();
        Assertions.assertNull(newAccount.getAccountId());
    }
}