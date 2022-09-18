package com.kai.unogame.listener;

public interface ExitGameListener {
    void onExitSuccess();
    void onExitFailure(String message);
}
