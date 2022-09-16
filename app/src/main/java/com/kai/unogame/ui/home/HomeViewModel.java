package com.kai.unogame.ui.home;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kai.unogame.listener.CreateGameListener;
import com.kai.unogame.listener.JoinGameListener;
import com.kai.unogame.utils.FirebaseHelper;

public class HomeViewModel extends AndroidViewModel implements CreateGameListener, JoinGameListener {
    MutableLiveData<Boolean> createStatusLiveData;
    MutableLiveData<Boolean> joinStatusLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        createStatusLiveData = new MutableLiveData<>();
        joinStatusLiveData = new MutableLiveData<>();
    }

    public void createGame(){
        FirebaseHelper.createGame(this);
    }

    public void joinGame(){
        FirebaseHelper.joinGame( this );
    }

    @Override
    public void gameCreatedSuccessfully() {
        createStatusLiveData.postValue(true);
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
//        joinStatusLiveData.postValue(false);
    }

    public MutableLiveData<Boolean> getCreateStatusLiveData() {
        return createStatusLiveData;
    }

    public MutableLiveData<Boolean> getJoinStatusLiveData() {
        return joinStatusLiveData;
    }
}
