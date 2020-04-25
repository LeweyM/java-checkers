import java.util.List;
import java.util.stream.Collectors;

// board is index 1-32

public class Checkers {
    public static final int OUT_OF_BOUNDS = 3;
    private Player currentPlayer = Player.ONE;
    private Board board;

    public Checkers(int[] b) {
        board = new Board(b);
    }

    public Checkers() {
    }

    public String prettyString() {
        int[] state = board.stateArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < state.length; i++) {
            if (i % 4 == 1) {
                sb.append("|");
            }
            if (isEvenRow(i)) {
                sb.append(" ");
                sb.append(pieceString(state, i));
            } else {
                sb.append(pieceString(state, i));
                sb.append(" ");
            }
            if (i % 4 == 0) {
                sb.append("|  |" + (i-3) + " " + (i-2) + " " + (i-1) + " " + i + "|\n");
            }
        }
        return sb.toString();
    }

    public void setup() {
        currentPlayer = Player.ONE;
        int[] startingPosition = {OUT_OF_BOUNDS, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        board = new Board(startingPosition);
    }

    public int[] stateSlice() {
        return board.stateArray();
    }

    public List<Move> getLegalMoves(int i) {
        return board.getLegalMoves(currentPlayer).stream()
                .filter(m -> m.origin() == i)
                .collect(Collectors.toList());
    }

    public List<Move> getAllLegalMoves() {
        return this.board.getLegalMoves(this.currentPlayer);
    }

    public void move(int origin, int target) {
        if (board.isLegalMove(origin, target, currentPlayer)) {
            Move move = new Move(origin, target);

            switch (move.type()) {
                case Move.NORMAL:
                    board.move(origin, target);
                    break;
                case Move.JUMP:
                    board.take(origin, target);
                    List<Move> chainJumps = board.jumpingPieceChainJumps();
                    while (chainJumps.size() == 1) {
                            Move jump = chainJumps.get(0);
                            board.take(jump.origin(), jump.target());
                            chainJumps = board.jumpingPieceChainJumps();
                    }
                    if (chainJumps.size() > 1) {
                        return;
                    } else {
                        break;
                    }
            }

            nextTurn();

            List<Move> takingMoves = board.getLegalJumps(currentPlayer);
            if (takingMoves.size() == 1) {
                Move forcedMove = takingMoves.get(0);
                move(forcedMove.origin(), forcedMove.target());
            }
        } else {
            throw new IllegalArgumentException("Illegal move: [" + origin + "->" + target + "]");
        }
    }

    public int whoseTurn() {
        return currentPlayer.value();
    }

    private String pieceString(int[] board, int i) {
        switch (board[i]) {
            case 1: return "o";
            case 2: return "O";
            case -1: return "x";
            case -2: return "X";
            case 0: return "_";
            default: return "";
        }
    }

    private void nextTurn() {
        currentPlayer = currentPlayer.opponent();
    }

    private boolean isEvenRow(int i) {
        return Math.floorDiv(i - 1, 4) % 2 == 0;
    }
}
