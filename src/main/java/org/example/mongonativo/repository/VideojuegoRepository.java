package org.example.mongonativo.repository;

import org.example.mongonativo.model.Videojuego;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideojuegoRepository extends MongoRepository<Videojuego, String> {

}
