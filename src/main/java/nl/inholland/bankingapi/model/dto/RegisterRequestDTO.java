package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.UserType;

public record RegisterRequestDTO(String email, String password, String firstName, String lastName, String birthDate,
                                 String postalCode, String address, String city, String phoneNumber, UserType userType,Double dailyLimit,Double transactionLimit, boolean hasAccount) {
}
