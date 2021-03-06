package connect4;
import java.util.HashMap;
import java.util.Random;

public class Connect4 {
    static final int ROWS = 6;
    static final int COLUMNS = 7;
    static final int TOWIN = 4;
    static final String[] SYMBOL = new String[] {".", "R", "Y"};

    public Position current;
    public int turn;
    public int winner;
    private Random rn = new Random();
    private HashMap<Position, Integer> transpositionTable;

    /**
     * Constructs a blank Connect4 Game with R (1) to start.
     */
    public Connect4() {
        current = new Position(COLUMNS, ROWS);
        turn = 1;
        winner = 0;
    }

    /**
     * Plays a disk in the column specified. The disk will be of the player who's turn it is. This has no error check.
     * @param column
     * @throws IllegalMoveException
     */
    public void play(int column) throws IllegalMoveException {
        current.push(column, turn);
        turn = 3 - turn;
        winner = checkWin();
    }

    /**
     * Plays a disk in the column specified, if legal. The disk will be of the player who's turn it is.
     * If an error is encountered, this method will do nothing.
     * @param column
     */
    public boolean safePlay(int column) {
        try {
            current.push(column, turn);
            turn = 3 - turn;
            winner = checkWin();
            return true;
        } catch (IllegalMoveException | ArrayIndexOutOfBoundsException ignore) {
            return false;
        } 
    }

    public void playRandom() throws IllegalMoveException {
        Integer[] legalPlays = current.getLegal();
        play(legalPlays[rn.nextInt(legalPlays.length)]);
    }

    public void playComputer() throws IllegalMoveException {
        transpositionTable = new HashMap<>();
        int maxEval = -1000000;
        int bestPlay = -1;
        for (int columnMove : current.getLegal()) {
            current.push(columnMove, turn);
            int eval = minimax(15, -1000000, 1000000, false, turn);
            if (eval > maxEval) {
                bestPlay = columnMove;
                maxEval = eval;
            }
            current.pop(columnMove);
        }
        play(bestPlay);
    }

    private int minimax(int depth, int alpha, int beta, boolean maximizing, int computerDisk) throws IllegalMoveException {
        // Look up the position in the transposition table
        if (transpositionTable.containsKey(current)) {
            return transpositionTable.get(current);
        }
        //Return if the game has ended (or if the depth is at maximum)
        int eval = checkWin();
        if (depth == 0 || eval != 0) {
            if (eval == 0) {
                return eval;
            } else {
                if (eval == 3) {
                    return 0;
                } else if (eval == computerDisk) {
                    return 100 - depth;
                } else {
                    return -100 - depth;
                }
            }
        }
        //Maximizing: finds the best way forward (for the computer)
        boolean broke = false;
        if (maximizing) {
            int maxEval = -1000000;
            for (int columnMove : current.getLegal()) {
                current.push(columnMove, computerDisk);
                eval = minimax(depth - 1, alpha, beta, false, computerDisk);
                maxEval = Math.max(eval, maxEval);
                alpha = Math.max(alpha, eval);
                current.pop(columnMove);
                if (beta <= alpha) {
                    broke = true;
                    break;
                }
            }
            // Add the eval to the transposition table *if* it was calculated fully
            if (!broke) {
                transpositionTable.put(current, maxEval);
            }
            return maxEval;
        // Minimizing: finds the best way forward for the opposition (the player)
        } else {
            int minEval = 1000000;
            int playerDisk = 3 - computerDisk;
            for (int columnMove : current.getLegal()) {
                current.push(columnMove, playerDisk);
                eval = minimax(depth - 1, alpha, beta, true, computerDisk);
                minEval = Math.min(eval, minEval);
                beta = Math.min(beta, eval);
                current.pop(columnMove);
                if (beta <= alpha) {
                    broke = true;
                    break;
                }
            }
            if (!broke) {
                transpositionTable.put(current, minEval);
            }
            return minEval;
        }
    }

    /**
     * Checks if there are any wins in the current position, for the lines specified by the arguments. 
     * @param startRow  The row to start checking in. Each row will be the start of a potential winning line.
     * @param rowAmount  The amount of rows to check.
     * @param startColumn  The column to start checking in. Each row will be the start of a potential winning line.
     * @param columnAmount  The amount of columns to check.
     * @param rowStep  The amount of rows to add for each space in a potential win position. 
     * For example, 1 would specify that the winning lines to check for are all sloping/pointing upwards.
     * @param columnStep  The amount of columns to add for each space in a potential win position.
     * For example, 1 would specify that the winning lines to check for are all sloping/pointing forward.
     * @param winAmount  The amount of consecutive same disks to check for.
     * @return  The winning player, 0 if none.
     */
    public int checkLines(int startRow, int rowAmount, int startColumn, int columnAmount, int rowStep, int columnStep, int winAmount) {
        int n;
        for (int i = startRow; i < startRow + rowAmount; i++) { // Loops through the specified rows.
            for (int j = startColumn; j < startColumn + columnAmount; j++) { 
            // Loops through the specified columns.
                n = 1;
                for (int k = 0; k < winAmount; k++) { 
                /* 
                Loops through the spaces in the potential win position,
                multiplying n by the value in each position. If n is 0, 
                no one has won. If not, it checks if one of the players has won. 
                */
                    n *= current.get(j + (k * columnStep), i + (k * rowStep));
                }
                if (n == 0) {
                    continue;
                }
                if (n == 1) {
                    return 1;
                } else if (n > Math.pow(2.0, winAmount) - 0.5) { //Checking an inequality to avoid problems with float inaccuracy.
                    return 2;
                }
            }
        }

        return 0;
    }

    /**
     * Checks if there are wins in the current position.
     * @return  The winning player, 0 if none.
     */
    public int checkWin() {
        int winPlayer;
        
        //Horizontal Check
        winPlayer = checkLines(0, ROWS, 0, COLUMNS - TOWIN + 1, 0, 1, TOWIN);
        if (winPlayer != 0) {
            return winPlayer;
        }

        //Vertical Check
        winPlayer = checkLines(0, ROWS - TOWIN + 1, 0, COLUMNS, 1, 0, TOWIN);
        if (winPlayer != 0) {
            return winPlayer;
        }

        //Upward Diagonals Check
        winPlayer = checkLines(0, ROWS - TOWIN + 1, 0, COLUMNS - TOWIN + 1, 1, 1, TOWIN);
        if (winPlayer != 0) {
            return winPlayer;
        }

        //Downward Diagonals Check
        winPlayer = checkLines(TOWIN - 1, ROWS - TOWIN + 1, 0, COLUMNS - TOWIN + 1, -1, 1, TOWIN);
        if (winPlayer != 0) {
            return winPlayer;
        }

        //If no one has won yet...
        if (current.isFull()) {
            return 3;
        }
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(100);
        //Build the position, row by row.
        for (int i = ROWS - 1; i > -1; i--) {
            for (int j = 0; j < COLUMNS; j++) {
                str.append(SYMBOL[current.get(j, i)] + " ");
            }
            str.append('\n');
        }
        //Add the column numbers.
        for (int j = 0; j < COLUMNS; j++) {
            str.append(j);
            str.append(' ');
        }
        //Add a separator.
        str.append('\n');
        for (int j = 0; j < COLUMNS; j++) {
            str.append("--");
        }
        str.append("\nTurn: " + SYMBOL[turn]);
        return str.toString();
    }
}