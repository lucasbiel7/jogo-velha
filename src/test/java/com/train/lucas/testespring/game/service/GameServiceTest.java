package com.train.lucas.testespring.game.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.game.model.Player;
import com.train.lucas.testespring.game.repository.GameRepository;

@SpringBootTest
public class GameServiceTest {

	private static final String ID_GAME = "teste";

	@Autowired
	private GameService gameService;

	@MockBean
	private GameRepository gameRepository;

	@BeforeEach
	public void setup() {
		Game game = new Game(ID_GAME, Player.O, null);
		when(gameRepository.findById(ArgumentMatchers.anyString())).then(invocation -> {
			if (ID_GAME.equals(invocation.getArguments()[0])) {
				return Optional.of(game);
			}
			return Optional.empty();
		});
		when(gameRepository.save(ArgumentMatchers.any(Game.class))).then(invocation -> {
			Game newGame = null;
			if (invocation.getArguments()[0] instanceof Game) {
				newGame = (Game) invocation.getArguments()[0];
				if (newGame.getId() == null) {
					newGame.setId(UUID.randomUUID().toString());
				}
			}
			return newGame;
		});
	}

	@DisplayName("Testando quando existe game")
	@Test
	public void findGame_thenFindGameExist() {
		Optional<Game> findGame = gameService.findGame(ID_GAME);
		assertNotNull(findGame.get());
	}

	@DisplayName("Testando quando não existe game com o id igual á 1")
	@Test
	public void findGame_thenFindGameNotExist1() {
		Optional<Game> findGame = gameService.findGame("1");
		assertThrows(NoSuchElementException.class, () -> findGame.get());
	}

	@DisplayName("Testando quando não existe game com o id igual á 2")
	@Test
	public void findGame_thenFindGameNotExist2() {
		Optional<Game> findGame = gameService.findGame("2");
		assertThrows(NoSuchElementException.class, () -> findGame.get());
	}

	@DisplayName("Criação de novo jogo")
	@Test
	public void createGame_thenFirstPlayerGenerated() {
		Game game = gameService.createGame();
		assertNotNull(game);
		assertNotNull(game.getFirstPlayer());
	}

	@DisplayName("Verificando se marca o player O como winner ele marca corretamente")
	@Test
	public void createGame_thenWinnerO() {
		Game game = gameService.createGame();
		gameService.winner(Player.O, game);
		assertEquals(Player.O, game.getWinner());
	}

	@DisplayName("Verificando se marca o player X como winner ele marca corretamente")
	@Test
	public void createGame_thenWinnerX() {
		Game game = gameService.createGame();
		gameService.winner(Player.X, game);
		assertEquals(Player.X, game.getWinner());
	}

	@DisplayName("Verificando se marca o player X como winner ele não marca como O")
	@Test
	public void createGame_thenWinnerXAndVerifyO() {
		Game game = gameService.createGame();
		gameService.winner(Player.X, game);
		assertNotEquals(Player.O, game.getWinner());
	}

	@DisplayName("Verificando se marca o player O como winner ele não marca como X")
	@Test
	public void createGame_thenWinnerOAndVerifyX() {
		Game game = gameService.createGame();
		gameService.winner(Player.O, game);
		assertNotEquals(Player.X, game.getWinner());
	}

}
