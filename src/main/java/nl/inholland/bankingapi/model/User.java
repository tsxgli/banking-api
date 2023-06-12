package nl.inholland.bankingapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String firstName;
    private String lastName;
    private String birthDate;
    private String postalCode;
    private String address;
    private String city;
    private String phoneNumber;
    private UserType userType;
    private Boolean hasAccount;
    private Double dailyLimit;
    private Double transactionLimit;


    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Account> accounts = new ArrayList<>();

   public User(String email, String password, String firstName, String lastName, String birthDate, String postalCode, String address, String city, String phoneNumber, UserType userType, Double dailyLimit, Double transactionLimit, boolean hasAccount) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.postalCode = postalCode;
        this.address = address;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.hasAccount = hasAccount;
        this.dailyLimit = dailyLimit;
        this.transactionLimit = transactionLimit;

    }
}