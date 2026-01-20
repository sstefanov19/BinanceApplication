package org.example.binancetickerdemo.controller;

import org.example.binancetickerdemo.service.BinanceService;
import org.example.binancetickerdemo.service.PriceCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class BinanceController {

    private final PriceCache priceCache;
    private final BinanceService binanceService;

    public BinanceController(PriceCache priceCache, BinanceService binanceService) {
        this.priceCache = priceCache;
        this.binanceService = binanceService;
    }

    @GetMapping("/price/{symbol}")
    public ResponseEntity<BigDecimal> getPrice(@PathVariable String symbol) {
        BigDecimal price = priceCache.getPrice(symbol);
        if (price == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(price);
    }

    @GetMapping("/prices")
    public Map<String, BigDecimal> getAllPrices() {
        return priceCache.getAllPrices();
    }

    @PostMapping("/subscribe/{symbol}")
    public ResponseEntity<String> subscribe(@PathVariable String symbol) {
        binanceService.subscribeToSymbol(symbol);
        return ResponseEntity.ok("Subscribed to " + symbol.toUpperCase());
    }
}