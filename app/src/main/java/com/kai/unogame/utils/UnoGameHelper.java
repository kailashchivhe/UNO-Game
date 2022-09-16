package com.kai.unogame.utils;

import android.graphics.Color;

import com.kai.unogame.model.Card;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
    static Card redSkip = new Card(11,Color.RED,"special","Skip");

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
    static Card greenSkip = new Card(22,Color.GREEN,"special","Skip");

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
    static Card yellowSkip = new Card(33,Color.YELLOW,"special","Skip");

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
    static Card blueSkip = new Card(44,Color.BLUE,"special","Skip");

    static Card draw1 = new Card(45,Color.WHITE,"special","Draw 4");
    static Card draw2 = new Card(45,Color.WHITE,"special","Draw 4");
    static Card draw3 = new Card(46,Color.WHITE,"special","Draw 4");
    static Card draw4 = new Card(47,Color.WHITE,"special","Draw 4");


    public static HashMap<Integer, Card> getAllCards(){
        HashMap<Integer,Card> cards= new HashMap<Integer, Card>()
        {{
            put(1,red0);put(12,green0);put(23,yellow0);put(34,blue0);
            put(2,red1);put(13,green1);put(24,yellow1);put(35,blue1);
            put(3,red2);put(14,green2);put(25,yellow2);put(36,blue2);
            put(4,red3);put(15,green3);put(26,yellow3);put(37,blue3);
            put(5,red4);put(16,green4);put(27,yellow4);put(38,blue4);
            put(6,red5);put(17,green5);put(28,yellow5);put(39,blue5);
            put(7,red6);put(18,green6);put(29,yellow6);put(40,blue6);
            put(8,red7);put(19,green7);put(30,yellow7);put(41,blue7);
            put(9,red8);put(20,green8);put(31,yellow8);put(42,blue8);
            put(10,red9);put(21,green9);put(32,yellow9);put(43,blue9);
            put(11,redSkip);put(22,greenSkip);put(33,yellowSkip);put(44,blueSkip);
            put(45,draw1);put(46,draw2);put(47,draw3);put(48,draw4);
        }};
        return cards;
    }
}
