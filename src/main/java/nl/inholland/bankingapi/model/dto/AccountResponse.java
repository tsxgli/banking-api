package nl.inholland.bankingapi.model.dto;

import java.util.List;

public class AccountResponse {
    private List<AccountGET_DTO> userAccounts;
    private Double totalBalance;

    // Getters and setters for the fields

    public List<AccountGET_DTO> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(List<AccountGET_DTO> userAccounts) {
        this.userAccounts = userAccounts;
    }

    public Double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Double totalBalance) {
        this.totalBalance = totalBalance;
    }
}
