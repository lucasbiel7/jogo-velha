package com.train.lucas.testespring.movement.api;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.train.lucas.testespring.movement.resource.MoveResource;
import com.train.lucas.testespring.movement.resource.ReponseMovement;
import com.train.lucas.testespring.movement.service.MovementService;

@RestController
@RequestMapping("game")
public class MovementController {

	@Autowired
	private MovementService movementService;

	@PostMapping("{id}/movement")
	public ResponseEntity<ReponseMovement> movement(@PathVariable("id") String id,
			@RequestBody @Valid MoveResource moveResource) {
		try {
			Optional<String> winner = movementService.movement(moveResource);
			return ResponseEntity
					.ok(ReponseMovement.builder().msg(winner.isPresent() ? "Partida Finalizada" : "Jogada realizada")
							.winner(winner.orElse(null)).build());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ReponseMovement.builder().msg(e.getMessage()).build());
		}
	}
}
