package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.model.UserType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper extends CustomRowMapper implements RowMapper<User> {
    public UserMapper() {
        super("usr");
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .email(rs.getString(getColumn("email")))
                .password(rs.getString(getColumn("password")))
                .type(UserType.valueOf(rs.getString(getColumn("type"))))
                .build();
    }
}
