package nl.inholland.bankingapi.model.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import nl.inholland.bankingapi.model.TransactionType;

public record TransactionPOST_DTO(@NotNull
                                  @Pattern(regexp = "^NL\\d{2}INHO\\d{8}$", message = "fromIban must be a valid IBAN")
                                  String fromIban,
                                  @NotNull
                                  @Pattern(regexp = "^NL\\d{2}INHO\\d{8}$", message = "toIban must be a valid IBAN")
                                  String toIban,
                                  @NotNull(message = "Amount must be provided")
                                  @Min(value = 0, message = "Amount must be greater than 0")
                                  Double amount,
                                  @NotNull(message = "Type must be provided")
                                  TransactionType type,
                                  @NotNull(message = "performingUser must be provided")
                                  long performingUser) {

}
