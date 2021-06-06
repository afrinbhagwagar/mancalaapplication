package com.application.mancala.service;

import java.util.List;

import com.application.mancala.model.MancalaStatus;
import com.application.mancala.model.Pit;
import com.application.mancala.model.Player;
import com.application.mancala.service.exceptions.PitNotFoundException;

public interface MancalaService {

  void createGame();

  void sortPieces(String pitId) throws PitNotFoundException;
  
  List<Pit> getBoardPits();

  MancalaStatus currentStatus();

  Player currentPlayer();

}
