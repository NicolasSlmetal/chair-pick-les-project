package com.chairpick.ecommerce.repositories;

import com.chairpick.ecommerce.daos.interfaces.GenericDAO;
import com.chairpick.ecommerce.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private final GenericDAO<User> userDAO;

    public UserRepository(GenericDAO<User> userDAO) {
        this.userDAO = userDAO;
    }

    public Optional<User> findByEmail(String email) {
        List<User> userByEmail = userDAO.findBy(Map.of("email", email));
        return userByEmail
                .stream()
                .findFirst();
    }

    public void update(User user) {
        userDAO.update(user);
    }
}
