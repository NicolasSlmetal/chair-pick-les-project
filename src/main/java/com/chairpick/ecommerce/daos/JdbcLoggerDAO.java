package com.chairpick.ecommerce.daos;

import com.chairpick.ecommerce.daos.interfaces.LoggerDAO;
import com.chairpick.ecommerce.daos.registry.EntitiesTables;
import com.chairpick.ecommerce.model.DomainEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class JdbcLoggerDAO implements LoggerDAO {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcLoggerDAO(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void logInsert(Object object, Long userId) {
        String tableName = EntitiesTables.getTableName(object.getClass());

        if (object instanceof DomainEntity entity) {
            String sql = """
                    INSERT INTO tb_insert_log (ilo_table, ilo_user_id, ilo_row_id)
                    VALUES (:tableName, :userId, :rowId)
                    """;
            var parameters = Map.of(
                    "tableName", tableName,
                    "userId", userId,
                    "rowId", entity.getId()
            );

            jdbcTemplate.update(sql, parameters);

        }
    }

    @Override
    public void logUpdate(Object oldObject, Object newObject, Long userId) {
        if (oldObject.getClass().equals(newObject.getClass())) {
            String tableName = EntitiesTables.getTableName(oldObject.getClass());
            Method[] methods = oldObject.getClass().getMethods();
            for (Method method : methods) {
                String methodName = method.getName();
                if ((methodName.startsWith("get") || methodName.startsWith("is")) && !methodName.equalsIgnoreCase("getId")) {
                    try {
                        String columnName = methodName.startsWith("get") ? methodName.substring(3) : methodName.substring(2);
                        Object oldValue = method.invoke(oldObject);
                        Object newValue = method.invoke(newObject);

                        oldValue = turnValueSuitableToDatabase(oldValue);
                        newValue = turnValueSuitableToDatabase(newValue);

                        if (oldValue == null) {
                            oldValue = "null";
                        }

                        if (newValue == null) {
                            newValue = "null";
                        }

                        if (oldValue instanceof Collection<?>) continue;

                        if (!oldValue.equals(newValue)) {
                            String sql = """
                                    INSERT INTO tb_update_log (ulo_table, ulo_user_id, ulo_row_id, ulo_column, ulo_old_value, ulo_new_value)
                                    VALUES (:tableName, :userId, :rowId, :columnName, :oldValue, :newValue)
                                    """;
                            var parameters = Map.of(
                                    "tableName", tableName,
                                    "userId", userId,
                                    "rowId", ((DomainEntity) oldObject).getId(),
                                    "columnName", columnName,
                                    "oldValue", oldValue,
                                    "newValue", newValue
                            );
                            jdbcTemplate.update(sql, parameters);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Object turnValueSuitableToDatabase(Object value) {
        if (value instanceof DomainEntity d) {
            Long id = d.getId();
            if (id == null) {
                return null;
            }
            return d.getId().toString();
        }

        if (value instanceof Collection<?>) {
            return value;
        }

        if (value == null) {
            return null;
        }

        return value.toString();
    }


}
