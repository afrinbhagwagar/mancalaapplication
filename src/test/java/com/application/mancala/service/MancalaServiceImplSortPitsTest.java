package com.application.mancala.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.application.mancala.model.Pit;
import com.application.mancala.model.Player;
import com.application.mancala.repository.PitRepository;
import com.application.mancala.repository.PlayerRepository;
import com.application.mancala.service.MancalaService;
import com.application.mancala.service.MancalaServiceImpl;
import com.application.mancala.service.exceptions.PitNotFoundException;

@RunWith(SpringRunner.class)
public class MancalaServiceImplSortPitsTest {

  @TestConfiguration
  static class MancalaServiceImplTestConfiguration {

    @Bean
    public MancalaService mancalaService() {
      return new MancalaServiceImpl();
    }

  }

  @MockBean
  private PitRepository pitRepository;

  @MockBean
  private PlayerRepository playerRepository;

  @Autowired
  private MancalaService mancalaService;

  /**
   * Below test case initiates pit repository with default amount of stones and then with pitId example as 2 and then 11
   * checks if each pit has correct amount of stones. Also which player next turn would be is verified.
   * 
   * @throws PitNotFoundException if PitId not found
   */
  @Test
  public void testSortPiecesWithDifferentInputsSequentially() throws PitNotFoundException {

    List<Pit> pitsList = samplePitsSavedInRepository();
    Mockito.when(pitRepository.findAll()).thenReturn(pitsList);
    when(pitRepository.findById(any(Integer.class))).thenAnswer(invocation -> {
      Integer id = invocation.getArgument(0, Integer.class);
      return Optional.of(pitsList.get(id));

    });

    Player playerA = createPlayerA(pitsList);
    Player playerB = createPlayerB(pitsList);

    when(playerRepository.findByName("PlayerA")).thenReturn(playerA);
    when(playerRepository.findByName("PlayerB")).thenReturn(playerB);

    when(playerRepository.findByCurrentPlayer(true)).thenReturn(playerA).thenReturn(playerB);
    when(playerRepository.findByCurrentPlayer(false)).thenReturn(playerB).thenReturn(playerA);

    when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> playerA).thenAnswer(invocation -> playerB);

    mancalaService.sortPieces("2");
    int[] expectedIteration1 = {6, 6, 0, 7, 7, 7, 1, 7, 7, 6, 6, 6, 6, 0};
    int[] actualIteration1 = pitRepository.findAll().stream().mapToInt(Pit::getAmountOfStones).toArray();
    assertTrue(Arrays.equals(expectedIteration1, actualIteration1));
    assertEquals(false, playerRepository.findByName("PlayerA").isCurrentPlayer());
    assertEquals(true, playerRepository.findByName("PlayerB").isCurrentPlayer());

