package com.train.lucas.testespring.game.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.game.service.GameService;

@RestController
@RequestMapping("game")
public class GameController {

	@Autowired
	private GameService gameService;

	@PostMapping()
	public ResponseEntity<Game> createGame() {
		return ResponseEntity.ok(gameService.createGame());
	}

}
