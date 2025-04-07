package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.User;
import com.chairpick.ecommerce.model.enums.UserType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper extends CustomRowMapper<User> {
    public UserRowMapper() {
        super("usr");
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong(getColumn("id")))
                .email(rs.getString(getColumn("email")))
                .password(rs.getString(getColumn("password")))
                .type(UserType.valueOf(rs.getString(getColumn("type"))))
                .build();
    }
}
