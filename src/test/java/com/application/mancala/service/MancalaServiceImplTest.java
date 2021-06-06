package com.application.mancala.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import com.application.mancala.repository.PitRepository;
import com.application.mancala.repository.PlayerRepository;
import com.application.mancala.service.MancalaService;
import com.application.mancala.service.MancalaServiceImpl;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MancalaServiceImplTest {

  @TestConfiguration
  static class MancalaServiceImplTestConfiguration {

    @Bean
    public MancalaService mancalaService() {
      return new MancalaServiceImpl();
    }

  }

  @Autowired
  private PitRepository pitRepository;

  @Autowired
  private PlayerRepository playerRepository;

  @Autowired
  private MancalaService mancalaService;

  @Test
  public void testIfGameCreatedSuccessfully() {
    mancalaService.createGame();
    assertEquals(2, playerRepository.findAll().size());
    assertEquals("PlayerB", playerRepository.getOne(2).getName());
    assertEquals(false, playerRepository.getOne(2).isCurrentPlayer());
    assertEquals(6, playerRepository.getOne(1).getMainPit().getId().intValue());
    assertEquals(14, pitRepository.findAll().size());
  }

}
