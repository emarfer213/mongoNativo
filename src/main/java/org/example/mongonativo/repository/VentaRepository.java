package org.example.mongonativo.repository;

import org.example.mongonativo.model.Venta;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VentaRepository extends MongoRepository<Venta, String> {

    List<Venta> findByCliente(String email);
}
