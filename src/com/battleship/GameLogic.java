package com.battleship;


public class GameLogic {

    private int[][] _field1;
    private int[][] _field2;
    //field integer meaning: 0 = water; 1 = living ship location; -1 = dead ship location; 2 = island
    private UserData _player1;
    private UserData _player2;
    private static final int NUMBER_OF_SHIPS = 6;

    //Constructors
    public GameLogic(UserData player1, UserData player2, int[][] defaultField1, int[][] defaultField2) {
        _player1 = player1;
        _player2 = player2;
        _field1 = defaultField1.clone();
        _field2 = defaultField2.clone();
    }

    public GameLogic(UserData player1, UserData player2) {
        _player1 = player1;
        _player2 = player2;
        for (int i[] : _field1) {
            for (int j : i) {
                j = 0;
            }
        }
    }

    //public methods
    public boolean shot(int x, int y, int playerIndex) {
        switch (playerIndex) {
            case 1:
                if (_field1[x][y] == 1) //player1
                {
                    _field1[x][y] = -1;
                    return true;
                } else {
                    return false;
                }
            case 2:
                if (_field2[x][y] == 1) //player2
                {
                    _field2[x][y] = -1;
                    return true;
                } else {
                    return false;
                }
            default: //exception
        }
        return false;
    }


    public void setBattleships(int[] ships, int playerIndex) {

        if (ships.length / 2 != NUMBER_OF_SHIPS || !(playerIndex == 1 || playerIndex == 2)) {
            //execption
        } else {
            switch (playerIndex) {
                case 1:
                    for (int i = 0; i < ships.length - 2; i += 2) {
                        if (ships[i] == ships[i + 2]) //the x coordinate of two following coordinates is the same => the ship is oriented vertically
                        {
                            for (int l = 0; l < ships[i + 1] - ships[i + 3] + 2; l++) {
                                _field1[i][i + 1 + l] = 1;
                            }
                        } else if (ships[i + 1] == ships[i + 3]) // the y coordinate of two following coordinates is the same => the ship is faced horizontally
                        {
                            for (int l = 0; l < ships[i] - ships[i + 2] + 2; l++) {
                                _field1[i][i + 1 + l] = 1;
                            }
                        } else {
                            //execption
                        }
                    }
                case 2:
                    for (int i = 0; i < ships.length - 2; i += 2) {
                        if (ships[i] == ships[i + 2]) //the x coordinate of two following coordinates is the same => the ship is oriented vertically
                        {
                            for (int l = 0; l < ships[i + 1] - ships[i + 3] + 2; l++) {
                                _field2[i][i + 1 + l] = 1;
                            }
                        } else if (ships[i + 1] == ships[i + 3]) // the y coordinate of two following coordinates is the same => the ship is faced horizontally
                        {
                            for (int l = 0; l < ships[i] - ships[i + 2] + 2; l++) {
                                _field2[i][i + 1 + l] = 1;
                            }
                        } else {
                            //execption
                        }
                    }
            }
        }
    }

    //Hilfsmethoden:

    public int[][] getField(int playerIndex) {
        switch (playerIndex) {
            case 1:
                return _field1;
            case 2:
                return _field2;
            default:
                return null;
        }
    }

}
