import java.util.ArrayList;
import java.util.List;

public class CanGoDown implements PieceAbility {
    @Override
    public ArrayList<Move> getMoves(int location) {
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
    public List<Move> getJumps(int location) {
        List<Move> moves = new ArrayList<>();
        if (canJumpDown(location, downLeft(downLeft(location)))) {
            moves.add(new Move(location, downLeft(downLeft(location)), Move.JUMP));
        }

        if (canJumpDown(location, downRight(downRight(location)))) {
            moves.add(new Move(location, downRight(downRight(location)), Move.JUMP));
        }
        return moves;
    }

    private boolean canJumpDown(int origin, int destination) {
        return row(origin) + 2 == row(destination);
    }

    private Move leftDownMove(int i) {
        int leftIndex = downLeft(i);
        if (row(i) + 1 == row(leftIndex)) {
            return new Move(i, leftIndex, Move.NORMAL);
        }
        return null;
    }

    private Move rightDownMove(int i) {
        int rightIndex = downRight(i);
        if (row(i) + 1 == row(rightIndex)) {
            return new Move(i, rightIndex, Move.NORMAL);
        }
        return null;
    }

    private int downLeft(int i) {
        return isEven(row(i)) ? i + 4 : i + 3;
    }

    private int downRight(int i) {
        return isEven(row(i)) ? i + 5 : i + 4;
    }

    private boolean isEven(int row) {
        return row % 2 == 0;
    }

    private int row(int i) {
        return Math.floorDiv(i - 1, 4);
    }
}
