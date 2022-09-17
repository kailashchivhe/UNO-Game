package com.kai.unogame.listener;

import java.util.ArrayList;

public interface UserCardsListener {
    void userCardsSuccess(ArrayList<Integer> list);
    void userFailure(String message);
}
