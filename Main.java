import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // Serve index.html at /
        server.createContext("/", exchange -> {
            byte[] response = Files.readAllBytes(Paths.get("index.html"));
            exchange.getResponseHeaders().add("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });

        // Sudoku solve handler
        server.createContext("/solve", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String csv = reader.readLine();
                System.out.println("Received: " + csv);

                String[] parts = csv.split(",");
                int[][] board = new int[9][9];
                for (int i = 0; i < 81; i++) {
                    board[i / 9][i % 9] = Integer.parseInt(parts[i]);
                }

                SudokuSolver.solve(board); // Solve it

                StringBuilder result = new StringBuilder();
                for (int[] row : board)
                    for (int val : row)
                        result.append(val).append(",");
                result.setLength(result.length() - 1); // remove last comma

                byte[] response = result.toString().getBytes();
                exchange.getResponseHeaders().add("Content-Type", "text/plain");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });

        server.start();
        System.out.println("Server running at http://localhost:8000");
    }
}