package com.kai.unogame.listener;

public interface UpdateExitStatusListener {
    void onExitStatusChanged();
    void onExitFailure(String message);
}
