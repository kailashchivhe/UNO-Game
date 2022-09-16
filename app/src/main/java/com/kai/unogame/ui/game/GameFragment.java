package com.kai.unogame.ui.game;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kai.unogame.R;
import com.kai.unogame.adapter.PlayerCardsAdapter;
import com.kai.unogame.databinding.FragmentGameBinding;
import com.kai.unogame.model.Card;
import com.kai.unogame.utils.FirebaseHelper;
import com.kai.unogame.utils.UnoGameHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GameFragment extends Fragment {

    FragmentGameBinding binding;
    PlayerCardsAdapter playerCardsAdapter;
    List<Card> cardList = new ArrayList<>();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        HashMap<Integer, Card> hashSet =  UnoGameHelper.getAllCards();
        cardList.add(hashSet.get(1));
        cardList.add(hashSet.get(11));
        cardList.add(hashSet.get(45));
        cardList.add(hashSet.get(33));
        cardList.add(hashSet.get(23));
        cardList.add(hashSet.get(37));


        playerCardsAdapter = new PlayerCardsAdapter(cardList);
        binding.recyclerViewMyCards.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerViewMyCards.setAdapter(playerCardsAdapter);
    }
}