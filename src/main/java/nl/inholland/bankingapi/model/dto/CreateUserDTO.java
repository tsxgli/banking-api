package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.UserType;

public record CreateUserDTO(String email, String password, UserType role) {
}
