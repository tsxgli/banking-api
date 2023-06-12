package nl.inholland.bankingapi.model.dto;

import nl.inholland.bankingapi.model.UserType;


import java.util.List;

public record UserGET_DTO(long userId, String email, String firstName,
                          String lastName, String birthDate, String postalCode,
                          String address, String city, String phoneNumber,
                          UserType userType, Boolean hasAccount) {

}
