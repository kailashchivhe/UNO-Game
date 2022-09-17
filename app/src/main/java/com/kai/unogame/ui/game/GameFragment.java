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

public class GameFragment extends Fragment implements CardClickedListener, CardCheckedListener, UserCardsListener, DeckCardsListener {

    FragmentGameBinding binding;
    PlayerCardsAdapter playerCardsAdapter;
    List<Card> userCardList = new ArrayList<>();
    List<Card> deckCardList = new ArrayList<>();
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
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        gameViewModel.initTurnStatus();

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseHelper.getUserCards(this);
        FirebaseHelper.getDeckCards(this);
        binding.drawCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDrawCardClicked();
            }
        });
    }

    void onDrawCardClicked(){
        userCardList.add(deckCardList.remove(0));
        playerCardsAdapter.notifyDataSetChanged();
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
    public void cardNumSuccesfull(Card newTopCard) {
        //set newTopCard
        userCardList.remove(newTopCard);
        playerCardsAdapter.notifyDataSetChanged();
        //change turn
    }

    @Override
    public void cardSkipSuccesfull(Card newTopCard) {
        //set newTopCard
        userCardList.remove(newTopCard);
        playerCardsAdapter.notifyDataSetChanged();
    }

    @Override
    public void cardDraw4Succesfull(Card newTopCard) {
        //set newTopCard
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

    @Override
    public void userCardsSuccess(ArrayList<Integer> list) {
        userCardList = getCardDetailsList(list);
        playerCardsAdapter = new PlayerCardsAdapter(userCardList,this);
        binding.recyclerViewMyCards.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerViewMyCards.setAdapter(playerCardsAdapter);
    }

    @Override
    public void userFailure(String message) {
        showAlert(message);
    }

    @Override
    public void deckCardsSuccess(ArrayList<Integer> list) {
        deckCardList = getCardDetailsList(list);
        Card card = deckCardList.remove(0);
    }

    @Override
    public void deckFailure(String message) {
        showAlert(message);
    }
}