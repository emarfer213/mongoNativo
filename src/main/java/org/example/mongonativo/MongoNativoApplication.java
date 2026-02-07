package org.example.mongonativo;

import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.inc;

public class MongoNativoApplication {
    private static Scanner input = new Scanner(System.in);
    static MongoCollection<Document> clientesCollection;
    static MongoCollection<Document> juegosCollection;
    static MongoCollection<Document> ventaCollection;

    public static void main(String[] args) {

        String conectionString = "mongodb://localhost:27017";

        try (MongoClient mongoClient = MongoClients.create(conectionString)) {
            MongoDatabase database = mongoClient.getDatabase("tienda_gaming");
            clientesCollection = database.getCollection("clientes");
            juegosCollection = database.getCollection("videjuegos");
            ventaCollection = database.getCollection("ventas");

            bucleMenu();

        }
    }

    private static void menu() {
        System.out.println("""
                ---------MENU------------
                1. Carga de Datos
                2. Realizar venta
                3. Listar compras de un cliente
                4. Listar los juegos en oferta
                5. Salir
                """);
    }

    private static void bucleMenu() {
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
                    procesarVenta("email1@gmail.com", "juego2");
                    break;
                case 3:
                    historialDeCliente("email1@gmail.com");
                    break;
                case 4:
                    listaDeOfertas();
                    break;
                case 5:
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
                {"juego1", "accion", "15", "15"},
                {"juego2", "rpg", "20", "1"},
                {"juego3", "puzzles", "0", "25"},
                {"juego4", "shooter", "50", "35"},
                {"juego5", "accion", "60", "50"}
        };

        for (String[] datos : datosJuegos) {
            try {
                juegosValidos.add(
                        crearJuego(
                                datos[0],
                                datos[1],
                                Double.parseDouble(datos[2]),
                                Integer.parseInt(datos[3])
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

    private static void procesarVenta(String emailCliente, String tituloJuego){
        Document cliente = clientesCollection.find(eq("email", emailCliente)).first();
        if (cliente == null){
            System.out.println("cliente no encontrado");
            return;
        }

        Document juego = juegosCollection.find(eq("titulo", tituloJuego)).first();
        if (juego == null){
            System.out.println("juego no encontrado");
            return;
        }

        if (juego.getInteger("stock") <= 0){
            System.out.println("stock del juego agotado");
            return;
        }

        Document venta = new Document("fecha", LocalDate.now())
                .append("cliente_id", cliente.getObjectId("_id"))
                .append("juego_id", juego.getObjectId("_id"))
                .append("titulo_snapshot", juego.getString("titulo"))
                .append("precio_snapshot", juego.getDouble("precio"));

        ventaCollection.insertOne(venta);

        juegosCollection.updateOne(
                eq("_id", juego.getObjectId("_id")),
                inc("stock", -1)
        );

        System.out.println("Venta realizada correctamente");
    }

    private static void historialDeCliente(String emailCliente){
        Document cliente = clientesCollection.find(eq("email", emailCliente)).first();
        if (cliente == null){
            System.out.println("cliente no encontrado");
            return;
        }

        ObjectId clienteID = cliente.getObjectId("_id");

        FindIterable<Document> compras = ventaCollection.find(
                eq("cliente_id", clienteID)
        );

        System.out.println("Compras de " + emailCliente + ":");
        for (Document v : compras) {
            System.out.println("Titulo: " + v.getString("titulo_snapshot")
                    + "\nPrecio :" + v.getDouble("precio_snapshot") + "€\n");
        }
    }

    private static void listaDeOfertas(){
        System.out.println("Los juegos que se encuentran en oferta son: ");
        juegosCollection.find(lt("precio", 25.00))
                .projection(Projections.fields(include("titulo", "precio"),exclude("_id")))
                .forEach(doc -> System.out.println("\n" + doc.toJson()));
    }
}
