package com.kai.unogame.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kai.unogame.listener.CreateGameListener;
import com.kai.unogame.listener.CreateStatusListener;
import com.kai.unogame.listener.JoinGameListener;
import com.kai.unogame.utils.FirebaseHelper;

public class HomeViewModel extends AndroidViewModel implements CreateGameListener, JoinGameListener, CreateStatusListener {
    MutableLiveData<Boolean> createGameLiveData;
    MutableLiveData<Boolean> joinStatusLiveData;
    MutableLiveData<Boolean> createGameStatusLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        createGameLiveData = new MutableLiveData<>();
        joinStatusLiveData = new MutableLiveData<>();
        createGameStatusLiveData = new MutableLiveData<>();
    }

    public void createGame(){
        FirebaseHelper.createGame(this);
    }

    public void joinGame(){
        FirebaseHelper.joinGame( this );
    }

    public void initCreateStatus(){
        FirebaseHelper.getCreatedStatus( this );
    }

    @Override
    public void gameCreatedSuccessfully() {
        createGameLiveData.postValue(true);
    }

    @Override
    public void gameCreationFailure(String message) {
//        createStatusLiveData.postValue(false);
    }

    @Override
    public void gamedJoined() {
        joinStatusLiveData.postValue(true);
    }

    @Override
    public void gamedJoinedFailure(String message) {
        joinStatusLiveData.postValue(false);
    }

    public MutableLiveData<Boolean> getCreateGameLiveData() {
        return createGameLiveData;
    }

    public MutableLiveData<Boolean> getJoinStatusLiveData() {
        return joinStatusLiveData;
    }

    public MutableLiveData<Boolean> getCreateGameStatusLiveData() {
        return createGameStatusLiveData;
    }

    @Override
    public void createStatusSuccessfully() {
        createGameStatusLiveData.postValue(true);
    }

    @Override
    public void createStatusFailure(String message) {
        createGameStatusLiveData.postValue(false);
    }
}
