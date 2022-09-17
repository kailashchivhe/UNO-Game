package com.kai.unogame.listener;

public interface GameExitListener {
    void onGameExitSuccess();
    void onGameExitFailure(String message);
}
