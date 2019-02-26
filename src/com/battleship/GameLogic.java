package com.battleship;
import java.util.*;

public class GameLogic {

    private int[][] _field1;
    private int[][] _field2;
    //field integer meaning: 0 = water; 1 = living ship location; -1 = dead ship location; 2 = island
    private String _player1;
    private String _player2;
    private String _currentTurn; // 1 = _player1; 2 = _player2
    private static final int NUMBER_OF_SHIPS = 6;


    //Constructors
    /*public GameLogic(String player1, String player2, int[][] defaultField1, int[][] defaultField2)
    {
        _player1 = player1;
        _player2 = player2;
        _field1 = defaultField1.clone();
        _field2 = defaultField2.clone();
        _currentTurn = (int) Math.ceil(Math.random());
    }*/

    public GameLogic(String player1, String player2)
    {
        _player1 = player1;
        _player2 = player2;
        _currentTurn = (int) Math.ceil(Math.random());
        _field1 = new int[15][15];
        _field2 = new int[15][15];
        //fill both fields with zeros
        for(int i = 0; i<15; i++)
        {
            for(int j = 0; j<15; j++)
            {
                _field1[i][j] = 0;
                _field2[i][j] = 0;
            }
        }

    }
    //testmethode
    /*public static void main(String[] args) {
        GameLogic testLogic = new GameLogic("Testspieler1", "Testspieler2");
        for (int k : testLogic.getField("TestSpieler1")) {
            System.out.print(k);
        }
    }*/

    //public methods
    public boolean shot(int x, int y, String playerName)
    {
        if(playerName.equals(_player1)) {
            if (_field2[x][y] == 1) //player1
            {
                _field2[x][y] = -1;
                return true;
            } else {
                _currentTurn = _player2;
                return false;
            }
        }
        else if(playerName.equals(_player2))
        {
            if (_field1[x][y] == 1) //player2
            {
                _field1[x][y] = -1;
                return true;
            } else {
                _currentTurn = _player1;
                return false;
            }
        }
        else
        {
            throw new IllegalArgumentException("Invalid player index.");
        }
    }


    public void setBattleships(int[] ships, String playerName)
    {
        if(ships.length/2 != NUMBER_OF_SHIPS || !(playerName.equals(_player1) || playerName.equals(_player2)))
        {
            throw new IllegalArgumentException("Wrong amount of Ships or invalid player index.");
        }
        else
        {
            if(playerName.equals(_player1)) {
                for (int i = 0; i < ships.length - 2; i += 2) {
                    if (ships[i] == ships[i + 2]) //the x coordinate of two following coordinates is the same => the ship is oriented vertically
                    {
                        for (int l = 0; l < ships[i + 1] - ships[i + 3] + 2; l++) {
                            if (_field1[i][i + 1 + l] == 0) {
                                _field1[i][i + 1 + l] = 1;
                            } else {
                                throw new IllegalArgumentException("At least one of your ships are located at a place where ships don't belong.");
                            }
                        }
                    } else if (ships[i + 1] == ships[i + 3]) // the y coordinate of two following coordinates is the same => the ship is faced horizontally
                    {
                        for (int l = 0; l < ships[i] - ships[i + 2] + 2; l++) {
                            if (_field1[i + 1 + l][i] == 0) {
                                _field1[i + 1 + l][i] = 1;
                            } else {
                                throw new IllegalArgumentException("At least one of your ships are located at a place where ships don't belong.");
                            }
                        }
                    } else {
                        throw new IllegalArgumentException("At least one ship is just 1x1 in size.");
                    }
                }
            }
            else
            {
                for(int i = 0; i<ships.length-2; i+=2)
                {
                    if(ships[i] == ships[i+2]) //the x coordinate of two following coordinates is the same => the ship is oriented vertically
                    {
                        for(int l = 0; l < ships[i+1]-ships[i+3]+2; l++)
                        {
                            if(_field2[i][i+1+l] == 0)
                            {
                                _field2[i][i+1+l] = 1;
                            }
                            else
                            {
                                throw new IllegalArgumentException("At least one of your ships are located at a place where ships don't belong.");
                            }
                        }
                    }
                    else if (ships[i+1] == ships[i+3]) // the y coordinate of two following coordinates is the same => the ship is faced horizontally
                    {
                        for(int l = 0; l < ships[i]-ships[i+2]+2; l++)
                        {
                            if(_field2[i+1+l][i] == 0)
                            {
                                _field2[i+1+l][i] = 1;
                            }
                            else
                            {
                                throw new IllegalArgumentException("At least one of your ships are located at a place where ships don't belong.");
                            }
                        }
                    }
                    else
                    {
                        throw new IllegalArgumentException("At least one ship is just 1x1 in size.");
                    }
                }
            }
        }
    }
    public boolean didILose(String playerName)
    {
        if(playerName.equals(_player1)) {
            for (int[] i : _field1) {
                for (int j : i) {
                    if (j == 1) {
                        return false;
                    }
                }
            }
            return true;
        }
        else if(playerName.equals(_player2))
        {
            for (int[] i : _field2) {
                for (int j : i) {
                    if (j == 1) {
                        return false;
                    }
                }
            }
            return true;
        }

        throw new IllegalArgumentException("Invalid player name.");

    }

    //Hilfsmethoden:

    public int[] getField(String playerName)
    {
        if(playerName.equals(_player1))
        {
            int[] streamField = new int[225];
            int count = 0;
            for(int r = 0; r<15; r++)
            {
                for(int c = 0; c<15; c++)
                {
                    streamField[count] = _field1[r][c];
                }
            }
            return streamField;
        }
        else if(playerName.equals(_player2))
        {
            int[] streamField = new int[225];
            int count = 0;
            for (int r = 0; r < 15; r++) {
                for (int c = 0; c < 15; c++)
                {
                    streamField[count] = _field2[r][c];
                }
            }
            return streamField;
        }
        throw new IllegalArgumentException("Invalid player name.");
    }


    public String getCurrentTurn()
    {
        return _currentTurn;
    }


    public String getOtherPlayer(String playerName)
    {
        if(playerName.equals(_player1))
        {
            return _player2;
        }
        else if (playerName.equals(_player2))
        {
            return _player1;
        }
        throw new IllegalArgumentException("Invalid player name.");
    }
}
