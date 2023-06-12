package nl.inholland.bankingapi.model.dto;

public record ExceptionDTO(int status, String message, String exception) {
}
