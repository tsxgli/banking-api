package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.AccountType;

public record AccountGET_DTO (long accountId, long user, String IBAN, double balance, double absoluteLimit, AccountType accountType, boolean isActive){}

