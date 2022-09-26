package com.kai.unogame.ui.game;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kai.unogame.listener.DeckCardsListener;
import com.kai.unogame.listener.ExitGameListener;
import com.kai.unogame.listener.GameExitListener;
import com.kai.unogame.listener.TopCardListener;
import com.kai.unogame.listener.TurnListener;
import com.kai.unogame.listener.UpdateExitStatusListener;
import com.kai.unogame.listener.UpdateTopCardListener;
import com.kai.unogame.listener.UserCardsListener;
import com.kai.unogame.model.Card;
import com.kai.unogame.utils.FirebaseHelper;

import java.util.ArrayList;

public class GameViewModel extends AndroidViewModel implements TurnListener, DeckCardsListener, UserCardsListener, TopCardListener, GameExitListener {

    MutableLiveData<String> turnLiveData;
    MutableLiveData<ArrayList<Card>> deckLiveData;
    MutableLiveData<ArrayList<Card>> userCardLiveData;
    MutableLiveData<Card> topCardLiveData;
    MutableLiveData<Boolean> gameExitLiveData;
    MutableLiveData<Boolean> exitStatusLiveData;

    public GameViewModel(@NonNull Application application) {
        super(application);
        turnLiveData = new MutableLiveData<>();
        deckLiveData = new MutableLiveData<>();
        userCardLiveData = new MutableLiveData<>();
        topCardLiveData = new MutableLiveData<>();
        gameExitLiveData = new MutableLiveData<>();
        exitStatusLiveData = new MutableLiveData<>();
    }

    public void initTurnStatus() {
        FirebaseHelper.getTurnStatus(this);
    }

    public MutableLiveData<String> getTurnLiveData() {
        return turnLiveData;
    }

    public void getDeckCards() {
        FirebaseHelper.getDeckCards(this);
    }

    public void getUserCards() {
        FirebaseHelper.getUserCards(this);
    }

    public void initExitStatusListener(){
//        FirebaseHelper.exitGameListener(this);
    }

    public void getTopCard() {
        FirebaseHelper.getTopCard(this);
    }

    @Override
    public void onTurnSuccess(String uid) {
        turnLiveData.postValue(uid);
    }

    @Override
    public void onTurnFailure(String message) {
        Log.e("Error", message);
    }

    @Override
    public void deckCardsSuccess(ArrayList<Card> list) {
        deckLiveData.postValue(list);
    }

    @Override
    public void deckFailure(String message) {
        Log.e("Error", message);
    }

    @Override
    public void userCardsSuccess(ArrayList<Card> list) {
        userCardLiveData.postValue(list);
    }

    @Override
    public void userFailure(String message) {
        Log.e("Error", message);
    }

    public MutableLiveData<ArrayList<Card>> getDeckLiveData() {
        return deckLiveData;
    }

    public MutableLiveData<ArrayList<Card>> getUserCardLiveData() {
        return userCardLiveData;
    }

    @Override
    public void onTopCardSuccess(Card card) {
        topCardLiveData.postValue(card);
    }

    @Override
    public void onTopFailure(String message) {
        Log.e("Error", message);
    }

    public MutableLiveData<Card> getTopCardLiveData() {
        return topCardLiveData;
    }

    public void exitGame() {
//        FirebaseHelper.leaveGame( this );
    }

    @Override
    public void onGameExitSuccess() {
        gameExitLiveData.postValue(true);

    }

    @Override
    public void onGameExitFailure(String message) {
        gameExitLiveData.postValue(false);
    }

    public MutableLiveData<Boolean> getGameExitLiveData() {
        return gameExitLiveData;
    }

    public MutableLiveData<Boolean> getExitStatusLiveData() {
        return exitStatusLiveData;
    }

//    @Override
//    public void onExitSuccess() {
//        exitStatusLiveData.postValue(true);
//    }
//
//    @Override
//    public void onExitStatusChanged() {
//        exitStatusLiveData.postValue(true);
//    }
//
//    @Override
//    public void onExitFailure(String message) {
//        Log.d("FirebaseHelper", "onExitFailure: ");
//    }
}
