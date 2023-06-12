package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.UserType;

public record UserPUT_DTO(String email, String firstName, String lastName, String birthDate, String postalCode, String address, String city, String phoneNumber, UserType userType, Boolean hasAccount)
{

}
