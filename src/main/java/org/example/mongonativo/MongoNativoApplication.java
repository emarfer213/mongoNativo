package org.example.mongonativo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.util.Arrays;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static com.mongodb.client.model.Projections.*;

public class MongoNativoApplication {
    public static void main(String[] args) {

        String conectionString = "mongodb://localhost:27017";

        try (MongoClient mongoClient = MongoClients.create(conectionString)) {
            MongoDatabase database = mongoClient.getDatabase("tienda_gaming");
            MongoCollection<Document> clientesCollection = database.getCollection("clientes");
            MongoCollection<Document> juegosCollection = database.getCollection("videjuegos");

            //limpieza
            clientesCollection.drop();
            juegosCollection.drop();

            //insercion
            Document cliente1 = new Document("nombre", "Ana").append("email", "email1@gmail.com").append("fecha_registro", LocalDate.now());
            Document cliente2 = new Document("nombre", "Paco").append("email", "email2@gmail.com").append("fecha_registro", LocalDate.now());

            Document juego1 = new Document("titulo", "juego1").append("genero", "accion").append("precio", 20.00).append("stock", 15);
            Document juego2 = new Document("titulo", "juego2").append("genero", "accion").append("precio", 30.00).append("stock", 20);
            Document juego3 = new Document("titulo", "juego3").append("genero", "accion").append("precio", 40.00).append("stock", 25);
            Document juego4 = new Document("titulo", "juego4").append("genero", "accion").append("precio", 50.00).append("stock", 35);
            Document juego5 = new Document("titulo", "juego5").append("genero", "accion").append("precio", 60.00).append("stock", 50);


        }
    }
}
