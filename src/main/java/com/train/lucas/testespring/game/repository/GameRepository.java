package com.train.lucas.testespring.game.repository;

import org.springframework.data.repository.CrudRepository;

import com.train.lucas.testespring.game.model.Game;

public interface GameRepository extends CrudRepository<Game, String>{

}
