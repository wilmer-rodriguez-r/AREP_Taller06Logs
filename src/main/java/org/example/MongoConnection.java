package org.example;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MongoConnection {

    private String databaseName = "nueva_db";
    private String address = "db:27017";
    private String collectionName = "test";
    private static MongoDatabase mongoDatabase;
    private static MongoConnection instance;

    private MongoConnection() {
        try {
            mongoDatabase = new MongoClient(new ServerAddress(address)).getDatabase(databaseName);
        } catch (Exception e) {
            System.out.println("No se pudo conectar a base de datos");
        }
    }

    public static MongoConnection getInstance() {
        if (instance == null) {
            instance = new MongoConnection();
        }
        return instance;
    }

    public String getCollection() {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(collectionName);
        List<Document> registros = mongoCollection.find().sort(Sorts.descending("_id")).limit(10).into(new ArrayList<>());
        Gson gson = new Gson();
        return gson.toJson(registros);
    }

    public boolean addDocument(String object) {
        try {
            Document document = Document.parse(object);
            Date date = new Date();
            SimpleDateFormat formateadorFecha = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formateadorHora = new SimpleDateFormat("HH:mm:ss");
            document.put("date", formateadorFecha.format(date) + " " + formateadorHora.format(date));
            mongoDatabase.getCollection(collectionName).insertOne(document);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}