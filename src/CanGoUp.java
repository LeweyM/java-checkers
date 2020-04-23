import java.util.ArrayList;
import java.util.List;

public class CanGoUp implements PieceAbility {

    @Override
    public ArrayList<Move> getMoves(int location) {
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
    public List<Move> getJumps(int location) {
        List<Move> moves = new ArrayList<>();
        if (canJumpUp(location, upLeft(upLeft(location)))) {
            moves.add(new Move(location, upLeft(upLeft(location)), Move.JUMP));
        }

        if (canJumpUp(location, upRight(upRight(location)))) {
            moves.add(new Move(location, upRight(upRight(location)), Move.JUMP));
        }
        return moves;
    }

    private boolean canJumpUp(int origin, int destination) {
        return row(origin) - 2 == row(destination);
    }

    private Move leftUpMove(int i) {
        int leftIndex = upLeft(i);
        if (row(i) - 1 == row(leftIndex)) {
            return new Move(i, leftIndex, Move.NORMAL);
        }
        return null;
    }

    private Move rightUpMove(int i) {
        int rightIndex = upRight(i);
        if (row(i) - 1 == row(rightIndex)) {
            return new Move(i, rightIndex, Move.NORMAL);
        }
        return null;
    }

    private int upLeft(int i) {
        return isEven(row(i)) ? i - 4 : i - 5;
    }

    private int upRight(int i) {
        return isEven(row(i)) ? i - 3 : i - 4;
    }

    private boolean isEven(int row) {
        return row % 2 == 0;
    }

    private int row(int i) {
        return Math.floorDiv(i-1, 4);
    }
}
