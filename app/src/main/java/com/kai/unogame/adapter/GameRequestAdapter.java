package com.kai.unogame.adapter;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.kai.unogame.R;
import com.kai.unogame.listener.JoinGameListener;
import com.kai.unogame.model.Game;
import com.kai.unogame.utils.FirebaseHelper;

import java.util.List;

public class GameRequestAdapter extends RecyclerView.Adapter<GameRequestHolder> implements JoinGameListener {
    List<Game> gameList;
    public GameRequestAdapter(List<Game> gameList) {
        this.gameList = gameList;
    }

    @NonNull
    @Override
    public GameRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_request,parent,false);
        GameRequestHolder holder = new GameRequestHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GameRequestHolder holder, int position) {
        Game game = gameList.get(position);
        holder.textViewGameRequestUserID.setText(game.getPlayer1ID());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGameRequestClicked(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }
    void onGameRequestClicked(Game game){
        //Go to game
//        FirebaseHelper.joinGame(game, FirebaseHelper.getUser().getUid());
    }

    @Override
    public void gamedJoined() {
        //Goto game fragment
    }

    @Override
    public void gamedJoinedFailure(String message) {
        Log.d("demo", "gamedJoinedFailure: " + message);
    }
}
class GameRequestHolder extends RecyclerView.ViewHolder{

    TextView textViewGameRequestUserID;
    View view;
    public GameRequestHolder(@NonNull View itemView) {
        super(itemView);
        textViewGameRequestUserID = itemView.findViewById(R.id.textViewGameRequestUserID);
        view = itemView;
    }
}
