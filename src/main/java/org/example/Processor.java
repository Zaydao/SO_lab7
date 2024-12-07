package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Processor {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);
        server.createContext("/process", new ProcessHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server running on port 5000...");
    }

    static class ProcessHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                JSONObject requestJson = new JSONObject(body);

                String text = requestJson.optString("text").toUpperCase();
                int[] array = requestJson.optJSONArray("array")
                        .toList().stream().mapToInt(o -> (int) o).toArray();

                int sum = 0;
                for (int num : array) {
                    sum += num;
                }

                JSONObject responseJson = new JSONObject();
                responseJson.put("text", text);
                responseJson.put("array_sum", sum);

                String response = responseJson.toString();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}
