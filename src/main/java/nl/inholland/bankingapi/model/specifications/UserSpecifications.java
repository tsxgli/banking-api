package nl.inholland.bankingapi.model.specifications;

import jakarta.persistence.criteria.*;
import nl.inholland.bankingapi.model.Account;
import nl.inholland.bankingapi.model.AccountType;
import nl.inholland.bankingapi.model.User;
import nl.inholland.bankingapi.model.UserType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class UserSpecifications {
    private UserSpecifications() {
    }

    public static Specification<User> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(root.get("firstName"), "%" + keyword + "%"),
                criteriaBuilder.like(root.get("lastName"), "%" + keyword + "%"),
                criteriaBuilder.like(root.get("email"), "%" + keyword + "%"),
                criteriaBuilder.like(root.get("address"), "%" + keyword + "%"),
                criteriaBuilder.like(root.get("city"), "%" + keyword + "%"),
                criteriaBuilder.like(root.get("postalCode"), "%" + keyword + "%"),
                criteriaBuilder.like(root.get("birthDate"), "%" + keyword + "%")
        );
    }

    public static Specification<User> hasFirstName(String firstName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("firstName"), firstName);
    }

    public static Specification<User> hasLastName(String lastName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("lastName"), lastName);
    }

    public static Specification<User> hasHasAccount(String hasAccount) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("hasAccount"), hasAccount);
    }
    public static Specification<User> hasNoAccountType(AccountType excludedAccountType) {
        return (root, query, criteriaBuilder) -> {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<User> subRoot = subquery.from(User.class);
        Join<User, Account> accountJoin = subRoot.join("accounts", JoinType.LEFT);

        Predicate accountTypeCondition = criteriaBuilder.equal(accountJoin.get("accountType"), excludedAccountType);
        subquery.select(subRoot.get("id")).where(accountTypeCondition);

        return criteriaBuilder.not(root.get("id").in(subquery));
        };
    }


    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"), email);
    }

    public static Specification<User> hasAddress(String address) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("address"), address);
    }

    public static Specification<User> hasCity(String city) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("city"), city);
    }

    public static Specification<User> hasPostalCode(String postalCode) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("postalCode"), postalCode);
    }

    public static Specification<User> hasbirthDate(String birthDate){
        //String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(birthDate);
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("birthDate"), birthDate);
    }

    public static Specification<User> hasPhoneNumber(String phoneNumber){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("phoneNumber"), phoneNumber);
    }

    public static Specification<User> hasUserType(UserType userType){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userType"), userType);
    }

    public static Specification<User> getSpecifications(String keyword, String firstName, String lastName, String hasAccount, String email, String address, String city, String postalCode, String birthDate, String phoneNumber, UserType userType, AccountType excludedAccountType) {
        Specification<User> spec = null;
        Specification<User> temp = null;

        if (keyword != null && !keyword.isEmpty()) {
            temp = hasKeyword(keyword);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (firstName != null) {
            temp = hasFirstName(firstName);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (lastName != null) {
            temp = hasLastName(lastName);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (hasAccount != null) {
            temp = hasHasAccount(hasAccount);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (email != null) {
            temp = hasEmail(email);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (address != null) {
            temp = hasAddress(address);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (city != null) {
            temp = hasCity(city);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (postalCode != null) {
            temp = hasPostalCode(postalCode);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (birthDate != null) {
            temp = hasbirthDate(birthDate);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (phoneNumber != null) {
            temp = hasPhoneNumber(phoneNumber);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (userType != null) {
            temp = hasUserType(userType);
            spec = spec == null ? temp : spec.and(temp);
        }
        if (excludedAccountType != null) {
            temp = hasNoAccountType(excludedAccountType);
            spec = spec == null ? temp : spec.and(temp);
        }
        return spec;
    }
}
