package com.chairpick.ecommerce.io.input;

public record LoginInput(String email, String password) {

    @Override
    public String toString() {
        return "LoginInput{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
