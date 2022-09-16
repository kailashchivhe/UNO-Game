package com.kai.unogame.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kai.unogame.R;
import com.kai.unogame.listener.CardClickedListener;
import com.kai.unogame.model.Card;

import java.util.List;

public class PlayerCardsAdapter extends RecyclerView.Adapter<PlayerCardHolder> {
    List<Card> cardList;
    CardClickedListener cardClickedListener;

    public PlayerCardsAdapter(List<Card> cardList, CardClickedListener cardClickedListener) {
        this.cardList = cardList;
        this.cardClickedListener = cardClickedListener;
    }

    @NonNull
    @Override
    public PlayerCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card,parent,false);
        PlayerCardHolder holder = new PlayerCardHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerCardHolder holder, int position) {
        Card card = cardList.get(position);
        holder.name.setText(card.getValue());
        holder.name.setTextColor(card.getColor());
//        holder.view.setBackgroundColor(card.getColor());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCardClicked(card);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    void onCardClicked(Card card){
        cardClickedListener.cardClickedSuccessfully(card);
    }
}
class PlayerCardHolder extends RecyclerView.ViewHolder{

    TextView name;
    View view;
    public PlayerCardHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.textViewCardName);
        view  = itemView;
    }
}
