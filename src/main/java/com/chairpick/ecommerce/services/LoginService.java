package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
import com.chairpick.ecommerce.exceptions.ExpiredTokenException;
import com.chairpick.ecommerce.exceptions.InvalidRequestException;
import com.chairpick.ecommerce.io.input.AlterPasswordInput;
import com.chairpick.ecommerce.io.input.NewPasswordInput;
import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.io.input.LoginInput;
import com.chairpick.ecommerce.model.enums.UserType;
import com.chairpick.ecommerce.params.SendMailParams;
import com.chairpick.ecommerce.repositories.CustomerRepository;
import com.chairpick.ecommerce.repositories.UserRepository;
import com.chairpick.ecommerce.security.AuthenticatedUser;
import com.chairpick.ecommerce.services.task.SendMailTask;
import com.chairpick.ecommerce.services.task.interfaces.TaskExecutor;
import com.chairpick.ecommerce.utils.ErrorCode;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final TokenService tokenService;
    private final TaskExecutor executor;
    private final ConcurrentHashMap<String, String> tokenCache = new ConcurrentHashMap<>();

    public LoginService(UserRepository userRepository, CustomerRepository customerRepository, TokenService tokenService, TaskExecutor executor) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.tokenService = tokenService;
        this.executor = executor;
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

    public void sendRequestPassword(String email) {
        email = email.replaceAll("\"", "");
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            String token = tokenService.encodeStringToken(email);
            String subject = "Redefinição de senha";
            String content = "Para redefinir sua senha, acesse o seguinte link: http://localhost:8080/reset-password?token=" + token;
            SendMailParams mailParams = new SendMailParams(user.get(), subject, content);
            executor.execute(new SendMailTask(mailParams));
        }
    }

    public void resetPassword(AlterPasswordInput input) {
        User user = userRepository.findByEmail(input.email())
                .orElseThrow(() -> new DomainValidationException(ErrorCode.INVALID_USER.name()));


        user.setPassword(input.password());
        user.validateOnlyPassword();
        user.setPassword(BCrypt.hashpw(input.password(), BCrypt.gensalt()));
        userRepository.update(user);

    }

    public String validateToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ExpiredTokenException("Token inválido ou não fornecido.");
        }

        if (tokenCache.contains(token)) {
            throw new ExpiredTokenException("Token já utilizado.");
        }

        String email = tokenService.decodeStringToken(token);
        if (email == null || email.isBlank()) {
            throw new ExpiredTokenException("Token inválido.");
        }

        tokenCache.put(token, email);

        return email;
    }
}
