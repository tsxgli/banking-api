package nl.inholland.bankingapi.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserType implements GrantedAuthority {


    ROLE_CUSTOMER,
    ROLE_EMPLOYEE,

    ROLE_USER;

    @Override
    public String getAuthority() {
        return name();
    }
}