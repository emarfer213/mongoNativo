package org.example.mongonativo.repository;

import org.example.mongonativo.model.Venta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VentaRepository extends MongoRepository<Venta, String> {
}
