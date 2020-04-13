import java.util.ArrayList;
import java.util.List;

public class CanGoDown implements PieceAbility {
    private final int[] board;
    private final int location;
    private final Player player;

    public CanGoDown(int[] board, int location, Player player) {
        this.board = board;
        this.location = location;
        this.player = player;
    }

    @Override
    public ArrayList<Move> getMoves() {
        ArrayList<Move> moves = new ArrayList<Move>() {
        };
        Move leftDown = leftDownMove(location);
        if (leftDown != null) {
            moves.add(leftDown);
        }

        Move rightDown = rightDownMove(location);
        if (rightDown != null) {
            moves.add(rightDown);
        }
        return moves;
    }

    @Override
    public List<Move> getJumps() {
        List<Move> moves = new ArrayList<>();
        if (canJumpDown(location, downLeft(downLeft(location)))) {
            moves.add(new Move(location, downLeft(downLeft(location))));
        }

        if (canJumpDown(location, downRight(downRight(location)))) {
            moves.add(new Move(location, downRight(downRight(location))));
        }
        return moves;
    }

    private boolean canJumpDown(int origin, int destination) {
        return row(origin) + 2 == row(destination)
                && getSquare(destination) == Checkers.EMPTY
                && isOpponentPiece(inbetweenIndex(origin, destination));
    }

    private Move leftDownMove(int i) {
        int leftIndex = downLeft(i);
        if (row(i) + 1 == row(leftIndex) && getSquare(leftIndex) == Checkers.EMPTY) {
            return new Move(i, leftIndex);
        }
        return null;
    }

    private Move rightDownMove(int i) {
        int rightIndex = downRight(i);
        if (row(i) + 1 == row(rightIndex) && getSquare(rightIndex) == Checkers.EMPTY) {
            return new Move(i, rightIndex);
        }
        return null;
    }

    private int downLeft(int i) {
        return isEven(row(i)) ? i + 4 : i + 3;
    }

    private int downRight(int i) {
        return isEven(row(i)) ? i + 5 : i + 4;
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
        return Math.floorDiv(i - 1, 4);
    }
}
