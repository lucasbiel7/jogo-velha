package com.train.lucas.testespring.game.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.game.model.Player;
import com.train.lucas.testespring.game.repository.GameRepository;

@Service
public class GameService {

	@Autowired
	private GameRepository gameRepository;

	public Game createGame() {
		Game game = new Game();
		game.setFirstPlayer(Player.generateFirstPlayer());
		return gameRepository.save(game);
	}
}
