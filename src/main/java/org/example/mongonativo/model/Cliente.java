package org.example.mongonativo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;

@Document(collection = "clientes")
public class Cliente {

    @Id
    private String id;

    @Getter
    @Setter
    private String nombre;
    private String email;
    private LocalDate fecha_registro;

    public Cliente() {}

    public Cliente(String nombre, String email, LocalDate fecha_registro) {
        this.nombre = nombre;
        this.email = email;
        this.fecha_registro = fecha_registro;
    }
}
