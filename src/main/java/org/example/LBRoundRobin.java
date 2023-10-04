package org.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import static spark.Spark.*;

public class LBRoundRobin {

    public static AtomicInteger numService = new AtomicInteger(0);
    public static void main(String[] args) {
        startServer();
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 8080;
    }
    public static void startServer() {
        port(getPort());
        staticFiles.location("public");
        get("/logservice", (req, res) -> getCollection());
        post("/logservice", (req, res) -> postNewDocument(req.body()));
    }

    private static HttpURLConnection roundRobin(String service) throws IOException {
        String urlString = "http://log" + numService.getAndAdd(1)%3 + ":6000/" + service;
        URL url = new URL(urlString);
        //local
        //URL url = new URL("http://localhost:4567/" + service);
        return (HttpURLConnection) url.openConnection();
    }
    public static String getCollection() throws IOException {
        HttpURLConnection con = roundRobin("get");
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    public static String postNewDocument(String object) throws IOException {
        try {
            HttpURLConnection con = roundRobin("add");

            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            // Set request headers as needed (e.g., Content-Type)
            con.setRequestProperty("Content-Type", "application/json");

            // Define the POST data (JSON body) to send
            String postData = object;

            // Write the POST data to the connection
            try (DataOutputStream dataOutputStream = new DataOutputStream(con.getOutputStream())) {
                dataOutputStream.writeBytes(postData);
                dataOutputStream.flush();
            }

            // Get the response code (e.g., 200 for success)
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    con.disconnect();
                    return response.toString();
                }
            } else {
                con.disconnect();
                return "API request failed with response code: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "API doesn't work";
    }

}
