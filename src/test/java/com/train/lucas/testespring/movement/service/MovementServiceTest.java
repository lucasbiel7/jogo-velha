package com.train.lucas.testespring.movement.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.game.model.Player;
import com.train.lucas.testespring.game.service.GameService;
import com.train.lucas.testespring.movement.repository.MovementRepository;
import com.train.lucas.testespring.movement.resource.MoveResource;
import com.train.lucas.testespring.movement.resource.PositionResource;

@SpringBootTest
public class MovementServiceTest {

	private static final String ID_TESTE_O = "ID_TESTE_O";
	private static final String WITHOUT_GAME = "WITHOUT_GAME";

	private final Game GAME_START_O = new Game(ID_TESTE_O, Player.O, null);

	@Autowired
	private MovementService movementService;

	@MockBean
	private GameService gameService;

	@MockBean
	private MovementRepository movementRepository;

	@BeforeEach
	public void setup() {
		when(gameService.findGame(ID_TESTE_O)).thenReturn(Optional.of(GAME_START_O));

	}

	@DisplayName("Tentar jogar um jogo que não existe")
	@Test
	public void movement_whenGameNotExist() {
		MoveResource moveResource = new MoveResource();
		moveResource.setId(WITHOUT_GAME);
		assertThrows(IllegalArgumentException.class, () -> movementService.movement(moveResource),
				() -> MovementService.PARTIDA_NAO_ENCONTRADA);
	}

	@DisplayName("Jogar primeiro movimento na vez de outro jogador")
	@Test
	public void movement_whenOtherPlayerCanBePlay() {
		MoveResource moveResource = new MoveResource();
		moveResource.setPlayer("X");
		moveResource.setPosition(new PositionResource(0, 0));
		moveResource.setId(ID_TESTE_O);
		assertThrows(IllegalArgumentException.class, () -> movementService.movement(moveResource),
				() -> MovementService.TURNO_INCORRETO);
	}

}
