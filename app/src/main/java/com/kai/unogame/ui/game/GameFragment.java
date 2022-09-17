package com.kai.unogame.ui.game;

import static com.kai.unogame.utils.UnoGameHelper.getCardDetailsList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kai.unogame.R;
import com.kai.unogame.adapter.PlayerCardsAdapter;
import com.kai.unogame.databinding.FragmentGameBinding;
import com.kai.unogame.databinding.ItemCardBinding;
import com.kai.unogame.listener.CardCheckedListener;
import com.kai.unogame.listener.CardClickedListener;
import com.kai.unogame.listener.DeckCardsListener;
import com.kai.unogame.listener.UserCardsListener;
import com.kai.unogame.model.Card;
import com.kai.unogame.model.Game;
import com.kai.unogame.utils.FirebaseHelper;
import com.kai.unogame.utils.UnoGameHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameFragment extends Fragment implements CardClickedListener, CardCheckedListener {

    FragmentGameBinding binding;
    PlayerCardsAdapter playerCardsAdapter;
    ArrayList<Card> userCardList = new ArrayList<>();
    ArrayList<Card> deckCardList = new ArrayList<>();
    Card topCard;
    GameViewModel gameViewModel;
    String turnId;

    public GameFragment() {
        // Required empty public constructor
    }

    public static GameFragment newInstance(String param1, String param2) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        gameViewModel.initTurnStatus();
        playerCardsAdapter = new PlayerCardsAdapter(userCardList,this);
        binding.recyclerViewMyCards.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerViewMyCards.setAdapter(playerCardsAdapter);
        gameViewModel.getDeckCards();
        gameViewModel.getUserCards();
        gameViewModel.getTopCard();

        gameViewModel.getTopCardLiveData().observe(getViewLifecycleOwner(), new Observer<Card>() {
            @Override
            public void onChanged(Card card) {
                topCard = card;
                initTopCard();
            }
        });

        gameViewModel.getTurnLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String uid) {
                turnId = uid;
                if(FirebaseHelper.getUser().getUid().contains(uid)){
                    binding.currentPlayerText.setText("Your Turn");
                    binding.currentPlayerText.setTextColor(Color.GREEN);
                }
                else{
                    binding.currentPlayerText.setText("Opposition Turn");
                    binding.currentPlayerText.setTextColor(Color.RED);
                }
            }
        });


        gameViewModel.getDeckLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(ArrayList<Card> cards) {
                deckCardList.clear();
                deckCardList.addAll(cards);
            }
        });

        gameViewModel.getUserCardLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(ArrayList<Card> cards) {
                userCardList.clear();
                userCardList.addAll(cards);
                playerCardsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initTopCard() {
        ItemCardBinding itemCardBinding = binding.deckCard;
        itemCardBinding.textViewCardName.setText(topCard.getValue());
        itemCardBinding.cardView.setCardBackgroundColor(topCard.getColor());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding.drawCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDrawCardClicked();
            }
        });
    }

    void onDrawCardClicked(){
        userCardList.add(deckCardList.remove(0));
        gameViewModel.updateUserCards(userCardList);
        gameViewModel.updateDeck(deckCardList);
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    @Override
    public void cardClickedSuccessfully(Card card) {
        if(turnId.equals(FirebaseHelper.getUser().getUid())){
            UnoGameHelper.checkCard(topCard,card,this);
        }
        else{
            showAlert("Not your turn");
        }
    }

    @Override
    public void cardClickedFailure(String message) {
        showAlert(message);
    }

    @Override
    public void cardNumSuccessful(Card newTopCard) {
        gameViewModel.updateTopCard(newTopCard);
        userCardList.remove(newTopCard);
        if(userCardList.isEmpty()){
            showAlert("Winner");
        }
        else {
            gameViewModel.updateUserCards(userCardList);
            gameViewModel.updateTurn();
        }
    }

    @Override
    public void cardSkipSuccessful(Card newTopCard) {
        //set newTopCard
        gameViewModel.updateTopCard(newTopCard);
        userCardList.remove(newTopCard);
        gameViewModel.updateUserCards(userCardList);
    }

    @Override
    public void cardDraw4Successful(Card newTopCard) {
        //set newTopCard
        topCard = newTopCard;
        initTopCard();
        //ask user to select colour
        //add 4 cards to other user
        userCardList.remove(newTopCard);
        playerCardsAdapter.notifyDataSetChanged();
        //change turn
    }

    @Override
    public void cardCheckedFailure(String message) {
        showAlert(message);
    }
}