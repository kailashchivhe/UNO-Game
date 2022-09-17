package com.kai.unogame.listener;

import java.util.ArrayList;

public interface UserCardsListener {
    void userCardsSuccess(ArrayList<Long> list);
    void userFailure(String message);
}
