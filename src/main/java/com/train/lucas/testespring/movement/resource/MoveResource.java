package com.train.lucas.testespring.movement.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveResource {

	@NotNull
	private String id;
	@NotNull
	private String player;
	@NotNull
	@Valid
	private PositionResource position;

}
