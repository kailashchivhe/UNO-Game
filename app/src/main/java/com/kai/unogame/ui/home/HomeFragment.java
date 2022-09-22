package com.kai.unogame.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kai.unogame.R;
import com.kai.unogame.databinding.FragmentHomeBinding;
import com.kai.unogame.databinding.GameLineItemBinding;
import com.kai.unogame.model.Game;
import com.kai.unogame.utils.FirebaseHelper;

import java.util.ArrayList;

public class HomeFragment extends Fragment{

    FragmentHomeBinding binding;
    AlertDialog.Builder builder;
    HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
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

        if (id == R.id.action_profile) {
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
        Boolean flag = false;
        if(getArguments()!=null){
            flag = getArguments().getBoolean("flag");
        }

        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        binding.gameRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeViewModel.createGame();
            }
        });

        /*
        binding.gameJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeViewModel.joinGame();
            }
        });
        */

        homeViewModel.getCreateGameLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    binding.gameRequestButton.setEnabled(false);
                }
            }
        });
        homeViewModel.initStartStatus();
        Boolean finalFlag = flag;
        homeViewModel.getStartStatusLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean && !finalFlag){
                    navigateToGame();
                }
            }
        });


        homeViewModel.getJoinStatusLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

            }
        });

        homeViewModel.initCreateStatus();
        /*
        homeViewModel.getCreateGameStatusLiveData().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    binding.gameJoinButton.setEnabled(true);
                }
            }
        });
         */
    }

    private void navigateToGame(){
        NavHostFragment.findNavController(this ).navigate(R.id.action_HomeFragment_to_GameFragment);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void onLogoutClicked(){
        FirebaseHelper.logout();
        NavHostFragment.findNavController(this).navigate(R.id.action_HomeFragment_to_LoginFragment);
    }

    private void onProfileClicked(){
        NavHostFragment.findNavController(this).navigate(R.id.action_HomeFragment_to_ProfileFragment);
    }

    class GamesListAdapter extends RecyclerView.Adapter<GamesListAdapter.GamesViewHolder> {
        ArrayList<Game> mGames;

        public GamesListAdapter(ArrayList<Game> data) {
            this.mGames = data;
        }

        @NonNull
        @Override
        public GamesListAdapter.GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GameLineItemBinding binding = GameLineItemBinding.inflate(getLayoutInflater(), parent, false);
            return new GamesListAdapter.GamesViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull GamesListAdapter.GamesViewHolder holder, int position) {
            Game game = mGames.get(position);
            holder.setupUI(game);
        }

        @Override
        public int getItemCount() {
            return this.mGames.size();
        }

        public class GamesViewHolder extends RecyclerView.ViewHolder {
            GameLineItemBinding mBinding;
            Game mGame;
            int position;

            public GamesViewHolder(@NonNull GameLineItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Game game) {
                mGame = game;
                mBinding.textViewGame.setText(mGame.getGameID());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String gameID = game.getGameID();
                        String uid = mAuth.getCurrentUser().getUid();
                        homeViewModel.joinGame(uid, gameID);
                    }
                });
            }
        }
    }
}
