import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Checkers {
    private int[] board;

    public Checkers(int[] board) {
        this.board = board;
    }

    public Checkers() { }

    public void setup() {
        this.board = new int[]{1,1,1,1, 1,1,1,1, 1,1,1,1, 0,0,0,0, 0,0,0,0, -1,-1,-1,-1, -1,-1,-1,-1, -1,-1,-1,-1};
    }

    public String toString() {
        return Arrays.toString(this.board);
    }

    public List<Move> getLegalMoves(int i) {
        ArrayList<Move> moves = new ArrayList<Move>() {};
        if (this.board[i + 4] == 0) {
            moves.add(new Move(i, i + 4));
        }
        return moves;
    }
}
