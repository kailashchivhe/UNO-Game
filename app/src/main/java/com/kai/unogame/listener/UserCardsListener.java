package com.kai.unogame.listener;

import com.kai.unogame.model.Card;

import java.util.ArrayList;

public interface UserCardsListener {
    void userCardsSuccess(ArrayList<Card> list);
    void userFailure(String message);
}
