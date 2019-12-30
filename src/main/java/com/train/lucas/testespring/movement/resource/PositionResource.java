package com.train.lucas.testespring.movement.resource;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionResource {

	private static final String CAMPO_ERROR = "Posição inválida";
	@NotNull
	@Min(value = 0, message = CAMPO_ERROR)
	@Max(value = 2, message = CAMPO_ERROR)
	private Integer x;

	@NotNull
	@Min(value = 0, message = CAMPO_ERROR)
	@Max(value = 2, message = CAMPO_ERROR)
	private Integer y;
}
