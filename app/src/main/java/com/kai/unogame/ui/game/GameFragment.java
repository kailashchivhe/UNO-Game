package com.kai.unogame.ui.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
    List<Card> cardList = new ArrayList<>();
    Card topCard;
    GameViewModel gameViewModel;
    Game game;
    AlertDialog.Builder builder;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        HashMap<Integer, Card> hashSet =  UnoGameHelper.getAllCards();
        cardList.add(hashSet.get(1));
        cardList.add(hashSet.get(11));
        cardList.add(hashSet.get(45));
        cardList.add(hashSet.get(33));
        cardList.add(hashSet.get(23));
        cardList.add(hashSet.get(37));


        playerCardsAdapter = new PlayerCardsAdapter(cardList,this);
        binding.recyclerViewMyCards.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerViewMyCards.setAdapter(playerCardsAdapter);
    }

    @Override
    public void cardClickedSuccessfully(Card card) {
        if(game.getTurnID().equals(FirebaseHelper.getUser().getUid())){
            UnoGameHelper.checkCard(topCard,card,this);
        }
        else{
            builder.setMessage("Not your turn");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    @Override
    public void cardClickedFailure(String message) {
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void cardCheckedSuccesfull() {

    }

    @Override
    public void cardCheckedFailure(String message) {
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}