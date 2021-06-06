package com.application.mancala.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.application.mancala.model.MancalaStatus;
import com.application.mancala.model.Pit;
import com.application.mancala.model.Player;
import com.application.mancala.repository.PitRepository;
import com.application.mancala.repository.PlayerRepository;
import com.application.mancala.service.exceptions.PitNotFoundException;
import com.application.mancala.utilities.MancalaConstants;

@Service
public class MancalaServiceImpl implements MancalaService {

  private String playerA = "PlayerA";
  private String playerB = "PlayerB";

  @Autowired
  private PitRepository pitRepository;

  @Autowired
  private PlayerRepository playerRepository;

  private MancalaStatus status = new MancalaStatus();

  @Override
  public void createGame() {
    List<Pit> fullBoardPits = new ArrayList<>();

    pitRepository.deleteAll();
    playerRepository.deleteAll();

    List<Pit> playerAPits = new ArrayList<>();
    Player player1 = new Player(1, playerA, true);
    for (int i = 0; i < 6; i++) {
      playerAPits.add(new Pit(i, MancalaConstants.MAXSTONES, false));
      player1.addPit(new Pit(i, MancalaConstants.MAXSTONES, false));
    }
    playerAPits.add(new Pit(MancalaConstants.MAINPITINDEX_PLAYERA, 0, true));
    player1.addPit(new Pit(MancalaConstants.MAINPITINDEX_PLAYERA, 0, true));

    List<Pit> playerBPits = new ArrayList<>();
    Player player2 = new Player(2, playerB, false);
    for (int i = 7; i < 13; i++) {
      playerBPits.add(new Pit(i, MancalaConstants.MAXSTONES, false));
      player2.addPit(new Pit(i, MancalaConstants.MAXSTONES, false));
    }
    playerBPits.add(new Pit(MancalaConstants.MAINPITINDEX_PLAYERB, 0, true));
    player2.addPit(new Pit(MancalaConstants.MAINPITINDEX_PLAYERB, 0, true));

    fullBoardPits.addAll(playerAPits);
    fullBoardPits.addAll(playerBPits);

    pitRepository.saveAll(fullBoardPits);
    playerRepository.save(player1);
    playerRepository.save(player2);

  }

  @Override
  public void sortPieces(String pitId) throws PitNotFoundException {
    Player currentPlayer = playerRepository.findByName(playerA);
    if (Integer.parseInt(pitId) > 6)
      currentPlayer = playerRepository.findByName(playerB);
    List<Pit> allBoardPits = pitRepository.findAll();

    Pit selectedPit = pitRepository.findById(Integer.parseInt(pitId))
        .orElseThrow(() -> new PitNotFoundException(Integer.parseInt(pitId)));
    int selectedPitId = selectedPit.getId();
    int selectedPitAmount = selectedPit.getAmountOfStones();
    allBoardPits.get(selectedPitId).setAmountOfStones(0);

    int i = selectedPitId + 1;
    while (selectedPitAmount > 0) {
      int a = i % allBoardPits.size();
      Pit p = pitRepository.findById(a).orElseThrow(() -> new PitNotFoundException(a));
      if (!p.isMainPit()) {
        if (!operationWhenLastStone(p, currentPlayer, selectedPitAmount, allBoardPits)) {
          p.setAmountOfStones(p.getAmountOfStones() + 1);
        }
        selectedPitAmount--;
      } else {
        if (checkIfPitBelongsToPlayer(p, currentPlayer)) {
          p.setAmountOfStones(p.getAmountOfStones() + 1);
          selectedPitAmount--;
        }
      }
      i = i % allBoardPits.size() + 1;
    }
    i--;

    playerTurns(i);

    pitRepository.saveAll(allBoardPits);

    boolean finished = checkGameEnded(currentPlayer);
    status.setGameFinished(finished);
    status.setWinner(getWinner());

  }

  private void playerTurns(int i) {
    if (!(i == MancalaConstants.MAINPITINDEX_PLAYERA || i == MancalaConstants.MAINPITINDEX_PLAYERB)) {
      Player p1 = playerRepository.findByCurrentPlayer(true);
      Player p2 = playerRepository.findByCurrentPlayer(false);

      p1.setCurrentPlayer(false);
      p2.setCurrentPlayer(true);

      playerRepository.save(p1);
      playerRepository.save(p2);
    }
  }

  private String getWinner() {
    String winner = playerA;
    int sumOfPlayerAStones = playerRepository.findByName(playerA).getPits().stream().mapToInt(Pit::getAmountOfStones)
        .reduce(0, (a, b) -> a + b);
    int sumOfPlayerBStones = playerRepository.findByName(playerB).getPits().stream().mapToInt(Pit::getAmountOfStones)
        .reduce(0, (a, b) -> a + b);
    if (sumOfPlayerBStones > sumOfPlayerAStones)
      winner = playerB;
    return winner;
  }

  private boolean checkGameEnded(Player currentPlayer) {
    return currentPlayer.getPits().stream().filter(x -> !x.isMainPit()).noneMatch(x -> x.getAmountOfStones() > 0);
  }

  private boolean operationWhenLastStone(Pit p, Player currentPlayer, int selectedPitAmount, List<Pit> allBoardPits) {
    if (selectedPitAmount == 1 && checkIfPitBelongsToPlayer(p, currentPlayer) && p.getAmountOfStones() == 0) {
      int pitSeq = p.getId();
      int oppositeSideSeq = allBoardPits.size() - 2 - pitSeq;
      if (allBoardPits.get(oppositeSideSeq).getAmountOfStones() > 0) {

        Pit mainPit = currentPlayer.getMainPit();
        allBoardPits.get(mainPit.getId()).setAmountOfStones(allBoardPits.get(mainPit.getId()).getAmountOfStones()
            + allBoardPits.get(oppositeSideSeq).getAmountOfStones() + 1);
        allBoardPits.get(oppositeSideSeq).setAmountOfStones(0);
        allBoardPits.get(pitSeq).setAmountOfStones(0);
        return true;
      }
    }
    return false;
  }

  private boolean checkIfPitBelongsToPlayer(Pit pit, Player currentPlayer) {
    return currentPlayer.getPits().stream().anyMatch(x -> x.getId().equals(pit.getId()));
  }

  @Override
  public List<Pit> getBoardPits() {
    return pitRepository.findAll();
  }

  @Override
  public MancalaStatus currentStatus() {
    return status;
  }

  @Override
  public Player currentPlayer() {
    return playerRepository.findByCurrentPlayer(true);
  }

}
