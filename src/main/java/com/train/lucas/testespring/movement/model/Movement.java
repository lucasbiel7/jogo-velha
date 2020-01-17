package com.train.lucas.testespring.movement.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.train.lucas.testespring.game.model.Game;
import com.train.lucas.testespring.game.model.Player;

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
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = { "id" })
public class Movement {

	@Id
	@GeneratedValue
	private Integer id;

	@ManyToOne
	private Game game;

	private Player player;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Position position;

	@CreatedDate
	private Date created;

}
