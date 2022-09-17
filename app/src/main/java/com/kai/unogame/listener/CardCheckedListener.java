package com.kai.unogame.listener;

import com.kai.unogame.model.Card;

public interface CardCheckedListener {
    void cardNumSuccessful(Card newTopCard);
    void cardSkipSuccessful(Card newTopCard);
    void cardDraw4Successful(Card newTopCard);
    void cardCheckedFailure(String message);
}
