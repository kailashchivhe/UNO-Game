package com.kai.unogame.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kai.unogame.R;
import com.kai.unogame.adapter.GameRequestAdapter;
import com.kai.unogame.databinding.FragmentHomeBinding;
import com.kai.unogame.listener.StartGameListener;
import com.kai.unogame.model.Game;
import com.kai.unogame.utils.FirebaseHelper;

import java.util.List;

public class HomeFragment extends Fragment implements StartGameListener {

    FragmentHomeBinding binding;
    List<Game> gameList;
    AlertDialog.Builder builder;
    GameRequestAdapter gameRequestAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            onProfileClicked();
            return true;
        }
        else if (id == R.id.action_new_game) {
            startGame();
            return true;
        }
        else if (id == R.id.action_logout) {
            onLogoutClicked();
            return true;
        }

        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
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
        gameRequestAdapter = new GameRequestAdapter(gameList);
        binding.recyclerViewGameRequest.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.recyclerViewGameRequest.setAdapter(gameRequestAdapter);
    }

    private void onLogoutClicked(){
        FirebaseHelper.logout();
        NavHostFragment.findNavController(this).navigate(R.id.action_HomeFragment_to_LoginFragment);
    }

    private void onProfileClicked(){
        NavHostFragment.findNavController(this).navigate(R.id.action_HomeFragment_to_ProfileFragment);
    }

    private void startGame(){
        FirebaseHelper.startGame(FirebaseHelper.getUser().getUid());
    }

    @Override
    public void gameStarted(String gameID) {
        FirebaseHelper.displayGames(gameID);
    }

    @Override
    public void gameStartedFailure(String message) {
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}