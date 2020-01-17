package com.train.lucas.testespring.movement.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.game.model.Player;
import com.train.lucas.testespring.game.service.GameService;
import com.train.lucas.testespring.movement.model.Movement;
import com.train.lucas.testespring.movement.model.Position;
import com.train.lucas.testespring.movement.repository.MovementRepository;
import com.train.lucas.testespring.movement.resource.MoveResource;

@Service
public class MovementService {

	public static final String DRAW = "Draw";

	public static final String POSICAO_JOGADA = "Já foi realizado uma jogada nessa posição!";

	public static final String TURNO_INCORRETO = "Não é o turno do jogador";

	public static final String PARTIDA_NAO_ENCONTRADA = "Partida não encontrada";

	private static final int DIMENSION_LIMIT = 2;

	@Autowired
	private MovementRepository movementRepository;

	@Autowired
	private GameService gameService;

	public Optional<String> movement(MoveResource moveResource) {
		Game game = gameService.findGame(moveResource.getId())
				.orElseThrow(() -> new IllegalArgumentException(PARTIDA_NAO_ENCONTRADA));
		if (Objects.nonNull(game.getWinner())) {
			return Optional.of(game.getWinner().getName());
		}
		List<Movement> movements = movementRepository.findByGame(game).stream()
				.sorted((move1, move2) -> move1.getCreated().compareTo(move2.getCreated()) * -1)
				.collect(Collectors.toList());
		Player actualPlayer = Player.valueOf(moveResource.getPlayer());
		validateTurnoOfPlayer(game, actualPlayer, movements);
		Movement movement = createMovement(moveResource, game, actualPlayer);
		validatePosition(movements, movement.getPosition());
		movements.add(0, movementRepository.save(movement));
//		paint(movements);
		return validateWinner(movements, game);
	}

	private void paint(List<Movement> movements) {
		System.out.println("--------------------------------------------------------------------");
		for (int i = 0; i <= DIMENSION_LIMIT; i++) {
			for (int j = 0; j <= DIMENSION_LIMIT; j++) {
				Position position = new Position(i, j);
				Optional<Movement> move = movements.stream().filter(movement -> movement.getPosition().equals(position))
						.findFirst();
				System.out.print("|");
				move.map(Movement::getPlayer).ifPresentOrElse(System.out::print, () -> System.out.print(" "));
			}
			System.out.println();
		}

	}

	private Optional<String> validateWinner(List<Movement> movements, Game game) {
		Optional<String> optional = Optional.empty();
		Optional<Movement> ultimaJogada = movements.stream().findFirst();
		int numberOfMovements = movements.size();
		movements = movements.stream().filter(movement -> movement.getPlayer().equals(ultimaJogada.get().getPlayer()))
				.collect(Collectors.toList());
		Integer x = ultimaJogada.get().getPosition().getX();
		Integer y = ultimaJogada.get().getPosition().getY();
		int linha = 0, coluna = 0, diagonalPrincipal = 0, diagonalSecundaria = 0;

		for (int i = 0; i <= DIMENSION_LIMIT; i++) {

			Position positionLinha = new Position(x, i);
			linha += hasPosition(positionLinha, movements);

			Position positionColuna = new Position(i, y);
			coluna += hasPosition(positionColuna, movements);

			if (x == y) {
				Position positionDiagonPrincipal = new Position(i, i);
				diagonalPrincipal += hasPosition(positionDiagonPrincipal, movements);
			}

			if (x + y == DIMENSION_LIMIT) {
				Position positionDiagonalSecundaria = new Position(DIMENSION_LIMIT - i, i);
				diagonalSecundaria += hasPosition(positionDiagonalSecundaria, movements);
			}
		}

		if (Arrays.asList(linha, coluna, diagonalPrincipal, diagonalSecundaria).stream()
				.anyMatch(Integer.valueOf(3)::equals)) {
			optional = Optional.of(ultimaJogada.get().getPlayer().getName());
			gameService.winner(ultimaJogada.get().getPlayer(), game);
		}

		if (numberOfMovements == 9 && optional.isEmpty()) {
			optional = Optional.of(DRAW);
		}

		return optional;
	}

	private int hasPosition(Position position, List<Movement> movements) {
		return movements.stream().map(Movement::getPosition).anyMatch(position::equals) ? 1 : 0;
	}

	private Movement createMovement(MoveResource moveResource, Game game, Player actualPlayer) {
		Position position = new Position();
		position.setX(moveResource.getPosition().getX());
		position.setY(moveResource.getPosition().getY());
		Movement movement = new Movement();
		movement.setGame(game);
		movement.setPlayer(actualPlayer);
		movement.setPosition(position);
		return movement;
	}

	private void validateTurnoOfPlayer(Game game, Player actualPlayer, List<Movement> movements) {
		Optional<Movement> last = movements.stream().findFirst();
		Supplier<IllegalArgumentException> error = () -> new IllegalArgumentException(TURNO_INCORRETO);
		if (last.isPresent()) {
			if (last.get().getPlayer().equals(actualPlayer)) {
				throw error.get();
			}
		} else {
			if (!game.getFirstPlayer().equals(actualPlayer)) {
				throw error.get();
			}
		}
	}

	private void validatePosition(List<Movement> movements, Position position) {
		if (movements.stream().map(Movement::getPosition).anyMatch(position::equals)) {
			throw new IllegalArgumentException(POSICAO_JOGADA);
		}
	}

}
