package com.application.mancala.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MancalaStatus {

  private boolean gameFinished;
  private String winner;
}
