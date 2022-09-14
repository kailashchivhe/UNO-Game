package com.kai.unogame.utils;

import com.kai.unogame.model.Card;

import java.util.HashSet;

public class UnoGameHelper {

    static Card red0 = new Card(1,"num","0");
    static Card red1 = new Card(1,"num","1");
    static Card red2 = new Card(1,"num","2");
    static Card red3 = new Card(1,"num","3");
    static Card red4 = new Card(1,"num","4");
    static Card red5 = new Card(1,"num","5");
    static Card red6 = new Card(1,"num","6");
    static Card red7 = new Card(1,"num","7");
    static Card red8 = new Card(1,"num","8");
    static Card red9 = new Card(1,"num","9");
    static Card redSkip = new Card(1,"special","Skip");

    static Card green0 = new Card(2,"num","0");
    static Card green1 = new Card(2,"num","1");
    static Card green2 = new Card(2,"num","2");
    static Card green3 = new Card(2,"num","3");
    static Card green4 = new Card(2,"num","4");
    static Card green5 = new Card(2,"num","5");
    static Card green6 = new Card(2,"num","6");
    static Card green7 = new Card(2,"num","7");
    static Card green8 = new Card(2,"num","8");
    static Card green9 = new Card(2,"num","9");
    static Card greenSkip = new Card(2,"special","Skip");

    static Card yellow0 = new Card(3,"num","0");
    static Card yellow1 = new Card(3,"num","1");
    static Card yellow2 = new Card(3,"num","2");
    static Card yellow3 = new Card(3,"num","3");
    static Card yellow4 = new Card(3,"num","4");
    static Card yellow5 = new Card(3,"num","5");
    static Card yellow6 = new Card(3,"num","6");
    static Card yellow7 = new Card(3,"num","7");
    static Card yellow8 = new Card(3,"num","8");
    static Card yellow9 = new Card(3,"num","9");
    static Card yellowSkip = new Card(3,"special","Skip");

    static Card blue0 = new Card(4,"num","0");
    static Card blue1 = new Card(4,"num","1");
    static Card blue2 = new Card(4,"num","2");
    static Card blue3 = new Card(4,"num","3");
    static Card blue4 = new Card(4,"num","4");
    static Card blue5 = new Card(4,"num","5");
    static Card blue6 = new Card(4,"num","6");
    static Card blue7 = new Card(4,"num","7");
    static Card blue8 = new Card(4,"num","8");
    static Card blue9 = new Card(4,"num","9");
    static Card blueSkip = new Card(4,"special","Skip");

    static Card draw1 = new Card(5,"special","Draw 4");
    static Card draw2 = new Card(5,"special","Draw 4");
    static Card draw3 = new Card(5,"special","Draw 4");
    static Card draw4 = new Card(5,"special","Draw 4");


    static HashSet<Card> getAllCards(){
        HashSet<Card> cards = new HashSet<>();
        cards.add(red0);cards.add(green0);cards.add(yellow0);cards.add(blue0);
        cards.add(red1);cards.add(green1);cards.add(yellow1);cards.add(blue1);
        cards.add(red2);cards.add(green2);cards.add(yellow2);cards.add(blue2);
        cards.add(red3);cards.add(green3);cards.add(yellow3);cards.add(blue3);
        cards.add(red4);cards.add(green4);cards.add(yellow4);cards.add(blue4);
        cards.add(red5);cards.add(green5);cards.add(yellow5);cards.add(blue5);
        cards.add(red6);cards.add(green6);cards.add(yellow6);cards.add(blue6);
        cards.add(red7);cards.add(green7);cards.add(yellow7);cards.add(blue7);
        cards.add(red8);cards.add(green8);cards.add(yellow8);cards.add(blue8);
        cards.add(red9);cards.add(green9);cards.add(yellow9);cards.add(blue9);
        cards.add(redSkip);cards.add(greenSkip);cards.add(yellowSkip);cards.add(blueSkip);
        cards.add(draw1);cards.add(draw2);cards.add(draw3);cards.add(draw4);

        return cards;
    }
}
