package com.kai.unogame.listener;

import com.kai.unogame.model.Card;

public interface TopCardListener {
    void onTopCardSuccess(Card card);
    void onTopFailure(String message);
}
