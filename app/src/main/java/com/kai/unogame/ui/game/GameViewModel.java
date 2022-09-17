package com.kai.unogame.ui.game;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kai.unogame.listener.CardClickedListener;
import com.kai.unogame.listener.TurnListener;
import com.kai.unogame.model.Card;
import com.kai.unogame.utils.FirebaseHelper;

public class GameViewModel extends AndroidViewModel implements TurnListener {

    MutableLiveData<String> turnLiveData;
    public GameViewModel(@NonNull Application application) {
        super(application);
        turnLiveData = new MutableLiveData<>();
    }

    public void initTurnStatus(){
        FirebaseHelper.getTurnStatus(this);
    }

    public MutableLiveData<String> getTurnLiveData() {
        return turnLiveData;
    }

    @Override
    public void onTurnSuccess(String uid) {
        turnLiveData.postValue(uid);
    }

    @Override
    public void onTurnFailure(String message) {
        Log.e("Error", message );
    }
}
