package com.kai.unogame.ui.home;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kai.unogame.listener.CreateGameListener;
import com.kai.unogame.listener.GameListListener;
import com.kai.unogame.listener.JoinGameListener;
import com.kai.unogame.listener.StartGameListener;
import com.kai.unogame.model.Game;
import com.kai.unogame.utils.FirebaseHelper;

import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel implements CreateGameListener, JoinGameListener, StartGameListener, GameListListener {
    public static final String TAG = "HomeViewModel";
    MutableLiveData<Boolean> createGameLiveData;
    MutableLiveData<Boolean> joinGameLiveData;
    MutableLiveData<Boolean> startStatusLiveData;
    MutableLiveData<ArrayList<Game>> gameListLiveData;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        createGameLiveData = new MutableLiveData<>();
        joinGameLiveData = new MutableLiveData<>();
        startStatusLiveData = new MutableLiveData<>();
        gameListLiveData = new MutableLiveData<>();
    }

    public void createGame(){
        FirebaseHelper.createGame(this);
    }

    public void joinGame(Game game){
        FirebaseHelper.joinGame( this, game );
    }

    public void initStartStatus(){
        FirebaseHelper.gameStartedListener(this);
    }

    public void getGameList(){
        FirebaseHelper.getGamesList( this );
    }

    @Override
    public void gameCreatedSuccessfully() {
        createGameLiveData.postValue(true);
    }

    @Override
    public void gameCreationFailure(String message) {
        createGameLiveData.postValue(false);
    }

    @Override
    public void joinGameSuccess() {
        joinGameLiveData.postValue(true);
    }

    @Override
    public void gamedJoinedFailure(String message) {
        joinGameLiveData.postValue(false);
    }

    public MutableLiveData<Boolean> getCreateGameLiveData() {
        return createGameLiveData;
    }

    public MutableLiveData<Boolean> getJoinGameLiveData() {
        return joinGameLiveData;
    }

    @Override
    public void gameStarted() {
        startStatusLiveData.postValue(true);
    }

    @Override
    public void gameStartedFailure(String message) {
        startStatusLiveData.postValue(false);
    }

    public MutableLiveData<Boolean> getStartStatusLiveData() {
        return startStatusLiveData;
    }

    public MutableLiveData<ArrayList<Game>> getGameListLiveData() {
        return gameListLiveData;
    }

    @Override
    public void onGameListSuccess(ArrayList<Game> gameArrayList) {
        gameListLiveData.postValue( gameArrayList );
    }

    @Override
    public void onGameListFailure(String message) {
        Log.d(TAG, "onGameListFailure: ");
    }
}
