package org.example;

import static spark.Spark.*;

public class LogService {

    private static MongoConnection mongoConnection;

    public static void main(String[] args) {
        mongoConnection = MongoConnection.getInstance();
        startServer();
    }

    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }

    public static void startServer() {
        port(getPort());
        get("/work", (req, res) -> "funcionando");
        get("/get", (req, res) -> mongoConnection.getCollection());
        post("/add", (req, res) -> mongoConnection.addDocument(req.body()));
    }

}
