package com.kai.unogame.listener;

import com.kai.unogame.model.Card;

import java.util.ArrayList;

public interface DeckCardsListener {
    void deckCardsSuccess(ArrayList<Card> list);
    void deckFailure(String message);
}
