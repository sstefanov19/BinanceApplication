package org.example.binancetickerdemo.model;

import java.math.BigDecimal;


public class Trade {

    private Long id;

    private String symbol;

    private BigDecimal price;

    private Long time;


    public void setId(Long id) { this.id = id; }

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol  = symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }



}

