package nl.inholland.bankingapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//    @GeneratedValue(generator = "account_seq", strategy = GenerationType.SEQUENCE)
//    @SequenceGenerator(name="account_seq", initialValue = 1)
    private Long accountId;

    @ManyToOne
    @JsonIgnoreProperties({"accounts"})
    private User user;

    @Column(unique = true)
    private String IBAN;
    private Double balance;
    private Double absoluteLimit;
    private AccountType accountType;
    private Boolean isActive;

    public Account(User user, String IBAN, double balance,double absoluteLimit, AccountType accountType, Boolean isActive) {
        this.user = user;
        this.IBAN = IBAN;
        this.balance = balance;
        this.absoluteLimit = absoluteLimit;
        this.accountType = accountType;
        this.isActive = isActive;
    }
}
