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
    private static Scanner sc = new Scanner(System.in);
    static MongoCollection<Document> clientesCollection;
    static MongoCollection<Document> juegosCollection;
    static MongoCollection<Document> ventaCollection;
    static boolean exit = false;

    static String email = "";

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

    /**
     * metodo auxiliar que imprime por pantalla
     * un menu con las opciones disponibles
     */
    private static void menu() {
        System.out.println("""
                ---------MENU------------
                1. Carga de Datos
                2. Crear un nuevo cliente
                3. Crear un nuevo juego
                4. Realizar venta
                5. Listar compras de un cliente
                6. Listar los juegos en oferta
                7. Salir
                """);
    }

    /**
     * metodo auxiliar que utiliza un bucle para mantener el menu activo
     * y funcional hasta que la opcion de salir sea seleccionada
     */
    private static void bucleMenu() {
        exit = false;
        int opcion = -1;

        while (!exit) {
            menu();

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (InputMismatchException | NumberFormatException e) {
                opcion = -1;
            }
            switch (opcion) {
                case 1:
                    //llamada al metodo que introducira datos predeterminados a la base de datos
                    crearDatosPredefinidos();
                    break;
                case 2:
                    System.out.println("Ingresa el nombre del cliente a crear");
                    String nombre = sc.nextLine();

                    System.out.println("Ingresa el email del cliente a crear");
                    email = sc.nextLine();

                    try {
                        //insertamos el cliente con los datos introducidos
                        clientesCollection.insertOne(crearCliente(nombre, email));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Ingresa el titulo del juego a crear");
                    String titulo = sc.nextLine();

                    System.out.println("Ingresa el genero del juego a crear");
                    String genero = sc.nextLine();

                    System.out.println("Ingresa el precio del juego a crear");
                    Double precio = Double.parseDouble(sc.nextLine());

                    System.out.println("Ingresa el stock del juego a crear");
                    int stock = Integer.parseInt(sc.nextLine());

                    try {
                        //insertamos el juego con los datos introducidos
                        juegosCollection.insertOne(crearJuego(titulo, genero, precio, stock));
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 4:
                    //llama al metodo que procesara toda la operacion de la venta
                    procesarVenta();
                    break;
                case 5:
                    System.out.println("Ingresa el email del cliente que quieres buscar");
                    email = sc.nextLine();

                    //llamada al metodo que lista las compras hechas por un cliente
                    historialDeCliente(email);
                    break;
                case 6:
                    //llamda al metodo que mostrar por pantalla todos los juegos en oferta
                    listaDeOfertas();
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.printf("\n opcion no valida \n");
                    break;
            }
        }
    }

    /**
     * metodo el cual crea un objeto videojuego, recibiendo por parametros sus datos,
     * ademas comprueba que el precio ni el stock sean menores a 0,
     * si es igual o menor a 0 mandara una excepcion
     *
     * @param titulo
     * @param genero
     * @param precio
     * @param stock
     * @return
     */
    private static Document crearJuego(String titulo, String genero, double precio, int stock) {
        if (precio <= 0 || stock <= 0) {
            throw new IllegalArgumentException(
                    "El precio del producto y el valor del stock no puede ser menor o igual a 0"
            );
        }

        if (juegosCollection.find(eq("titulo", titulo)).first() != null) {
            throw new IllegalArgumentException(
                    "Este titulo ya existe"
            );
        }

        return new Document("titulo", titulo)
                .append("genero", genero)
                .append("precio", precio)
                .append("stock", stock);
    }

    /**
     * metodo el cual recogera en una lista todos los objetos juego
     * que queramos crear y que tengan valores validos, mediante un bucle recorrera una matriz
     * de la cual sacara los datos, si alguno es incorrecto ese juego no se creara
     * y pasara al siguiente conjunto de datos
     */
    private static void extraerJuegosValidos() {
        List<Document> juegosValidos = new ArrayList<>();

        String[][] datosJuegos = {
                {"juego1", "accion", "15", "15"},
                {"juego2", "rpg", "20", "1"},
                {"juego3", "puzzles", "5", "25"},
                {"juego4", "shooter", "50", "35"},
                {"juego5", "accion", "60", "50"}
        };

        for (String[] datos : datosJuegos) {
            try {
                juegosValidos.add(
                        crearJuego(
                                datos[0], //titulo
                                datos[1], //genero
                                Double.parseDouble(datos[2]), //precio
                                Integer.parseInt(datos[3]) //stock
                        )
                );
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        //si la lista final no esta vacia, guardara todos los juegos
        if (!juegosValidos.isEmpty()) {
            juegosCollection.insertMany(juegosValidos);
        }
    }

    /**
     * metodo el cual creara un objeto cliente con los datos introducidos por parametros
     * este comprobara primero si los datos son correctos, si no lo son enviara un mensaje de error
     *
     * @param nombre
     * @param email
     * @return
     */
    private static Document crearCliente(String nombre, String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException(
                    "Email inválido: " + email
            );
        }

        if (clientesCollection.find(eq("email", email)).first()  != null) {
            throw new IllegalArgumentException("Email ya existente");
        }

        return new Document("nombre", nombre)
                .append("email", email)
                .append("fecha_registro", LocalDate.now());
    }

    /**
     * metodo el cual recogera en una lista todos los objetos cliente
     * que queramos crear y que tengan valores validos, mediante un bucle recorrera una matriz
     * de la cual sacara los datos, si alguno es incorrecto ese cliente no se creara
     * y pasara al siguiente conjunto de datos
     */
    private static void extraerClientesValidos() {
        List<Document> clientesValidos = new ArrayList<>();

        String[][] datosClientes = {
                {"Ana", "email1@gmail.com"},
                {"Paco", "email2gmail.com"}
        };

        for (String[] datos : datosClientes) {
            try {
                clientesValidos.add(crearCliente(datos[0], datos[1]));
            } catch (IllegalArgumentException e) {
                System.out.println("Cliente descartado: " + e.getMessage());
            }
        }

        //si la lista resultante no esta vacia se guardaran los datos
        if (!clientesValidos.isEmpty()) {
            clientesCollection.insertMany(clientesValidos);
        }
    }

    /**
     * metodo que borrara todos los datos de las tablas pre existentes
     * y carganara los datos nuevos llamando a los metodos auxiliares
     * definidos preciamente
     */
    private static void crearDatosPredefinidos() {
        clientesCollection.drop();
        juegosCollection.drop();

        extraerClientesValidos();
        extraerJuegosValidos();
    }

    /**
     * metodo el cual realizara toda la logica de procesar la venta,
     * comprobara que no tenga datos incorrectos, utilizaremos dos objetos para guardar los datos
     * del juego y cliente que tengan los datos metidos por parametro
     */
    private static void procesarVenta() {

        System.out.println("Ingresa el email del cliente que realizara la compra");
        String emailCliente = sc.nextLine();

        //buscamos el cliente relacionado con el email
        Document cliente = clientesCollection.find(eq("email", emailCliente)).first();

        //comprobamos que el cliente exista
        if (cliente == null) {
            System.out.println("cliente no encontrado");
            return;
        }

        System.out.println("Ingresa el nombre del juego que comprara");
        String tituloJuego = sc.nextLine();

        //buscamos el juego que tenga el nombre introducido
        Document juego = juegosCollection.find(eq("titulo", tituloJuego)).first();

        //comprobamos que el juego existe
        if (juego == null) {
            System.out.println("juego no encontrado");
            return;
        }

        //comprobamos que el stock no sea cero o menor
        if (juego.getInteger("stock") <= 0) {
            System.out.println("stock del juego agotado");
            return;
        }

        //creamos un objeto venta con los datos correspondientes
        Document venta = new Document("fecha", LocalDate.now())
                .append("cliente_id", cliente.getObjectId("_id"))
                .append("juego_id", juego.getObjectId("_id"))
                .append("titulo_snapshot", juego.getString("titulo"))
                .append("precio_snapshot", juego.getDouble("precio"));

        //guardamos en la base de datos la venta
        ventaCollection.insertOne(venta);

        //actualizamos el stock del juego restandole 1
        juegosCollection.updateOne(
                eq("_id", juego.getObjectId("_id")),
                inc("stock", -1)
        );

        System.out.println("\nVenta realizada correctamente");
    }

    /**
     * metodo que usaremos para mostrar por pantalla todas las compras
     * que un cliente ha realizado, sacaremos la informacion del cliente que contenga
     * el email pasado por parametro e imprimiremos por pantalla el nombre del juego y el precio
     *
     * @param emailCliente
     */
    private static void historialDeCliente(String emailCliente) {
        //creamos un objeto cliente con los datos del cliente propietario del email
        Document cliente = clientesCollection.find(eq("email", emailCliente)).first();

        //comprobamos que el usuario exista, si no lo hace la operacion no se realiza
        if (cliente == null) {
            System.out.println("cliente no encontrado");
            return;
        }

        //sacamos la id del cliente para utilizarla  para buscar las ventas que contengan dicha id
        ObjectId clienteID = cliente.getObjectId("_id");

        //recogemos todas las compras que contengan dicho id de cliente
        FindIterable<Document> compras = ventaCollection.find(
                eq("cliente_id", clienteID)
        );

        //imprimiremos por pantalla todas y cada una de dichas ventas
        System.out.println("Compras de " + emailCliente + ":");
        for (Document v : compras) {
            System.out.println("Titulo: " + v.getString("titulo_snapshot")
                    + "\nPrecio :" + v.getDouble("precio_snapshot") + "€\n");
        }
    }

    /**
     * clase la cual listara todos los videojuegos con un precio menor de 25
     * ademas solamente mostrara el titulo y precio del videojuego,
     * excluyendo su id
     */
    private static void listaDeOfertas() {
        System.out.println("Los juegos que se encuentran en oferta son: ");
        juegosCollection.find(lt("precio", 25.00))
                .projection(Projections.fields(include("titulo", "precio"), exclude("_id")))
                .forEach(doc -> System.out.println(doc.toJson()));
    }
}