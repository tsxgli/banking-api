package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.UserType;

public record UserPOST_DTO(String email, String password, String passwordConfirm, String firstName, String lastName, String birthDate, String postalCode, String address, String city, String phoneNumber, UserType userType, Boolean hasAccount)
{

}
