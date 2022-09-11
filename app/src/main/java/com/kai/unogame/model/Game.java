package com.kai.unogame.model;

public class Game {
    String gameID;
    String player1ID;
    String player2ID;
    String status;

    public Game(String gameID, String player1ID, String status) {
        this.gameID = gameID;
        this.player1ID = player1ID; 
        this.status = status;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getPlayer1ID() {
        return player1ID;
    }

    public void setPlayer1ID(String player1ID) {
        this.player1ID = player1ID;
    }

    public String getPlayer2ID() {
        return player2ID;
    }

    public void setPlayer2ID(String player2ID) {
        this.player2ID = player2ID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
