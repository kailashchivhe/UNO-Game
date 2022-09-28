package com.kai.unogame.listener;

public interface ExitGameListener {
    void onExitSuccess(String message);
    void onExitFailure(String message);
}
