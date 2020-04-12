import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// board is index 1-32

public class Checkers {
    private static final int EMPTY = 0;
    private static final int OUT_OF_BOUNDS = 3;
    private static final int PLAYER_ONE_PAWN = 1;
    private static final int PLAYER_TWO_PAWN = -1;
    private int[] board;

    public Checkers(int[] b) {
        board = b;
    }

    public Checkers() {
    }

    public void setup() {
        board = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    }

    public String toString() {
        return Arrays.toString(board);
    }

    public List<Move> getLegalMoves(int i) {
        ArrayList<Move> moves = new ArrayList<Move>() {
        };

        if (this.getSquare(i) > 0) {
            Move leftDown = leftDownMove(i);
            if (leftDown != null) {
                moves.add(leftDown);
            }

            Move rightDown = rightDownMove(i);
            if (rightDown != null) {
                moves.add(rightDown);
            }
        }

        if (this.getSquare(i) < 0) {
            Move leftUp = leftUpMove(i);
            if (leftUp != null) {
                moves.add(leftUp);
            }

            Move rightUp = rightUpMove(i);
            if (rightUp != null) {
                moves.add(rightUp);
            }
        }

        return moves;
    }

    public List<Move> getAllLegalMoves(int player) {
        List<Integer> playerPawnSquares = new ArrayList<>();

        for (int i = 0; i < board.length; i++) {
            if (getSquare(i) == pawnValue(player)) {
                playerPawnSquares.add(i);
            }
        }

        return playerPawnSquares.stream()
                .map(this::getLegalMoves)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private int pawnValue(int player) {
        return player == 1 ? PLAYER_ONE_PAWN : PLAYER_TWO_PAWN;
    }

    private Move leftUpMove(int i) {
        int leftIndex = isEven(row(i)) ? i - 4 : i - 5;

        if (row(i) - 1 == row(leftIndex) && getSquare(leftIndex) == EMPTY) {
            return new Move(i, leftIndex);
        }
        return null;
    }

    private Move rightUpMove(int i) {
        int rightIndex = isEven(row(i)) ? i - 5 : i - 4;

        if (row(i) - 1 == row(rightIndex) && getSquare(rightIndex) == EMPTY) {
            return new Move(i, rightIndex);
        }
        return null;
    }

    private Move leftDownMove(int i) {
        int leftIndex = isEven(row(i)) ? i + 4 : i + 3;

        if (row(i) + 1 == row(leftIndex) && getSquare(leftIndex) == EMPTY) {
            return new Move(i, leftIndex);
        }
        return null;
    }

    private Move rightDownMove(int i) {
        int rightIndex = isEven(row(i)) ? i + 5 : i + 4;

        if (row(i) + 1 == row(rightIndex) && getSquare(rightIndex) == EMPTY) {
            return new Move(i, rightIndex);
        }
        return null;
    }

    private boolean isEven(int row) {
        return row % 2 == 0;
    }

    private int getSquare(int i) {
        int boardIndex = i - 1;
        if (boardIndex < 1 || boardIndex > board.length) {
            return OUT_OF_BOUNDS;
        } else {
            return board[boardIndex];
        }
    }

    ;

    private int row(int i) {
        return Math.floorDiv(i - 1, 4);
    }
}
