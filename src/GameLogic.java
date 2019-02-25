import com.battleship.UserData;

public class GameLogic {
    private int[][] _field1;
    private int[][] _field2;
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

    /*public void shot(int x, int y, b)
    {

    }*/


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
