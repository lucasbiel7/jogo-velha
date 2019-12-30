package com.train.lucas.testespring.movement.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.game.model.Player;
import com.train.lucas.testespring.game.repository.GameRepository;
import com.train.lucas.testespring.movement.model.Movement;
import com.train.lucas.testespring.movement.model.Position;
import com.train.lucas.testespring.movement.repository.MovementRepository;
import com.train.lucas.testespring.movement.resource.MoveResource;

@Service
public class MovementService {

	@Autowired
	private MovementRepository movementRepository;

	@Autowired
	private GameRepository gameRepository;

	public Optional<String> movement(MoveResource moveResource) {
		Game game = gameRepository.findById(moveResource.getId())
				.orElseThrow(() -> new IllegalArgumentException("Partida não encontrada"));
		List<Movement> movements = movementRepository.findByGame(game).stream()
				.sorted((move1, move2) -> move1.getCreated().compareTo(move2.getCreated()) * -1)
				.collect(Collectors.toList());
		Player actualPlayer = Player.valueOf(moveResource.getPlayer());
		validateTurnoOfPlayer(game, actualPlayer, movements);
		Movement movement = createMovement(moveResource, game, actualPlayer);
		validatePosition(movements, movement.getPosition());
		movements.add(0, movementRepository.save(movement));
		return validateWinner(movements);
	}

	private Optional<String> validateWinner(List<Movement> movements) {
		Optional<String> optional = Optional.ofNullable(null);
		Optional<Movement> ultimaJogada = movements.stream().findFirst();
		Integer x = ultimaJogada.get().getPosition().getX();
		Integer y = ultimaJogada.get().getPosition().getY();
		int linha = 0, coluna = 0, diagonalPrincipal = 0, diagonalSecundaria = 0;

		for (int i = 0; i < 3; i++) {

			Position positionLinha = new Position(x, i);
			linha += hasPosition(positionLinha, movements);

			Position positionColuna = new Position(i, y);
			coluna += hasPosition(positionColuna, movements);

			if (x == y) {
				Position positionDiagonPrincipal = new Position(i, i);
				diagonalPrincipal += hasPosition(positionDiagonPrincipal, movements);
			}

			if (x + y == 2) {
				Position positionDiagonalSecundaria = new Position(2 - i, i);
				diagonalSecundaria += hasPosition(positionDiagonalSecundaria, movements);
			}
		}

		if (Arrays.asList(linha, coluna, diagonalPrincipal, diagonalSecundaria).stream()
				.anyMatch(Integer.valueOf(3)::equals)) {
			optional = Optional.of(ultimaJogada.get().getPlayer().getName());
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
		Supplier<IllegalArgumentException> error = () -> new IllegalArgumentException("Não é o turno do jogador");
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
			throw new IllegalArgumentException("Já foi realizado uma jogada nessa posição!");
		}
	}

}
