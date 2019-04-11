import com.battleship.CurrentTurnResponse
import java.util.*

class GameLogic
//Constructors
/*public GameLogic(String player1, String player2, int[][] defaultField1, int[][] defaultField2)
    {
        _player1 = player1;
        _player2 = player2;
        _field1 = defaultField1.clone();
        _field2 = defaultField2.clone();
        _currentTurn = (int) Math.ceil(Math.random());
    }*/
@Throws(IllegalArgumentException::class)
constructor(//field integer meaning: 0 = water; 1 = living ship location; -1 = dead ship location; 2 = island
    private val _player1: String, private val _player2: String
) {

    private val _field1: Array<IntArray>
    private val _field2: Array<IntArray>
    private var _currentTurn: String? = null // 1 = _player1; 2 = _player2
    private val _lastShotsPlayer1: ArrayList<Int>
    private val _lastShotsPlayer2: ArrayList<Int>


    //}
    //
    //
    //if (_currentTurn.equals(_player1)) {
    //    _lastShotsPlayer1.clear();
    //    return new CurrentTurnResponse("success",_currentTurn, _lastShotsPlayer1);
    //} else if (_currentTurn.equals(_player2)) {
    //    _lastShotsPlayer2.clear();
    //    return new CurrentTurnResponse("success",_currentTurn, _lastShotsPlayer2);
    val currentTurn: CurrentTurnResponse
        get() {

            if (_currentTurn == _player1 || _currentTurn == _player2) {
                val lastShots = mapOf(
                    _player1 to _lastShotsPlayer1,
                    _player2 to _lastShotsPlayer2
                )
                return CurrentTurnResponse("success", _currentTurn!!, lastShots)
            } else {
                throw IllegalArgumentException("Current turn system broken! Server is exploding!")
            }
        }

    init {
        val random01 = Math.round(Math.random())
        if (random01 == 0L) {
            _currentTurn = _player1
        } else {
            _currentTurn = _player2
        }
        _field1 = Array(15) { IntArray(15) }
        _field2 = Array(15) { IntArray(15) }
        //fill both fields with values
        for (i in 0..14) {
            for (j in 0..14) {
                _field1[i][j] = 0
                _field2[i][j] = 0
            }
        }
        _lastShotsPlayer1 = ArrayList()
        _lastShotsPlayer2 = ArrayList()
    }

    //testmethode
    /*public static void main(String[] args) {
        GameLogic testLogic = new GameLogic("Testspieler1", "Testspieler2");
        for (int k : testLogic.getField("Testspieler1")) {
            System.out.print(k + " ");
        }
        System.out.println("");
        for (int k : testLogic.getField("Testspieler2")) {
            System.out.print(k + " ");
        }
    }*/

    //public methods
    @Throws(IllegalArgumentException::class)
    fun shot(x: Int, y: Int, playerName: String): Boolean {
        if (playerName == _player1) {
            _lastShotsPlayer1.add(x)
            _lastShotsPlayer1.add(y)
            if (_field2[x][y] == 1) {
                _field2[x][y] = -1
                return true
            } else {
                _currentTurn = _player2
                return false
            }
        } else if (playerName == _player2) {
            _lastShotsPlayer2.add(x)
            _lastShotsPlayer2.add(y)
            if (_field1[x][y] == 1) {
                _field1[x][y] = -1
                return true
            } else {
                _currentTurn = _player1
                return false
            }
        } else {
            throw IllegalArgumentException("Invalid player index.")
        }
    }


    @Throws(IllegalArgumentException::class)
    fun setBattleships(ships: IntArray, playerName: String) {
        if (ships.size / 4 != NUMBER_OF_SHIPS || !(playerName == _player1 || playerName == _player2)) {
            throw IllegalArgumentException("Wrong amount of Ships or invalid player name.")
        } else {
            if (playerName == _player1) {
                var i = 0
                while (i < ships.size - 2) {
                    if (ships[i] == ships[i + 2])
                    //the x coordinate of two following coordinates is the same => the ship is oriented vertically
                    {
                        for (l in 0 until Math.abs(ships[i + 3] - ships[i + 1]) + 1) {
                            if (_field1[i][i + l] == 0) {
                                _field1[i][i + l] = 1
                            } else {
                                throw IllegalArgumentException("not placed in water")
                            }
                        }
                    } else if (ships[i + 1] == ships[i + 3])
                    // the y coordinate of two following coordinates is the same => the ship is faced horizontally
                    {
                        for (l in 0 until Math.abs(ships[i + 2] - ships[i]) + 1) {
                            if (_field1[i + l][i] == 0) {
                                _field1[i + l][i] = 1
                            } else {
                                throw IllegalArgumentException("At least one of your ships are located at a place where ships don't belong.")
                            }
                        }
                    } else {
                        for (j1 in _field1) {
                            for (j2 in j1) {
                                print(j2)
                            }
                            println()
                        }
                        //throw new IllegalArgumentException("Es hat nicht zu hundert Prozent geklappt.");
                    }
                    i += 2
                }
            } else {
                var i = 0
                while (i < ships.size - 2) {
                    if (ships[i] == ships[i + 2])
                    //the x coordinate of two following coordinates is the same => the ship is oriented vertically
                    {
                        for (l in 0 until Math.abs(ships[i + 3] - ships[i + 1]) + 1) {
                            if (_field2[i][i + 1 + l] == 0) {
                                _field2[i][i + 1 + l] = 1
                            } else {
                                throw IllegalArgumentException("not placed in water")
                            }
                        }
                    } else if (ships[i + 1] == ships[i + 3])
                    // the y coordinate of two following coordinates is the same => the ship is faced horizontally
                    {
                        for (l in 0 until Math.abs(ships[i + 2] - ships[i]) + 1) {
                            if (_field2[i + 1 + l][i] == 0) {
                                _field2[i + 1 + l][i] = 1
                            } else {
                                throw IllegalArgumentException("not placed in water")
                            }
                        }
                    } else {
                        for (j1 in _field2) {
                            for (j2 in j1) {
                                print(j2)
                            }
                            println()
                        }
                        //throw new IllegalArgumentException("At least one ship is just 1x1 in size.");
                    }
                    i += 2
                }
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    fun didILose(playerName: String): Boolean {
        if (playerName == _player1) {
            for (i in _field1) {
                for (j in i) {
                    if (j == 1) {
                        return false
                    }
                }
            }
            return true
        } else if (playerName == _player2) {
            for (i in _field2) {
                for (j in i) {
                    if (j == 1) {
                        return false
                    }
                }
            }
            return true
        }

        throw IllegalArgumentException("Invalid player name.")

    }

    //Hilfsmethoden:

    @Throws(IllegalArgumentException::class)
    fun getField(playerName: String): IntArray {
        if (playerName == _player1) {
            val streamField = IntArray(225)
            var count = 0
            for (r in 0..14) {
                for (c in 0..14) {
                    streamField[count] = _field1[r][c]
                    count++
                }
            }
            return streamField
        } else if (playerName == _player2) {
            val streamField = IntArray(225)
            var count = 0
            for (r in 0..14) {
                for (c in 0..14) {
                    streamField[count] = _field2[r][c]
                    count++
                }
            }
            return streamField
        }
        throw IllegalArgumentException("Invalid player name.")
    }


    @Throws(IllegalArgumentException::class)
    fun getOtherPlayer(playerName: String): String {
        if (playerName == _player1) {
            return _player2
        } else if (playerName == _player2) {
            return _player1
        }
        throw IllegalArgumentException("Invalid player name.")
    }

    companion object {
        private val NUMBER_OF_SHIPS = 3
    }
}
