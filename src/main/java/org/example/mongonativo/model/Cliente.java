package org.example.mongonativo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "clientes")
public class Cliente {

    @Id
    private int id;

    @Getter
    @Setter
    private String nombre;
    private String email;
    private Date fecha_registro;

    public Cliente() {}

    public Cliente(String nombre, String email, Date fecha_registro) {
        this.nombre = nombre;
        this.email = email;
        this.fecha_registro = fecha_registro;
    }
}
