package nl.inholland.bankingapi.model.dto;


import nl.inholland.bankingapi.model.TransactionType;

public record TransactionGET_DTO(long transactionId, String fromIban, String toIban, double amount, TransactionType type,
                                 String timeStamp, long performingUserId) {

}
