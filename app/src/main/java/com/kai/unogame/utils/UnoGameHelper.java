package com.kai.unogame.utils;

import android.graphics.Color;

import com.kai.unogame.listener.CardCheckedListener;
import com.kai.unogame.listener.CardClickedListener;
import com.kai.unogame.model.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class UnoGameHelper {

    static Card red0 = new Card(1,Color.RED,"num","0");
    static Card red1 = new Card(2,Color.RED,"num","1");
    static Card red2 = new Card(3,Color.RED,"num","2");
    static Card red3 = new Card(4,Color.RED,"num","3");
    static Card red4 = new Card(5,Color.RED,"num","4");
    static Card red5 = new Card(6,Color.RED,"num","5");
    static Card red6 = new Card(7,Color.RED,"num","6");
    static Card red7 = new Card(8,Color.RED,"num","7");
    static Card red8 = new Card(9,Color.RED,"num","8");
    static Card red9 = new Card(10,Color.RED,"num","9");
    static Card redSkip = new Card(11,Color.RED,"skip","Skip");

    static Card green0 = new Card(12,Color.GREEN,"num","0");
    static Card green1 = new Card(13,Color.GREEN,"num","1");
    static Card green2 = new Card(14,Color.GREEN,"num","2");
    static Card green3 = new Card(15,Color.GREEN,"num","3");
    static Card green4 = new Card(16,Color.GREEN,"num","4");
    static Card green5 = new Card(17,Color.GREEN,"num","5");
    static Card green6 = new Card(18,Color.GREEN,"num","6");
    static Card green7 = new Card(19,Color.GREEN,"num","7");
    static Card green8 = new Card(20,Color.GREEN,"num","8");
    static Card green9 = new Card(21,Color.GREEN,"num","9");
    static Card greenSkip = new Card(22,Color.GREEN,"skip","Skip");

    static Card yellow0 = new Card(23,Color.YELLOW,"num","0");
    static Card yellow1 = new Card(24,Color.YELLOW,"num","1");
    static Card yellow2 = new Card(25,Color.YELLOW,"num","2");
    static Card yellow3 = new Card(26,Color.YELLOW,"num","3");
    static Card yellow4 = new Card(27,Color.YELLOW,"num","4");
    static Card yellow5 = new Card(28,Color.YELLOW,"num","5");
    static Card yellow6 = new Card(29,Color.YELLOW,"num","6");
    static Card yellow7 = new Card(30,Color.YELLOW,"num","7");
    static Card yellow8 = new Card(31,Color.YELLOW,"num","8");
    static Card yellow9 = new Card(32,Color.YELLOW,"num","9");
    static Card yellowSkip = new Card(33,Color.YELLOW,"skip","Skip");

    static Card blue0 = new Card(34,Color.BLUE,"num","0");
    static Card blue1 = new Card(35,Color.BLUE,"num","1");
    static Card blue2 = new Card(36,Color.BLUE,"num","2");
    static Card blue3 = new Card(37,Color.BLUE,"num","3");
    static Card blue4 = new Card(38,Color.BLUE,"num","4");
    static Card blue5 = new Card(39,Color.BLUE,"num","5");
    static Card blue6 = new Card(40,Color.BLUE,"num","6");
    static Card blue7 = new Card(41,Color.BLUE,"num","7");
    static Card blue8 = new Card(42,Color.BLUE,"num","8");
    static Card blue9 = new Card(43,Color.BLUE,"num","9");
    static Card blueSkip = new Card(44,Color.BLUE,"skip","Skip");

    static Card draw1 = new Card(45,Color.BLACK,"draw4","Draw 4");
    static Card draw2 = new Card(45,Color.BLACK,"draw4","Draw 4");
    static Card draw3 = new Card(46,Color.BLACK,"draw4","Draw 4");
    static Card draw4 = new Card(47,Color.BLACK,"draw4","Draw 4");


    public static HashMap<Long, Card> getAllCards(){
        HashMap<Long,Card> cards= new HashMap<Long, Card>()
        {{
            put(1L,red0);put(12L,green0);put(23L,yellow0);put(34L,blue0);
            put(2L,red1);put(13L,green1);put(24L,yellow1);put(35L,blue1);
            put(3L,red2);put(14L,green2);put(25L,yellow2);put(36L,blue2);
            put(4L,red3);put(15L,green3);put(26L,yellow3);put(37L,blue3);
            put(5L,red4);put(16L,green4);put(27L,yellow4);put(38L,blue4);
            put(6L,red5);put(17L,green5);put(28L,yellow5);put(39L,blue5);
            put(7L,red6);put(18L,green6);put(29L,yellow6);put(40L,blue6);
            put(8L,red7);put(19L,green7);put(30L,yellow7);put(41L,blue7);
            put(9L,red8);put(20L,green8);put(31L,yellow8);put(42L,blue8);
            put(10L,red9);put(21L,green9);put(32L,yellow9);put(43L,blue9);
            put(11L,redSkip);put(22L,greenSkip);put(33L,yellowSkip);put(44L,blueSkip);
            put(45L,draw1);put(46L,draw2);put(47L,draw3);put(48L,draw4);
        }};
        return cards;
    }

    public static void checkCard(Card topDeck, Card playedCard, CardCheckedListener cardCheckedListener){
        if(playedCard.getType().equals("skip")){
            if(topDeck.getColor() == playedCard.getColor()){
                //same colour skip
                cardCheckedListener.cardSkipSuccesfull(playedCard);
            }
            else{
                //Card can not be played
                cardCheckedListener.cardCheckedFailure("You can not play this card");
            }
        }
        else if(playedCard.getType().equals("num")){
            if(playedCard.getColor() == topDeck.getColor()){
                //same colour
                cardCheckedListener.cardNumSuccesfull(playedCard);
            }
            else{
                //Card can not be played
                cardCheckedListener.cardCheckedFailure("You can not play this card");
            }
        }
        else{
            //draw 4
            cardCheckedListener.cardDraw4Succesfull(playedCard);
        }
    }


    public static HashSet<Long> getUsersCards(){
        Random random = new Random();
        int cnt = 0;
        HashSet<Long> set = new HashSet<>();
        while(cnt < 14){
            long number = ThreadLocalRandom.current().nextLong(49);
//            int number = random.nextLong(49f );
            if(number != 0 && !set.contains(number)){
                set.add(number);
                cnt++;
            }
        }
        return  set;
    }

    public static ArrayList<Long> getDeck(HashSet<Long> userSet){
//        getAllCards()
        ArrayList<Long> deck = new ArrayList<>();
        for( Map.Entry<Long,Card> mapElement :getAllCards().entrySet()){
            if(!userSet.contains(mapElement.getKey())){
                deck.add(mapElement.getKey());
            }
        }
        return deck;
    }

    public static ArrayList<Card> getCardDetailsList(ArrayList<Long> cardIntegerList){
        ArrayList<Card> cardArrayList = new ArrayList<>();
        for(Long num : cardIntegerList){
            cardArrayList.add(getAllCards().get(num));
        }
        return cardArrayList;
    }
}
