package com.application.mancala.service.exceptions;

public class PitNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public PitNotFoundException(int pitId) {
    super("Pit with id " + pitId + " is not found.");
  }

  public PitNotFoundException() {
    super();
  }
}
