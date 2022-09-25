package com.kai.unogame.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kai.unogame.R;
import com.kai.unogame.listener.GameListClickedListener;
import com.kai.unogame.model.Game;

import java.util.ArrayList;

public class GameListAdapter extends RecyclerView.Adapter<GamesViewHolder> {
    ArrayList<Game> mGames;
    GameListClickedListener gameListClickedListener;

    public GameListAdapter(ArrayList<Game> mGames, GameListClickedListener gameListClickedListener) {
        this.mGames = mGames;
        this.gameListClickedListener = gameListClickedListener;
    }

    @NonNull
    @Override
    public GamesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_request, parent, false);
        GamesViewHolder holder = new GamesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GamesViewHolder holder, int position) {
        Game game = mGames.get(position);
        holder.textViewGameNumber.setText(""+position);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGameClicked(game);
            }
        });
    }

    private void onGameClicked(Game game) {
        gameListClickedListener.gameListClickedSuccessful(game);
    }

    @Override
    public int getItemCount() {
        return this.mGames.size();
    }
}
class GamesViewHolder extends RecyclerView.ViewHolder {
    View view;
    TextView textViewGameNumber;

    public GamesViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
        textViewGameNumber = itemView.findViewById(R.id.textViewGameNumber);
    }
}
