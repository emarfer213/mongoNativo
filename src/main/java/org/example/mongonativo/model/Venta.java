package org.example.mongonativo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class Venta {

    @Getter
    @Setter
    private Date fecha;
    private int cliente_id;
    private int juego_id;
    private String titulo_snapshot;
    private double precio_snapshot;

    public Venta() {}

    public Venta(Date fecha, int cliente_id, int juego_id, String titulo_snapshot, double precio_snapshot) {
        this.fecha = fecha;
        this.cliente_id = cliente_id;
        this.juego_id = juego_id;
        this.titulo_snapshot = titulo_snapshot;
        this.precio_snapshot = precio_snapshot;
    }
}
