package org.example.mongonativo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

public class Videojuego {

    @Id
    private int id;

    @Getter
    @Setter
    private String titulo;
    private String genere;
    private double precio;
    private Integer stock;

    public Videojuego() {}

    public Videojuego(String titulo, String genere, double precio, Integer stock) {
        this.titulo = titulo;
        this.genere = genere;
        this.precio = precio;
        this.stock = stock;
    }
}
