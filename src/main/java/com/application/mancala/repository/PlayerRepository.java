package com.application.mancala.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.mancala.model.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {

  Player findByName(String name);
  
  Player findByCurrentPlayer(boolean currentPlayer);

}
