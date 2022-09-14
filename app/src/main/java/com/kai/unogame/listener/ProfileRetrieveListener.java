package com.kai.unogame.listener;

import com.kai.unogame.model.User;

public interface ProfileRetrieveListener {
    void profileRetrieved(User user);
    void profileRetrievedFailure(String message);
}
