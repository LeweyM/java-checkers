import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// board is index 1-32

public class Checkers {
    public static final int EMPTY = 0;
    public static final int OUT_OF_BOUNDS = 3;

    private int[] board;
    private Player currentPlayer = Player.ONE;
    private Pieces pieces;

    public Checkers(int[] b) {
        board = b;
        pieces = new Pieces(b);
    }

    public Checkers() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < board.length; i++) {
            if (i % 4 == 1) {
                sb.append("|");
            }
            if (isEven(row(i))) {
                sb.append(" ");
                sb.append(pieceString(i));
            } else {
                sb.append(pieceString(i));
                sb.append(" ");
            }
            if (i % 4 == 0) {
                sb.append("| (" + (i-3) + "-" + i + ")\n");
            }
        }
        return sb.toString();
    }

    public void setup() {
        currentPlayer = Player.ONE;
        board = new int[]{OUT_OF_BOUNDS, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        pieces = new Pieces(board);
    }

    public int[] stateSlice() {
        return board.clone();
    }

    public List<Move> getLegalMoves(int i) {
        return pieces.getLegalMoves(currentPlayer).stream()
                .filter(m -> m.origin() == i)
                .collect(Collectors.toList());
    }

    public List<Move> getAllLegalMoves() {
        return this.pieces.getLegalMoves(this.currentPlayer);
    }

    public void move(int origin, int target) {
        Move move = makeMove(origin, target);

        switch (move.type()) {
            case Move.NORMAL:
                changePiecePosition(origin, target);
                break;
            case Move.JUMP:
                take(origin, target);

                List<Move> jumps = getLegalMoves(target).stream()
                        .filter(m -> m.type().equals(Move.JUMP))
                        .collect(Collectors.toList());
                while (jumps.size() == 1) {
                    Move jump = jumps.get(0);
                    take(jump.origin(), jump.target());

                    jumps = getLegalMoves(jump.target()).stream()
                            .filter(m -> m.type().equals(Move.JUMP))
                            .collect(Collectors.toList());
                }

                if (jumps.size() > 1) return;

                pieces.finishChainJumping();
                break;
        }

        nextTurn();

        List<Move> takingMoves = pieces.getLegalMoves(currentPlayer).stream()
                .filter(m -> m.type().equals(Move.JUMP))
                .collect(Collectors.toList());

        if (takingMoves.size() == 1) {
            Move take = takingMoves.get(0);
            move(take.origin(), take.target());
        }
    }

    public int whoseTurn() {
        return currentPlayer.value();
    }


    private String pieceString(int i) {
        switch (board[i]) {
            case 1: return "o";
            case 2: return "O";
            case -1: return "x";
            case -2: return "X";
            case 0: return "_";
            default: return "";
        }
    }

    private Move makeMove(int origin, int target) {
        if (!pieces.isLegalMove(origin, target, currentPlayer)) throw new IllegalArgumentException("Illegal move: [" + origin + "->" + target + "]");
        return new Move(origin, target, Math.abs(origin - target) < 6 ? Move.NORMAL : Move.JUMP);
    }

    private void take(int origin, int target) {
        pieces.take(origin, target);

        setSquare(target, getSquare(origin));
        setSquare(origin, EMPTY);
        this.setSquare(inbetweenIndex(origin, target), EMPTY);
    }

    private int inbetweenIndex(int i1, int i2) {
        return isEven(row(i1)) ? (i1 + i2 + 1) / 2 : (i1 + i2 - 1) / 2;
    }

    private void nextTurn() {
        currentPlayer = currentPlayer.opponent();
    }

    private void changePiecePosition(int origin, int target) {
        pieces.move(origin, target);

        int piece = getSquare(origin);
        setSquare(origin, EMPTY);
        setSquare(target, piece);
    }

    private void setSquare(int square, int value) {
        this.board[square] = value;
    }

    private int getSquare(int i) {
        if (i < 1 || i > 32) {
            return OUT_OF_BOUNDS;
        } else {
            return board[i];
        }
    }

    private boolean isEven(int row) {
        return row % 2 == 0;
    }

    private int row(int i) {
        return Math.floorDiv(i - 1, 4);
    }
}
