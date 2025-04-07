package com.chairpick.ecommerce.security;

import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.model.enums.UserType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthenticatedUser implements UserDetails {

    private final User user;
    private Customer customer;

    public AuthenticatedUser(User user) {
        this.user = user;
    }

    public AuthenticatedUser(User user, Customer customer) {
        this.user = user;
        this.customer = customer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getType().equals(UserType.ADMIN)) {
            return List.of(UserType.ADMIN::name, UserType.CUSTOMER::name);
        }
        return List.of(UserType.CUSTOMER::name);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{" +
                "user=" + user +
                '}';
    }
}
