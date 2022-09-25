package com.kai.unogame.listener;

import com.kai.unogame.model.Game;

public interface GameListClickedListener {
    void gameListClickedSuccessful(Game game);
    void gameListClickedFailure(String message);
}
