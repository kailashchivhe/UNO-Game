package com.kai.unogame.ui.game;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.kai.unogame.listener.CardClickedListener;
import com.kai.unogame.model.Card;

public class GameViewModel extends AndroidViewModel  {

    public GameViewModel(@NonNull Application application) {
        super(application);
    }
}
