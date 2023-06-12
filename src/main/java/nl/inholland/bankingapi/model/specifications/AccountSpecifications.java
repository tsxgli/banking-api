package nl.inholland.bankingapi.model.specifications;

import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.Transaction;
import nl.inholland.bankingapi.model.TransactionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RequestParam;

public class AccountSpecifications {
    private AccountSpecifications() {
    }
    public static Specification<Account> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("firstName"), firstName);
    }
    public static Specification<Account> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lastName"), lastName);
    }
    public static Specification<Account> hasAccountType(AccountType accountType) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("accountType"), accountType);
    }
    public static Specification<Account> hasAbsoluteLimit(Double absoluteLimit) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("absoluteLimit"), absoluteLimit);
    }
    public static Specification<Account> hasIsActive(Boolean isActive) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), isActive);
    }
    public static Specification<Account> hasUser(Long userId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), userId);
    }


    public static Specification<Account>getSpecifications(String firstName, String lastName, AccountType accountType, Double absoluteLimit, Boolean isActive, Long user) {
        Specification<Account> spec = null;
        Specification<Account> temp=null;
        if (firstName != null) {
            temp=hasFirstName(firstName);
            spec= temp;
        }
        if (lastName != null) {
            temp=hasLastName(lastName);
            spec=spec==null?temp:spec.and(temp);
        }
        if (accountType != null) {
            temp=hasAccountType(accountType);
            spec=spec==null?temp:spec.and(temp);
        }
        if (absoluteLimit != null) {
            temp=hasAbsoluteLimit(absoluteLimit);
            spec=spec==null?temp:spec.and(temp);
        }
        if (isActive!= null) {
            temp=hasIsActive(isActive);
            spec=spec==null?temp:spec.and(temp);
        }
        if (user != null) {
            temp=hasUser(user);
            spec=spec==null?temp:spec.and(temp);
        }

        return spec;
    }
}
