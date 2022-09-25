package com.kai.unogame.model;

public class Game {
    String gameID;
    String player1ID;
    String player2ID;
    Boolean status;
    String turnID;

    public String getTurnID() {
        return turnID;
    }

    public void setTurnID(String turnID) {
        this.turnID = turnID;
    }

    public Game(String gameID, String player1ID, Boolean status) {
        this.gameID = gameID;
        this.player1ID = player1ID; 
        this.status = status;
    }

    public Game(String gameID, String player1ID, Boolean status, String turnID) {
        this.gameID = gameID;
        this.player1ID = player1ID;
        this.status = status;
        this.turnID = turnID;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
