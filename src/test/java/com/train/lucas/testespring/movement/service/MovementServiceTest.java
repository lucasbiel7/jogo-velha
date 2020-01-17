package com.train.lucas.testespring.movement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	private static final String ID_GAME_WIN_X = "ID_GAME_X";

	private static final String ID_DRAW_GAME = "ID_DRAW_GAME";

	private final Game GAME_START_O = new Game(ID_TESTE_O, Player.O, null);
	private final Game GAME_DRAW = new Game(ID_DRAW_GAME, Player.X, null);
	private final Game GAME_WIN_X = new Game(ID_GAME_WIN_X, Player.X, null);

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
		when(gameService.findGame(ID_DRAW_GAME)).thenReturn(Optional.of(GAME_DRAW));
		when(gameService.findGame(ID_GAME_WIN_X)).thenReturn(Optional.of(GAME_WIN_X));
		when(gameService.findGame(WITHOUT_GAME)).thenReturn(Optional.empty());
		when(movementRepository.save(ArgumentMatchers.any())).then(invocation -> {
			Object arg1 = invocation.getArguments()[0];
			if (arg1 instanceof Movement) {
				Movement movement = (Movement) arg1;
				if (Objects.isNull(movement.getId())) {
					movement.setId(new Random().nextInt());
					movement.setCreated(new Date());
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
		MoveResource moveResource = new MoveResource(ID_TESTE_O, Player.X.getName(), new PositionResource(0, 0));
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> movementService.movement(moveResource), () -> MovementService.TURNO_INCORRETO);
		assertEquals(e.getMessage(), MovementService.TURNO_INCORRETO);
	}

	@DisplayName("Realizar jogadas na mesma posição")
	@Test
	public void movement_whenPlayInSamePosition() {
		PositionResource position = new PositionResource(0, 0);

		MoveResource moveResource1 = new MoveResource(ID_TESTE_O, Player.O.getName(), position);
		MoveResource moveResource2 = new MoveResource(ID_TESTE_O, Player.X.getName(), position);

		assertEquals(movementService.movement(moveResource1), Optional.empty());
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> movementService.movement(moveResource2), () -> MovementService.POSICAO_JOGADA);
		assertEquals(e.getMessage(), MovementService.POSICAO_JOGADA);
	}

	@DisplayName("Verificando se o jogo marca com empate corretamente")
	@Test
	public void movement_drawGame() {
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Draw start -----------------------------");
		List<MoveResource> moves = Arrays.asList(
				new MoveResource(ID_DRAW_GAME, Player.X.getName(), new PositionResource(0, 0)),
				new MoveResource(ID_DRAW_GAME, Player.O.getName(), new PositionResource(1, 0)),
				new MoveResource(ID_DRAW_GAME, Player.X.getName(), new PositionResource(0, 1)),
				new MoveResource(ID_DRAW_GAME, Player.O.getName(), new PositionResource(0, 2)),
				new MoveResource(ID_DRAW_GAME, Player.X.getName(), new PositionResource(1, 1)),
				new MoveResource(ID_DRAW_GAME, Player.O.getName(), new PositionResource(2, 2)),
				new MoveResource(ID_DRAW_GAME, Player.X.getName(), new PositionResource(1, 2)),
				new MoveResource(ID_DRAW_GAME, Player.O.getName(), new PositionResource(2, 1)),
				new MoveResource(ID_DRAW_GAME, Player.X.getName(), new PositionResource(2, 0)));
		for (int i = 0; i < moves.size(); i++) {
			Optional<String> result = i == moves.size() - 1 ? Optional.of(MovementService.DRAW) : Optional.empty();
			assertEquals(movementService.movement(moves.get(i)).orElse(null), result.orElse(null));
		}
		Logger.getLogger(getClass().getName()).log(Level.INFO, "Draw end -----------------------------");
	}

	/**
	 * Formato do jogo nesse caso
	 * 
	 * |X| |O <br>
	 * | |X|O <br>
	 * | | |X <br>
	 */
	@DisplayName("Ganhando com jogador x na diagonal principal")
	@Test
	public void movement_XWinGame() {
		List<MoveResource> moves = Arrays.asList(
				new MoveResource(ID_GAME_WIN_X, Player.X.getName(), new PositionResource(1, 1)),
				new MoveResource(ID_GAME_WIN_X, Player.O.getName(), new PositionResource(1, 2)),
				new MoveResource(ID_GAME_WIN_X, Player.X.getName(), new PositionResource(0, 1)),
				new MoveResource(ID_GAME_WIN_X, Player.O.getName(), new PositionResource(0, 2)),
				new MoveResource(ID_GAME_WIN_X, Player.X.getName(), new PositionResource(2, 1)));
		for (int i = 0; i < moves.size(); i++) {
			Optional<String> result = i == moves.size() - 1 ? Optional.ofNullable(Player.X.getName())
					: Optional.empty();
			assertEquals(movementService.movement(moves.get(i)).orElse(null), result.orElse(null));
		}
	}

}
