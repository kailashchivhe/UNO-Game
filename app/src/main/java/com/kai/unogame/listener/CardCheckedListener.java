package com.kai.unogame.listener;

import com.kai.unogame.model.Card;

public interface CardCheckedListener {
    void cardNumSuccesfull(Card newTopCard);
    void cardSkipSuccesfull(Card newTopCard);
    void cardDraw4Succesfull(Card newTopCard);
    void cardCheckedFailure(String message);
}
