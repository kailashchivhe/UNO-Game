package com.kai.unogame.ui.game;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.kai.unogame.listener.CardClickedListener;
import com.kai.unogame.listener.DeckCardsListener;
import com.kai.unogame.listener.TurnListener;
import com.kai.unogame.listener.UserCardsListener;
import com.kai.unogame.model.Card;
import com.kai.unogame.utils.FirebaseHelper;
import com.kai.unogame.utils.UnoGameHelper;

import java.util.ArrayList;

public class GameViewModel extends AndroidViewModel implements TurnListener, DeckCardsListener, UserCardsListener {

    MutableLiveData<String> turnLiveData;
    MutableLiveData<ArrayList<Card>> deckLiveData;
    MutableLiveData<ArrayList<Card>> userCardLiveData;

    public GameViewModel(@NonNull Application application) {
        super(application);
        turnLiveData = new MutableLiveData<>();
        deckLiveData = new MutableLiveData<>();
        userCardLiveData = new MutableLiveData<>();
    }

    public void initTurnStatus(){
        FirebaseHelper.getTurnStatus(this);
    }

    public MutableLiveData<String> getTurnLiveData() {
        return turnLiveData;
    }

    public void getDeckCards(){
        FirebaseHelper.getDeckCards( this );
    }

    public void getUserCards(){
        FirebaseHelper.getUserCards( this );
    }

    @Override
    public void onTurnSuccess(String uid) {
        turnLiveData.postValue(uid);
    }

    @Override
    public void onTurnFailure(String message) {
        Log.e("Error", message );
    }

    @Override
    public void deckCardsSuccess(ArrayList<Integer> list) {
        ArrayList<Card> cardArrayList = UnoGameHelper.getCardDetailsList( list );
        deckLiveData.postValue(cardArrayList);
    }

    @Override
    public void deckFailure(String message) {
        Log.e("Error", message );
    }

    @Override
    public void userCardsSuccess(ArrayList<Integer> list) {
        ArrayList<Card> cardArrayList = UnoGameHelper.getCardDetailsList( list );
        userCardLiveData.postValue(cardArrayList);
    }

    @Override
    public void userFailure(String message) {
        Log.e("Error", message );
    }

    public MutableLiveData<ArrayList<Card>> getDeckLiveData() {
        return deckLiveData;
    }

    public MutableLiveData<ArrayList<Card>> getUserCardLiveData() {
        return userCardLiveData;
    }
}
