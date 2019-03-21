package com.battleship;
import java.util.*;
public class GameLogic {

    private Player _player1;
    private Player _player2;
    private Player _currentPlayer; //determines whose turn it is at the moment
    private Player _relevantPlayer; //relevant for intern methods
    private static final int NUMBER_OF_SHIPS = 6;


    //Constructors

    public GameLogic(String player1, String player2) throws IllegalArgumentException {
        _player1 = new Player(player1);
        _player2 = new Player(player2);
        long random01 = Math.round(Math.random());
        if (random01==0) {
            _currentPlayer = _player1;
        }
        else {
            _currentPlayer = _player2;
        }
        //fill both fields with zeroes
        for(int i = 0; i<15; i++) {
            for(int j = 0; j<15; j++) {
                _player1.setField(i,j,0);
                _player2.setField(i,j,0);
            }
        }
    }

    /*public GameLogic(String player1, String player2, int[][] defaultField1, int[][] defaultField2)
{
    ...
}*/


    public boolean shot(int x, int y, String playerName) throws IllegalArgumentException {
        updateRelevantPlayer(playerName);
        _relevantPlayer.addShot(x, y);
        if (stringToPlayer(getOtherPlayer(_currentPlayer.getName())).getField(x, y) == 1) {
            _relevantPlayer.setField(x, y, -1);
            return true;
        } else {
            _currentPlayer = stringToPlayer(getOtherPlayer(_currentPlayer.getName()));
            return false;
        }
    }

    public void setBattleships(int[] ships, String playerName) throws IllegalArgumentException {
        if (ships.length / 4 != NUMBER_OF_SHIPS) {
            throw new IllegalArgumentException("Wrong amount of Ships or invalid player name.");
        }
        updateRelevantPlayer(playerName);
        for (int i = 0; i < ships.length - 2; i += 2) {
            if (ships[i] == ships[i + 2]) { //the x coordinate of two following coordinates is the same => the ship is oriented vertically
                for (int l = 0; l < Math.abs(ships[i + 3] - ships[i + 1]) + 1; l++) {
                    if (_relevantPlayer.getField(i, i + l) == 0) {
                        _relevantPlayer.setField(i, i + l, 1);
                    } else {
                        throw new IllegalArgumentException("Illegal ship placement");
                    }
                }
            } else if (ships[i + 1] == ships[i + 3]) {// the y coordinate of two following coordinates is the same => the ship is faced horizontally
                for (int l = 0; l < Math.abs(ships[i + 2] - ships[i]) + 1; l++) {
                    if (_relevantPlayer.getField(i + l, i) == 0) {
                        _relevantPlayer.setField(i + l, i, 1);
                    } else {
                        throw new IllegalArgumentException("Illegal ship placement");
                    }
                }
            }
        }
    }

    public boolean didILose(String playerName) throws IllegalArgumentException   {
        updateRelevantPlayer(playerName);
        for (int[] i : _relevantPlayer.getFieldObject()) {
            for (int j : i) {
                if (j == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getOtherPlayer(String playerName) throws IllegalArgumentException {
        if(playerName.equals(_player1.getName()))
        {
            return _player2.getName();
        }
        else if (playerName.equals(_player2.getName()))
        {
            return _player1.getName();
        }
        throw new IllegalArgumentException("Invalid player name.");
    }

    private Player stringToPlayer(String playerName) throws IllegalArgumentException {
        if(playerName.equals(_player1.getName())) {
            return _player1;
        } else if(playerName.equals(_player2.getName())) {
            return _player2;
        }
        throw new IllegalArgumentException("Invalid player name.");
    }

    private void updateRelevantPlayer(String playerName) throws IllegalArgumentException {
        if(playerName.equals(_player1)) {
            _relevantPlayer = _player1;
        } else if (playerName.equals(_player2.getName())) {
            _relevantPlayer = _player2;
        } else {
            throw new IllegalArgumentException("Invalid player name.");
        }
    }

    /*  public int[] getField(String playerName) throws IllegalArgumentException //as a one-dimensional array
    {
        if(playerName.equals(_player1.getName()))
        {
            _relevantPlayer = _player1;
        }
        else if(playerName.equals(_player2.getName()))
        {
            _relevantPlayer = _player2;
        } else {
            throw new IllegalArgumentException("Invalid player name.");
        }
        int[] streamField = new int[225];
        int count = 0;
        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                streamField[count] = _relevantPlayer.getField(r,c);
                count++;
            }
        }
        return streamField;
    }
    */

  /*  public CurrentTurnResponse getCurrentTurn()
    {
        CurrentTurnResponse response = new CurrentTurnResponse(_currentPlayer.getName(), _currentPlayer.getLastShots().toArray());
        _currentPlayer.clearShots();
        return response;
    } */
}
