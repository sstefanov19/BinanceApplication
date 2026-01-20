package org.example.binancetickerdemo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BinanceService extends WebSocketListener {

    private static final String WS_BASE = "wss://stream.binance.com:443";

    private final OkHttpClient client;
    private final PriceCache priceCache;
    private final ObjectMapper objectMapper;

    private WebSocket webSocket;

    public BinanceService(PriceCache priceCache) {
        this.client = new OkHttpClient();
        this.priceCache = priceCache;
        this.objectMapper = new ObjectMapper();
    }

    @PostConstruct
    public void init() {
        subscribeToSymbols(List.of("btcusdt", "ethusdt"));
    }

    public void subscribeToSymbols(List<String> symbols) {
        String streams = String.join("/", symbols.stream()
                .map(s -> s.toLowerCase() + "@trade")
                .toList());
        String url = WS_BASE + "/stream?streams=" + streams;

        System.out.println("Connecting to: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocket = client.newWebSocket(request, this);
    }

    public void subscribeToSymbol(String symbol) {
        String url = WS_BASE + "/ws/" + symbol.toLowerCase() + "@trade";
        System.out.println("Subscribing to: " + url);
        Request request = new Request.Builder().url(url).build();
        client.newWebSocket(request, this);
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        System.out.println("WebSocket connected to Binance");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        try {
            JsonNode json = objectMapper.readTree(text);

            JsonNode data = json.has("data") ? json.get("data") : json;

            String symbol = data.get("s").asText();
            BigDecimal price = new BigDecimal(data.get("p").asText());

            priceCache.updatePrice(symbol, price);
            System.out.println("Updated " + symbol + ": " + price);
        } catch (Exception e) {
            System.err.println("Error parsing message: " + e.getMessage());
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.err.println("WebSocket error: " + t.getMessage());
        reconnect();
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        System.out.println("WebSocket closed: " + reason);
    }

    private void reconnect() {
        try {
            Thread.sleep(5000);
            init();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing");
        }
    }
}