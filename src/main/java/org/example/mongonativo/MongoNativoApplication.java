package org.example.mongonativo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.util.*;

public class MongoNativoApplication {
    private static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        String conectionString = "mongodb://localhost:27017";

        try (MongoClient mongoClient = MongoClients.create(conectionString)) {
            MongoDatabase database = mongoClient.getDatabase("tienda_gaming");
            MongoCollection<Document> clientesCollection = database.getCollection("clientes");
            MongoCollection<Document> juegosCollection = database.getCollection("videjuegos");
            MongoCollection<Document> ventaCollection = database.getCollection("ventas");

            bucleMenu(clientesCollection, juegosCollection, ventaCollection);

        }
    }

    private static void menu() {
        System.out.println("""
                ---------MENU------------
                1. Carga de Datos
                2. Insertar Juego y Cliente
                3. Salir
                """);
    }

    private static void bucleMenu(MongoCollection<Document> clientesCollection, MongoCollection<Document> juegosCollection, MongoCollection<Document> ventaCollection) {
        boolean exit = false;
        int opcion = -1;

        while (!exit) {
            menu();

            try {
                opcion = Integer.parseInt(input.nextLine());
            } catch (InputMismatchException | NumberFormatException e) {
                opcion = -1;
            }
            switch (opcion) {
                case 1:
                    crearDatosPredefinidos(clientesCollection, juegosCollection, ventaCollection);
                    break;
                case 2:
                    System.out.printf("\n en proceso \n");
                    break;
                case 3:
                    exit = true;
                    break;
                default:
                    System.out.printf("\n opcion no valida \n");
                    break;
            }
        }
    }

    private static Document crearCliente(String nombre, String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException(
                    "Email inválido: " + email
            );
        }

        return new Document("nombre", nombre)
                .append("email", email)
                .append("fecha_registro", LocalDate.now());
    }

    private static Document crearJuego(String titulo, String genero, double precio, int stock) {
        if (precio <= 0) {
            throw new IllegalArgumentException(
                    "El precio del producto no puede ser menor o igua a 0"
            );
        }
        if (stock <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad en stock de un producto no puede ser 0 o menor"
            );
        }
        return new Document("titulo", titulo)
                .append("genero", genero)
                .append("precio", precio)
                .append("stock", stock);
    }

    private static void crearDatosPredefinidos(MongoCollection<Document> clientesCollection, MongoCollection<Document> juegosCollection, MongoCollection<Document> ventaCollection) {
        //limpieza
        clientesCollection.drop();
        juegosCollection.drop();

        //insercion
        extraerClientesValidos(clientesCollection);
        extraerJuegosValidos(juegosCollection);
    }

    private static void extraerJuegosValidos(MongoCollection<Document> juegosCollection) {
        List<Document> juegosValidos = new ArrayList<>();

        String[][] datosJuegos = {
                {"juego1", "accion", "20", "15"},
                {"juego2", "rpg", "30", "20"},
                {"juego3", "puzzles", "0", "25"},
                {"juego4", "shooter", "50", "35"},
                {"juego5", "accion", "60", "50"}
        };

        for (String[] j : datosJuegos) {
            try {
                juegosValidos.add(
                        crearJuego(
                                j[0],
                                j[1],
                                Double.parseDouble(j[2]),
                                Integer.parseInt(j[3])
                        )
                );
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        if (!juegosValidos.isEmpty()) {
            juegosCollection.insertMany(juegosValidos);
        }
    }

    private static void extraerClientesValidos(MongoCollection<Document> clientesCollection) {
        List<Document> clientesValidos = new ArrayList<>();

        List<String[]> datosClientes = Arrays.asList(
                new String[]{"Ana", "email1@gmail.com"},
                new String[]{"Paco", "email2gmail.com"}
        );

        for (String[] datos : datosClientes) {
            try {
                clientesValidos.add(crearCliente(datos[0], datos[1]));
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ Cliente descartado: " + e.getMessage());
            }
        }

        if (!clientesValidos.isEmpty()) {
            clientesCollection.insertMany(clientesValidos);
        }
    }
}
