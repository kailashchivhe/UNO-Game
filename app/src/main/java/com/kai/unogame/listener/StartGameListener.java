package com.kai.unogame.listener;

public interface StartGameListener {
    void gameStarted(String gameID);
    void gameStartedFailure(String message);
}
