package com.train.lucas.testespring.movement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.game.model.Player;
import com.train.lucas.testespring.game.service.GameService;
import com.train.lucas.testespring.movement.model.Movement;
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

	private Set<Movement> movements;

	@BeforeEach
	public void setup() {
		movements = new HashSet<>();
		when(gameService.findGame(ID_TESTE_O)).thenReturn(Optional.of(GAME_START_O));
		when(movementRepository.save(ArgumentMatchers.any())).then(invocation -> {
			Object arg1 = invocation.getArguments()[0];
			if (arg1 instanceof Movement) {
				Movement movement = (Movement) arg1;
				if (Objects.isNull(movement.getId())) {
					movement.setId(new Random().nextInt());
					movements.add(movement);
				}
			}
			return arg1;
		});
		when(movementRepository.findByGame(ArgumentMatchers.any())).then(invocation -> {
			if (invocation.getArguments()[0] instanceof Game) {
				Game game = (Game) invocation.getArguments()[0];
				return movements.stream().filter(movement -> movement.getGame().equals(game))
						.collect(Collectors.toList());
			}
			return Collections.emptyList();
		});
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
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> movementService.movement(moveResource), () -> MovementService.TURNO_INCORRETO);
		assertEquals(e.getMessage(), MovementService.TURNO_INCORRETO);
	}

	@DisplayName("Realizar jogadas na mesma posição")
	@Test
	public void movement_whenPlayInSamePosition() {
		PositionResource position = new PositionResource(0, 0);

		MoveResource moveResource1 = new MoveResource();
		moveResource1.setPlayer("O");
		moveResource1.setPosition(position);
		moveResource1.setId(ID_TESTE_O);

		MoveResource moveResource2 = new MoveResource();
		moveResource2.setPlayer("X");
		moveResource2.setPosition(position);
		moveResource2.setId(ID_TESTE_O);

		assertEquals(movementService.movement(moveResource1), Optional.ofNullable(null));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> movementService.movement(moveResource2), () -> MovementService.POSICAO_JOGADA);
		assertEquals(e.getMessage(), MovementService.POSICAO_JOGADA);

	}
}
