package com.kai.unogame.listener;

import java.util.ArrayList;

public interface DeckCardsListener {
    void deckCardsSuccess(ArrayList<Integer> list);
    void deckFailure(String message);
}
