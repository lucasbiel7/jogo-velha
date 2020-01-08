package com.train.lucas.testespring.game.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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

		Mockito.when(gameRepository.findById(ArgumentMatchers.anyString())).then(invocation -> {
			if (ID_GAME.equals(invocation.getArguments()[0])) {
				return Optional.of(game);
			}
			return Optional.ofNullable(null);
		});
	}

	@DisplayName("Testando quando existe game")
	@Test
	public void findGame_thenFindGameExist() {
		Optional<Game> findGame = gameService.findGame(ID_GAME);
		assertNotNull(findGame.get());
	}

	@Test
	public void findGame_thenFindGameNotExist1() {
		Optional<Game> findGame = gameService.findGame("1");
		assertThrows(NoSuchElementException.class, () -> findGame.get());
	}

	@Test
	public void findGame_thenFindGameNotExist2() {
		Optional<Game> findGame = gameService.findGame("2");
		assertThrows(NoSuchElementException.class, () -> findGame.get());
	}
}
