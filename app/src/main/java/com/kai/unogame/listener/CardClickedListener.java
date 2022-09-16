package com.kai.unogame.listener;

import com.kai.unogame.model.Card;

public interface CardClickedListener {
    void cardClickedSuccessfully(Card card);
    void cardClickedFailure(String message);
}
