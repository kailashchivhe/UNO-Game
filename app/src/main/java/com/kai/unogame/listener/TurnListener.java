package com.kai.unogame.listener;

public interface TurnListener {
    void onTurnSuccess(String uid);
    void onTurnFailure(String message);
}
