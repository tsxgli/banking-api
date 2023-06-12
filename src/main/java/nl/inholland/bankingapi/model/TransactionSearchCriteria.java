package nl.inholland.bankingapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionSearchCriteria implements Serializable {
    private String toIban;
    private String fromIban;
    private String fromDate;
    private String toDate;
    private Double lessThanAmount;
    private Double greaterThanAmount;
    private Double equalToAmount;
    private TransactionType type;
    private Long performingUser;
}
