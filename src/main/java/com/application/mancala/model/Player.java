package com.application.mancala.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "PLAYER")
public class Player {

  @Id
  private Integer id;
  private String name;

  @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY, targetEntity = Pit.class)
  @JsonIgnore
  private List<Pit> pits;
  
  private boolean currentPlayer;

  public Player() {}

  public Player(int id, String name, boolean currentPlayer) {
    this.id = id;
    this.name = name;
    this.currentPlayer = currentPlayer;
  }

  public boolean equals(Player compare) {
    return this.id.equals(compare.getId());
  }

  public Pit getMainPit() {
    return pits.stream().filter(x -> x.isMainPit()).findFirst().get();
  }

  public List<Pit> getPits() {
    if (this.pits == null) {
      this.pits = new ArrayList<>();
    }
    return this.pits;
  }

  public void addPit(Pit pit) {
    getPits().add(pit);
    pit.setPlayer(this);
  }

}
