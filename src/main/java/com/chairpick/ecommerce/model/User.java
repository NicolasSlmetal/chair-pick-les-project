package com.chairpick.ecommerce.model;

import com.chairpick.ecommerce.model.enums.UserType;
import com.chairpick.ecommerce.utils.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@Getter
@Setter
@SuperBuilder
public class User extends DomainEntity {
    private String email;
    private String password;
    private UserType type;

    public User() {
        super();
    }

    @Override
    public void validate() {
        if (!haveValidEmail()) {
            getErrors().add(ErrorCode.INVALID_EMAIL);
        }

        if (!haveValidPassword()) {
            getErrors().add(ErrorCode.INVALID_PASSWORD);
        }

        verifyIfHasErrors();
    }

    public void validateOnlyEmail() {
        if (!haveValidEmail()) {
            getErrors().add(ErrorCode.INVALID_EMAIL);
        }

        verifyIfHasErrors();
    }

    public void validateOnlyPassword() {
        if (!haveValidPassword()) {
            getErrors().add(ErrorCode.INVALID_PASSWORD);
        }

        verifyIfHasErrors();
    }

    private boolean haveValidEmail() {
        if (email == null || email.isBlank()) {
            return false;
        }

        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private boolean haveValidPassword() {
        if (password == null || password.isBlank()) {
            return false;
        }

        if (password.length() < 8) {
            return false;
        }

        Pattern lowerCase = Pattern.compile("[a-z]");
        Pattern upperCase = Pattern.compile("[A-Z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile("[^a-zA-Z0-9]");
        Predicate<String> predicate = lowerCase.asPredicate()
                .and(upperCase.asPredicate())
                .and(lowerCase.asPredicate())
                .and(digit.asPredicate())
                .and(special.asPredicate());

        return predicate.test(password);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", type=" + type +
                '}';
    }
}
