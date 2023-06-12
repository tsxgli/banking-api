package nl.inholland.bankingapi.model.dto;

public record LoginResponseDTO(String jwt, String email, long id) {

}