    mancalaService.sortPieces("11");
    int[] expectedIteration2 = {7, 7, 1, 8, 7, 7, 1, 7, 7, 6, 6, 0, 7, 1};
    int[] actualIteration2 = pitRepository.findAll().stream().mapToInt(Pit::getAmountOfStones).toArray();
    assertTrue(Arrays.equals(expectedIteration2, actualIteration2));
    assertEquals(true, playerRepository.findByName("PlayerA").isCurrentPlayer());
    assertEquals(false, playerRepository.findByName("PlayerB").isCurrentPlayer());
  }

  private Player createPlayerB(List<Pit> pitsList) {
    Player playerB = new Player(2, "PlayerB", false);
    List<Pit> pitsB = new ArrayList<>();
    for (int i = 7; i < 14; i++)
      pitsB.add(pitsList.get(i));
    playerB.setPits(pitsB);
    return playerB;
  }

  private Player createPlayerA(List<Pit> pitsList) {
    Player playerA = new Player(1, "PlayerA", true);
    List<Pit> pitsA = new ArrayList<>();
    for (int i = 0; i < 7; i++)
      pitsA.add(pitsList.get(i));
    playerA.setPits(pitsA);
    return playerA;
  }

  /**
   * Below test case is an exception scenario when input pit id is not found in repository. Here pitRepository is not
   * passed any arraylist as size would be defined.
   * 
   * @throws PitNotFoundException if pitId is not found
   */
  @Test(expected = PitNotFoundException.class)
  public void testSortPitsWhenIncorrectPitId() throws PitNotFoundException {
    mancalaService.sortPieces("18");
    verify(playerRepository, times(1)).findByName(any(String.class));
    verify(pitRepository, times(1)).findAll();
    verify(pitRepository, times(1)).findById(any(Integer.class));
  }

  private List<Pit> samplePitsSavedInRepository() {
    List<Pit> fullBoardPits = new ArrayList<>();
    List<Pit> playerAPits = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      playerAPits.add(new Pit(i, 6, false));
    }
    playerAPits.add(new Pit(6, 0, true));

    List<Pit> playerBPits = new ArrayList<>();
    for (int i = 7; i < 13; i++) {
      playerBPits.add(new Pit(i, 6, false));
    }
    playerBPits.add(new Pit(13, 0, true));

    fullBoardPits.addAll(playerAPits);
    fullBoardPits.addAll(playerBPits);
    return fullBoardPits;
  }

  /**
   * Below test case includes scenario when last stone is in your own pit and that pit was empty initially. In this case
   * sum of your pit and opposite pit values are added to main pit of that player.
   * 
   * @throws PitNotFoundException if exception
   */
  @Test
  public void testWhenLastStoneIsInYourOwnPitWithAmtZero() throws PitNotFoundException {
    List<Pit> pitsList = samplePitValuesWhenLastStoneInOwnPit();
    Mockito.when(pitRepository.findAll()).thenReturn(pitsList);
    when(pitRepository.findById(any(Integer.class))).thenAnswer(invocation -> {
      Integer id = invocation.getArgument(0, Integer.class);
      return Optional.of(pitsList.get(id));

    });

    Player playerA = createPlayerA(pitsList);
    Player playerB = createPlayerB(pitsList);

    when(playerRepository.findByName("PlayerA")).thenReturn(playerA);
    when(playerRepository.findByName("PlayerB")).thenReturn(playerB);
    when(playerRepository.findByCurrentPlayer(true)).thenReturn(playerB);
    when(playerRepository.findByCurrentPlayer(false)).thenReturn(playerA);
    mancalaService.sortPieces("9");

    int[] expectedIteration1 = {1, 0, 0, 9, 6, 0, 20, 0, 3, 0, 0, 0, 8, 25};
    int[] actualIteration1 = pitRepository.findAll().stream().mapToInt(Pit::getAmountOfStones).toArray();
    assertTrue(Arrays.equals(expectedIteration1, actualIteration1));
    assertEquals(true, playerRepository.findByName("PlayerA").isCurrentPlayer());
    assertEquals(false, playerRepository.findByName("PlayerB").isCurrentPlayer());

  }

  private List<Pit> samplePitValuesWhenLastStoneInOwnPit() {
    List<Pit> fullBoardPits = new ArrayList<>();
    fullBoardPits.add(new Pit(0, 1, false));
    fullBoardPits.add(new Pit(1, 0, false));
    fullBoardPits.add(new Pit(2, 7, false));
    fullBoardPits.add(new Pit(3, 9, false));
    fullBoardPits.add(new Pit(4, 6, false));
    fullBoardPits.add(new Pit(5, 0, false));
    fullBoardPits.add(new Pit(6, 20, true));
    fullBoardPits.add(new Pit(7, 0, false));
    fullBoardPits.add(new Pit(8, 3, false));
    fullBoardPits.add(new Pit(9, 1, false));
    fullBoardPits.add(new Pit(10, 0, false));
    fullBoardPits.add(new Pit(11, 0, false));
    fullBoardPits.add(new Pit(12, 8, false));
    fullBoardPits.add(new Pit(13, 17, true));
    return fullBoardPits;
  }

  /**
   * Below test case includes scenarios when last stone is in your own main pit. In that case player's turn does not
   * change.
   * 
   * @throws PitNotFoundException if exception
   */
  @Test
  public void testWhenLastStoneInOwnMainPit() throws PitNotFoundException {
    List<Pit> pitsList = samplePitValuesWhenLastStoneInOwnMainPit();
    Mockito.when(pitRepository.findAll()).thenReturn(pitsList);
    when(pitRepository.findById(any(Integer.class))).thenAnswer(invocation -> {
      Integer id = invocation.getArgument(0, Integer.class);
      return Optional.of(pitsList.get(id));

    });

    Player playerA = createPlayerA(pitsList);
    Player playerB = createPlayerB(pitsList);

    when(playerRepository.findByName("PlayerA")).thenReturn(playerA);
    when(playerRepository.findByName("PlayerB")).thenReturn(playerB);
    when(playerRepository.findByCurrentPlayer(true)).thenReturn(playerA);
    when(playerRepository.findByCurrentPlayer(false)).thenReturn(playerB);
    mancalaService.sortPieces("2");

    int[] expectedIteration1 = {5, 1, 0, 7, 4, 8, 13, 5, 2, 0, 0, 5, 6, 16};
    int[] actualIteration1 = pitRepository.findAll().stream().mapToInt(Pit::getAmountOfStones).toArray();
    assertTrue(Arrays.equals(expectedIteration1, actualIteration1));
    assertEquals(true, playerRepository.findByName("PlayerA").isCurrentPlayer());
    assertEquals(false, playerRepository.findByName("PlayerB").isCurrentPlayer());
  }

  private List<Pit> samplePitValuesWhenLastStoneInOwnMainPit() {
    List<Pit> fullBoardPits = new ArrayList<>();
    fullBoardPits.add(new Pit(0, 5, false));
    fullBoardPits.add(new Pit(1, 1, false));
    fullBoardPits.add(new Pit(2, 4, false));
    fullBoardPits.add(new Pit(3, 6, false));
    fullBoardPits.add(new Pit(4, 3, false));
    fullBoardPits.add(new Pit(5, 7, false));
    fullBoardPits.add(new Pit(6, 12, true));
    fullBoardPits.add(new Pit(7, 5, false));
    fullBoardPits.add(new Pit(8, 2, false));
    fullBoardPits.add(new Pit(9, 0, false));
    fullBoardPits.add(new Pit(10, 0, false));
    fullBoardPits.add(new Pit(11, 5, false));
    fullBoardPits.add(new Pit(12, 6, false));
    fullBoardPits.add(new Pit(13, 16, true));
    return fullBoardPits;
  }

}
