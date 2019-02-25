import com.battleship.UserData;

public class GameLogic {
    private int[][] _field1;
    private int[][] _field2;
    //field integer meaning: 0 = water; 1 = living ship location; -1 = dead ship location; 2 = island
    private UserData _player1;
    private UserData _player2;


    public GameLogic(UserData player1, UserData player2, int[][] defaultField1, int[][] defaultField2)
    {
        _player1 = player1;
        _player2 = player2;
        _field1 = defaultField1.clone();
        _field2 = defaultField2.clone();
    }


    public GameLogic(UserData player1, UserData player2)
    {
        _player1 = player1;
        _player2 = player2;
        for(int i[]: _field1)
        {
            for(int j: i)
            {
                j=0;
            }
        }
    }

    public boolean shot(int x, int y, int playerIndex)
    {
        switch (playerIndex)
        {
            case 1: if (_field1[x][y] == 1)
            {
                _field1[x][y] = -1;
                return true;
            }
            else
            {
                return false;
            }
            case 2: if (_field2[x][y] == 1)
            {
                _field2[x][y] = -1;
                return true;
            }
            else
            {
                return false;
            }
            default: return false;
        }
    }


    public int[][] getSpielfeld(int playerIndex)
    {
        switch (playerIndex)
        {
            case 1: return field1;
            case 2: return field2;
            default: return null;
        }
    }

}
