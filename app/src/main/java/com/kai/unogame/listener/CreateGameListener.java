package com.kai.unogame.listener;

public interface CreateGameListener {
    void gameCreatedSuccessfully();
    void gameCreationFailure(String message);
}
