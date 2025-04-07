package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.io.input.LoginInput;
import com.chairpick.ecommerce.model.enums.UserType;
import com.chairpick.ecommerce.repositories.CustomerRepository;
import com.chairpick.ecommerce.repositories.UserRepository;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import com.chairpick.ecommerce.utils.ErrorCode;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public LoginService(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    public AuthenticatedUser authenticate(LoginInput loginInput) {
        User user = userRepository.findByEmail(loginInput.email())
                .orElseThrow(() -> new DomainValidationException(ErrorCode.INVALID_USER.name()));

        if (!BCrypt.checkpw(loginInput.password(), user.getPassword())) {
            throw new DomainValidationException(ErrorCode.INVALID_USER.name());
        }

        if (user.getType().equals(UserType.CUSTOMER)) {
            Customer customer = customerRepository.findByUser(user)
                    .orElseThrow(() -> new DomainValidationException(ErrorCode.INVALID_USER.name()));

            return new AuthenticatedUser(user, customer);
        }

        return new AuthenticatedUser(user);
    }
}
