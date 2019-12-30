package com.train.lucas.testespring.movement.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = { "x", "y" })
public class Position {

	@Id
	@GeneratedValue
	private Integer id;
	private Integer x;
	private Integer y;
	@OneToOne(mappedBy = "position")
	private Movement movement;

	public Position(Integer x, Integer y) {
		super();
		this.x = x;
		this.y = y;
	}

}
