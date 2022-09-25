package com.kai.unogame.listener;

import com.kai.unogame.model.Game;

import java.util.ArrayList;

public interface GameListListener {
    void onGameListSuccess(ArrayList<Game> gameArrayList);
    void onGameListFailure(String message);
}
