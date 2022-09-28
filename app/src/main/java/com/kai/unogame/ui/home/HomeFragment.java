package com.kai.unogame.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.kai.unogame.R;
import com.kai.unogame.adapter.GameListAdapter;
import com.kai.unogame.databinding.FragmentHomeBinding;
import com.kai.unogame.listener.GameListClickedListener;
import com.kai.unogame.model.Game;
import com.kai.unogame.utils.FirebaseHelper;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements GameListClickedListener {
    public static final String TAG = "HomeFragment";
    FragmentHomeBinding binding;
    AlertDialog alertDialog;
    HomeViewModel homeViewModel;
    GameListAdapter gameListAdapter;
    ArrayList<Game> gameArrayList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_create_game){
            onCreateGameClicked();
            return true;
        }
        else if (id == R.id.action_profile) {
            onProfileClicked();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameListAdapter = new GameListAdapter(gameArrayList,this);
        binding.recyclerViewGames.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewGames.setAdapter(gameListAdapter);

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        homeViewModel.getGameList();

        homeViewModel.getCreateGameLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!FirebaseHelper.getGameId().isEmpty()) {
                    if (aBoolean) {
                        homeViewModel.initStartStatus();
                    } else {
                        showAlert("Game not created");
                    }
                }
            }
        });

        homeViewModel.getStartStatusLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!FirebaseHelper.getGameId().isEmpty()) {
                    if (aBoolean) {
                        navigateToGame();
                    } else {
                        showAlert("Game start failed");
                    }
                }
            }
        });

        homeViewModel.getJoinGameLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!FirebaseHelper.getGameId().isEmpty()) {
                    if (aBoolean) {
                        homeViewModel.initStartStatus();
                    } else {
                        showAlert("Game joined Failed");
                    }
                }
            }
        });

        homeViewModel.gameListLiveData.observe(getViewLifecycleOwner(), new Observer<ArrayList<Game>>() {
            @Override
            public void onChanged(ArrayList<Game> games) {
                gameArrayList.clear();
                gameArrayList.addAll(games);
                gameListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void navigateToGame(){
        NavHostFragment.findNavController(this ).navigate(R.id.action_HomeFragment_to_GameFragment);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void onCreateGameClicked() {
        //Create new game
        homeViewModel.createGame();
    }

    private void onLogoutClicked(){
        FirebaseHelper.logout();
        NavHostFragment.findNavController(this).navigate(R.id.action_HomeFragment_to_LoginFragment);
    }

    private void onProfileClicked(){
        NavHostFragment.findNavController(this).navigate(R.id.action_HomeFragment_to_ProfileFragment);
    }

    @Override
    public void gameListClickedSuccessful(Game game) {
        //Join game using game.id
        homeViewModel.joinGame(game);
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

            }
        });
        alertDialog = builder.show();
    }
}
