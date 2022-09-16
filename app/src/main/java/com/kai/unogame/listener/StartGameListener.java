package com.kai.unogame.listener;

public interface StartGameListener {
    void gameStarted();
    void gameStartedFailure(String message);
}
