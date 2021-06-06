package com.application.mancala.model;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Pit {

  @Id
  private Integer id;

  private int amountOfStones;
  private boolean mainPit;

  @OneToOne
  @JsonIgnore
  private Player player;

  public Pit(int id, int amountOfStones, boolean mainPit) {
    this.id = id;
    this.amountOfStones = amountOfStones;
    this.mainPit = mainPit;
  }

  public Pit() {}

}
