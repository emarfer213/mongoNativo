package org.example.mongonativo.repository;

import org.example.mongonativo.model.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClienteRepository extends MongoRepository<Cliente, String> {

}
