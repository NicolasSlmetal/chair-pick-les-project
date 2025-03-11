package com.chairpick.ecommerce.utils.mappers;

import com.chairpick.ecommerce.model.Customer;
import com.chairpick.ecommerce.model.Genre;
import com.chairpick.ecommerce.model.PhoneType;
import com.chairpick.ecommerce.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CustomerMapper extends CustomRowMapper implements RowMapper<Customer> {

    private final Map<String, Genre> characterToEnum;
    private final Map<String, PhoneType> phoneTypeMap;

    public CustomerMapper() {
        super("cus");
        characterToEnum = new HashMap<>();
        phoneTypeMap = new HashMap<>();

        characterToEnum.put("M", Genre.MALE);
        characterToEnum.put("F", Genre.FEMALE);

        phoneTypeMap.put("CELL_PHONE", PhoneType.CELL_PHONE);
        phoneTypeMap.put("LANE", PhoneType.LANE);
    }

    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = characterToEnum.get(rs.getString(getColumn("genre")));
        PhoneType phoneType = phoneTypeMap.get(rs.getString(getColumn("phone type")));

        User user = User.builder()
                .email(rs.getString(getRelatedTableColumn("email", "usr"))).build();
        user.setId(rs.getLong(getRelatedTableColumn("id", "usr")));
        return Customer
                .builder()
                .id(rs.getLong(getColumn("id")))
                .name(rs.getString(getColumn("name")))
                .phoneType(phoneType)
                .phoneDDD(rs.getString(getColumn("phone ddd")))
                .phone(rs.getString(getColumn("phone")))
                .user(user)
                .bornDate(rs.getDate(getColumn("born_date")).toLocalDate())
                .cpf(rs.getString(getColumn("cpf")))
                .genre(genre)
                .build();
    }
}
