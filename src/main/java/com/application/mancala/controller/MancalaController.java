package com.application.mancala.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.application.mancala.model.MancalaStatus;
import com.application.mancala.model.Pit;
import com.application.mancala.model.Player;
import com.application.mancala.service.MancalaService;
import com.application.mancala.service.exceptions.PitNotFoundException;

@RestController
@RequestMapping("/mancala")
public class MancalaController {

  @Autowired
  private MancalaService mancalaService;

  private Logger logger = LoggerFactory.getLogger(MancalaController.class);

  /**
   * Creates Game with 2 players and their pits accordingly.
   */
  @PostMapping
  @ResponseStatus(code = HttpStatus.CREATED)
  public void createGame() {
    mancalaService.createGame();
  }

  /**
   * Pits are calculated based on pit id. Result would be amount of stones in PitRepository.
   * 
   * @param pitId pitId.
   * @throws PitNotFoundException if that pit id is not found.
   */
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PutMapping(value = "/pits/{pitId}")
  public void sortPieces(@PathVariable String pitId) throws PitNotFoundException {
    logger.info("Input PitId : {}", pitId);
    mancalaService.sortPieces(pitId);
  }

  /**
   * Gets all pit values which has number of stones.
   * 
   * @return
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/pits")
  public List<Pit> getEntireBoardPits() {
    return mancalaService.getBoardPits();
  }

  /**
   * Gets the status if the game is finished or not. When true, UI can fetch the winner name.
   * 
   * @return MancalaStatus
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/status")
  public MancalaStatus getCurrentStatus() {
    return mancalaService.currentStatus();
  }

  /**
   * Gets the current player and accordingly UI would disable the pits from opposite member. Only pits of this player
   * would be enabled to be clicked.
   * 
   * @return Player
   */
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/currentplayer")
  public Player getCurrentPlayer() {
    return mancalaService.currentPlayer();
  }

}
