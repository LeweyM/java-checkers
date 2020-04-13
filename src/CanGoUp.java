import java.util.ArrayList;
import java.util.List;

public class CanGoUp implements PieceAbility {
    private final int[] board;
    private final int location;
    private final Player player;

    public CanGoUp(int[] board, int location, Player player) {
        this.board = board;
        this.location = location;
        this.player = player;
    }

    @Override
    public ArrayList<Move> getMoves() {
        ArrayList<Move> moves = new ArrayList<Move>() {};
        Move leftUp = leftUpMove(location);
        if (leftUp != null) {
            moves.add(leftUp);
        }

        Move rightUp = rightUpMove(location);
        if (rightUp != null) {
            moves.add(rightUp);
        }
        return moves;
    }

    @Override
    public List<Move> getJumps() {
        List<Move> moves = new ArrayList<>();
        if (canJumpUp(location, upLeft(upLeft(location)))) {
            moves.add(new Move(location, upLeft(upLeft(location))));
        }

        if (canJumpUp(location, upRight(upRight(location)))) {
            moves.add(new Move(location, upRight(upRight(location))));
        }
        return moves;
    }

    private boolean canJumpUp(int origin, int destination) {
        return row(origin) - 2 == row(destination)
                && getSquare(destination) == Checkers.EMPTY
                && isOpponentPiece(inbetweenIndex(origin, destination));
    }

    private Move leftUpMove(int i) {
        int leftIndex = upLeft(i);
        if (row(i) - 1 == row(leftIndex) && getSquare(leftIndex) == Checkers.EMPTY) {
            return new Move(i, leftIndex);
        }
        return null;
    }

    private Move rightUpMove(int i) {
        int rightIndex = upRight(i);
        if (row(i) - 1 == row(rightIndex) && getSquare(rightIndex) == Checkers.EMPTY) {
            return new Move(i, rightIndex);
        }
        return null;
    }

    private int upLeft(int i) {
        return isEven(row(i)) ? i - 4 : i - 5;
    }

    private int upRight(int i) {
        return isEven(row(i)) ? i - 3 : i - 4;
    }

    private int inbetweenIndex(int i1, int i2) {
        return isEven(row(i1)) ? (i1 + i2 + 1) / 2 : (i1 + i2 - 1) / 2;
    }

    private int pawnValue(Player player) {
        return player == Player.ONE ? Checkers.PLAYER_ONE_PAWN : Checkers.PLAYER_TWO_PAWN;
    }

    private int getSquare(int i) {
        if (i < 1 || i > 32) {
            return Checkers.OUT_OF_BOUNDS;
        } else {
            return board[i];
        }
    }

    private boolean isOpponentPiece(int origin) {
        return getSquare(origin) == pawnValue(player.opponent()); //todo - kings
    }

    private boolean isEven(int row) {
        return row % 2 == 0;
    }

    private int row(int i) {
        return Math.floorDiv(i-1, 4);
    }
}
