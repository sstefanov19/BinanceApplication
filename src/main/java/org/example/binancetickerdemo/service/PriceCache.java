package org.example.binancetickerdemo.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceCache {

    private final Map<String, BigDecimal> prices = new ConcurrentHashMap<>();

    public void updatePrice(String symbol, BigDecimal price) {
        prices.put(symbol.toUpperCase(), price);
    }

    public BigDecimal getPrice(String symbol) {
        return prices.get(symbol.toUpperCase());
    }

    public Map<String, BigDecimal> getAllPrices() {
        return Map.copyOf(prices);
    }
}