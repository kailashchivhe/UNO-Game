package com.kai.unogame.ui.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kai.unogame.R;
import com.kai.unogame.adapter.PlayerCardsAdapter;
import com.kai.unogame.databinding.FragmentGameBinding;
import com.kai.unogame.databinding.ItemCardBinding;
import com.kai.unogame.listener.CardClickedListener;
import com.kai.unogame.model.Card;
import com.kai.unogame.utils.FirebaseHelper;

import java.util.ArrayList;


public class GameFragment extends Fragment implements CardClickedListener{
    FragmentGameBinding binding;
    PlayerCardsAdapter playerCardsAdapter;
    ArrayList<Card> userCardList = new ArrayList<>();
    Card topCard;
    GameViewModel gameViewModel;
    String turnId;
    AlertDialog alertDialog;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGameBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_game,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            gameViewModel.exitGame();
            return true;
        }
        return false;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        playerCardsAdapter = new PlayerCardsAdapter(userCardList,this);
        binding.recyclerViewMyCards.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerViewMyCards.setAdapter(playerCardsAdapter);

        gameViewModel.initTurnStatus();
        gameViewModel.getDeckCards();
        gameViewModel.getUserCards();
        gameViewModel.getTopCard();
        gameViewModel.initExitStatusListener();

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

        gameViewModel.getUserCardLiveData().observe(getViewLifecycleOwner(), new Observer<ArrayList<Card>>() {
            @Override
            public void onChanged(ArrayList<Card> cards) {
                userCardList.clear();
                userCardList.addAll(cards);
                playerCardsAdapter.notifyDataSetChanged();
            }
        });

        gameViewModel.getExitStatusLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                showAlert(message);
            }
        });
    }

    private void initTopCard() {
        ItemCardBinding itemCardBinding = binding.deckCard;
        itemCardBinding.textViewCardName.setText(topCard.getValue());
        itemCardBinding.cardView.setCardBackgroundColor(getCardColor(topCard.getColor()));
    }

    private int getCardColor(String color) {
        if(color.contains("red")){
            return Color.RED;
        }
        else if( color.contains("green")){
            return Color.GREEN;
        }
        else if( color.contains("blue")){
            return Color.BLUE;
        }
        else if( color.contains("yellow")){
            return Color.YELLOW;
        }
        return Color.BLACK;
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
        binding.passTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.drawCard.setEnabled(true);
                binding.passTurn.setEnabled(false);
            }
        });
    }

    void onDrawCardClicked(){
        if(FirebaseHelper.getUser().getUid().contains(turnId)) {
            binding.passTurn.setEnabled(true);
            binding.drawCard.setEnabled(false);
            gameViewModel.drawCard();
        }
        else{
            showAlert("Not your turn");
        }
    }

    private void showAlert(String message) {
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(message.contains("Congratulations") || message.contains("Other player won") ){
                    navigateToHome();
                }
            }
        });
        alertDialog = builder.show();
    }

    public void navigateToHome(){
        Bundle bundle = new Bundle();
        bundle.putSerializable( "flag", true );
        NavHostFragment.findNavController( this ).navigate(R.id.action_GameFragment_to_HomeFragment,bundle);
    }

    @Override
    public void onDestroy() {
        gameViewModel.exitGame();
        super.onDestroy();
    }

    @Override
    public void onCardClicked(Card card) {
        gameViewModel.playCard(card);
        binding.drawCard.setEnabled(true);
    }
}