package com.chairpick.ecommerce.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.chairpick.ecommerce.exceptions.AuthenticationException;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.model.enums.UserType;
import com.chairpick.ecommerce.io.output.TokenResponseDTO;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.secret}")
    private String apiKey;


    public TokenResponseDTO generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant afterOneHour = now.plus(1, ChronoUnit.HOURS);

        String role = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseGet(UserType.CUSTOMER::name);
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withSubject(user.getUsername())
                .withClaim("role", role)
                .withIssuedAt(now)
                .withExpiresAt(afterOneHour)
                .withIssuer("chairpick")
                .withClaim("user_id", user.getUser().getId())
                .withClaim("email", user.getUsername())
                .withClaim("role", role);

        if (user.getUser().getType().equals(UserType.CUSTOMER)) {
            jwtBuilder.withClaim("customer_id", user.getCustomer().getId());
        }
        return new TokenResponseDTO(jwtBuilder.sign(Algorithm.HMAC256(apiKey)), role, afterOneHour);
    }

    public AuthenticatedUser decodeToken(String token) {
        DecodedJWT decodedJWT = JWT.decode(token);
        if (decodedJWT.getExpiresAt().before(new Date())) {
            throw new AuthenticationException("Token expired");
        }
        String email = decodedJWT.getClaim("email").asString();
        String role = decodedJWT.getClaim("role").asString();
        Long id = decodedJWT.getClaim("user_id").asLong();
        User user = User.builder()
                .id(id)
                .email(email)
                .type(UserType.valueOf(role))
                .build();
        if (!decodedJWT.getClaim("customer_id").isNull()) {
            Customer customer = Customer.builder()
                    .id(decodedJWT.getClaim("customer_id").asLong())
                    .user(user)
                    .build();

            return new AuthenticatedUser(user, customer);
        }

        return new AuthenticatedUser(user);
    }
}
