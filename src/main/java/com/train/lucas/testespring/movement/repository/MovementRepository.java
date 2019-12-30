package com.train.lucas.testespring.movement.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.movement.model.Movement;

public interface MovementRepository extends CrudRepository<Movement, Integer> {

	List<Movement> findByGame(Game game);

}
