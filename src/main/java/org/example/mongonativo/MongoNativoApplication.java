package org.example.mongonativo;

import org.example.mongonativo.model.Cliente;
import org.example.mongonativo.model.Videojuego;
import org.example.mongonativo.repository.ClienteRepository;
import org.example.mongonativo.repository.VentaRepository;
import org.example.mongonativo.repository.VideojuegoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Date;

@SpringBootApplication
public class MongoNativoApplication implements CommandLineRunner {

    @Autowired
    private VideojuegoRepository repositorioJuegos;
    @Autowired
    private VentaRepository repositorioVentas;
    @Autowired
    private ClienteRepository repositorioClientes;

    public static void main(String[] args) {
        SpringApplication.run(MongoNativoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("--- INICIANDO MONGO NATIVO ---");

        repositorioJuegos.deleteAll();
        repositorioClientes.deleteAll();
        repositorioVentas.deleteAll();

        repositorioJuegos.save(new Videojuego("Zelda 1", "rpg", 35.75, 40));
        repositorioJuegos.save(new Videojuego("Zelda 2", "rpg", 26.75, 2));
        repositorioJuegos.save(new Videojuego("Zelda 3", "rpg", 10.75, 15));
        repositorioJuegos.save(new Videojuego("Zelda 4", "rpg", 60.75, 31));
        repositorioJuegos.save(new Videojuego("Zelda 5", "rpg", 70.75, 50));

        repositorioClientes.save(new Cliente("juan", "correo1@gmail.com", LocalDate.now()));
        repositorioClientes.save(new Cliente("ana", "correo1@gmail.com",  LocalDate.now()));
    }
}
