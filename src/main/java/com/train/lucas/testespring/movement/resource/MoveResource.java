package com.train.lucas.testespring.movement.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MoveResource {

	@NotNull
	private String id;
	@NotNull
	private String player;
	@NotNull
	@Valid
	private PositionResource position;

}
