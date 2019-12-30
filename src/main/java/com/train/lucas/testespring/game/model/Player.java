package com.train.lucas.testespring.game.model;

import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Player {

	X("X"), O("O");

	private final String name;

	@Override
	public String toString() {
		return name;
	}

	public static Player generateFirstPlayer() {
		int sortNumber = new Random().nextInt(2);
		return sortNumber == 1 ? Player.X : Player.O;
	}

}
