package com.xkom.repository;

import com.xkom.entity.PriceEntity;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PriceRepository {

    private JdbcTemplate jdbcTemplate;

    public PriceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveAll(List<Tuple2<Long, PriceEntity>> entries) {
        if (!entries.isEmpty()) {
            jdbcTemplate.batchUpdate("INSERT INTO PRICE(price, timestamp, product_id) VALUES (?,?,?)", entries.map(toObjectArray()).asJava());
        }
    }

    private Function1<Tuple2<Long, PriceEntity>, Object[]> toObjectArray() {
        return t -> new Object[]{t._2.getPrice().doubleValue(), t._2.getTimestamp(), t._1};
    }
}
