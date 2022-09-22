package com.kai.unogame.listener;

public interface JoinGameListener {
    void joinGame(String uid, String gameId);
    void gamedJoinedFailure(String message);
}
