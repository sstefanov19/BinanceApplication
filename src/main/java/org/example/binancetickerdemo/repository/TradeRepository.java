package org.example.binancetickerdemo.repository;

import org.example.binancetickerdemo.model.Trade;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TradeRepository {


    private final JdbcTemplate jdbcTemplate;

    public TradeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Trade trade) {
        String sql = """
                INSERT INTO trades
                (symbol , price, trade_time)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (id) DO NOTHING
                """;

        jdbcTemplate.update(sql,
                trade.getId(),
                trade.getSymbol(),
                trade.getPrice(),
                trade.getTime()
        );
    }

}
