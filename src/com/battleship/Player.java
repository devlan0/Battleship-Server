package com.battleship;
import java.util.ArrayList;

public class Player {

    private String _name;
    private int _field[][]; //0 = water; 1 = ship location; -1 = dead ship location
    private ArrayList<Integer> _lastShots;


    public Player(String name) {
        _field = new int[15][15];
        _lastShots = new ArrayList<>();
        _name = name;
    }


    public void addShot(int x, int y){
        _lastShots.add(x);
        _lastShots.add(y);
    }

    //getter and setter methods
    public int getField(int x, int y) {
        return _field[x][y];
    }

    public void setField(int x, int y, int val) {
        _field[x][y] = val;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public ArrayList<Integer> getLastShots() {
        return _lastShots;
    }

    public void clearShots() {
        _lastShots.clear();
    }

    public int[][] getFieldObject() {
        return _field;
    }
}
