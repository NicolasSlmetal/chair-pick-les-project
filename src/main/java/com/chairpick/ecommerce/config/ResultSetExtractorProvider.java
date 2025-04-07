package com.chairpick.ecommerce.config;

import com.chairpick.ecommerce.model.Chair;
import com.chairpick.ecommerce.model.Order;
import com.chairpick.ecommerce.utils.mappers.ChairRowMapper;
import com.chairpick.ecommerce.utils.mappers.OrderRowMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.List;

@Configuration
public class ResultSetExtractorProvider {

    @Bean
    public ResultSetExtractor<List<Chair>> chairResultSetExtractor() {
        return new ChairRowMapper();
    }

    @Bean
    public ResultSetExtractor<List<Order>> orderResultSetExtractor() {
        return new OrderRowMapper();
    }
}
