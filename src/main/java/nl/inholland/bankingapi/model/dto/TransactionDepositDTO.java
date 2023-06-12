package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import nl.inholland.bankingapi.model.UserType;

public record TransactionDepositDTO (String toIban, Double amount ){
}
