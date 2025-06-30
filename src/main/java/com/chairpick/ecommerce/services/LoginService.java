package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.exceptions.DomainValidationException;
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

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final TaskExecutor executor;

    public LoginService(UserRepository userRepository, CustomerRepository customerRepository, TaskExecutor executor) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
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
        Optional<User> user = userRepository.findByEmail(email.replaceAll("\"", ""));

        if (user.isPresent()) {

            String subject = "Redefinição de senha";
            String content = "Para redefinir sua senha, acesse o seguinte link: http://localhost:8080/reset-password";
            SendMailParams mailParams = new SendMailParams(user.get(), subject, content);
            executor.execute(new SendMailTask(mailParams));
        }
    }

    public void resetPassword(String email, NewPasswordInput input) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new DomainValidationException(ErrorCode.INVALID_USER.name()));


        user.setPassword(input.getPassword());
        user.validateOnlyPassword();
        user.setPassword(BCrypt.hashpw(input.getPassword(), BCrypt.gensalt()));
        userRepository.update(user);

    }
}
