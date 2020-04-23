import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// board is index 1-32

public class Checkers {
    public static final int EMPTY = 0;
    public static final int OUT_OF_BOUNDS = 3;
    public static final int PLAYER_ONE_PAWN = 1;
    public static final int PLAYER_TWO_PAWN = -1;

    private int[] board;
    private Player currentPlayer = Player.ONE;

    public Checkers(int[] b) {
        board = b;
    }

    public Checkers() {
    }

    public void setup() {
        currentPlayer = Player.ONE;
        board = new int[]{OUT_OF_BOUNDS, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    }

    public int[] stateSlice() {
        return board.clone();
    }

    public List<Move> getLegalMoves(int i) {
        Piece piece = Piece.build(board, i, getSquare(i));

        if (piece == null) return Collections.emptyList();

        List<Move> takingMoves = piece.getJumps();
        if (!takingMoves.isEmpty()) {
            return takingMoves;
        }

        return piece.getMoves();
    }

    public List<Move> getAllLegalMoves() {
        List<Piece> pawns = getPlayerPieces();

        List<Move> takingMoves = pawns.stream()
                .map(Piece::getJumps)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        if (takingMoves.size() > 0) {
            return takingMoves;
        }

        return pawns.stream()
                .map(Piece::getMoves)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public void move(int origin, int target) throws IllegalArgumentException {
        Move move = new Move(origin, target);

        if (isLegalMove(origin, target)) {
            switch (move.type()) {
                case Move.NORMAL:
                    changePiecePosition(origin, target);
                    break;
                case Move.JUMP:
                    take(origin, target);
                    Piece piece = Piece.build(board, target, getSquare(target));
                    ArrayList<Move> jumps = piece.getJumps();

                    while (jumps.size() == 1) {
                        Move jump = jumps.get(0);
                        take(jump.origin(), jump.target());
                        piece = Piece.build(board, jump.target(), getSquare(jump.target()));
                        jumps = piece.getJumps();
                    }

                    if (jumps.size() > 1) return;

                    break;
            }

            nextTurn();

            List<Move> takingMoves = getCurrentPlayerJumps();
            if (takingMoves.size() == 1) {
                Move take = takingMoves.get(0);
                move(take.origin(), take.target());
            }

        } else {
            throw new IllegalArgumentException("Illegal move: [" + origin + "->" + target + "]");
        }
    }

    public int whoseTurn() {
        return currentPlayer.value();
    }

    private List<Move> getCurrentPlayerJumps() {
        return getPlayerPieces().stream()
                .map(Piece::getJumps)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void take(int origin, int target) {
        changePiecePosition(origin, target);
        this.setSquare(inbetweenIndex(origin, target), EMPTY);
    }

    private int inbetweenIndex(int i1, int i2) {
        return isEven(row(i1)) ? (i1 + i2 + 1) / 2 : (i1 + i2 - 1) / 2;
    }

    private List<Piece> getPlayerPieces() {
        List<Piece> playerPawnSquares = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            Piece piece = Piece.build(board, i, getSquare(i));
            if (piece != null && piece.belongsTo(currentPlayer)) {
                playerPawnSquares.add(piece);
            }
        }
        return playerPawnSquares;
    }

    private void nextTurn() {
        currentPlayer = currentPlayer.opponent();
    }

    private boolean isLegalMove(int origin, int target) {
        Piece piece = Piece.build(board, origin, getSquare(origin));
        if (piece == null) return false;
        return piece.belongsTo(currentPlayer) &&
                Stream.concat(
                        piece.getMoves().stream(),
                        piece.getJumps().stream())
                .anyMatch(move -> move.target() == target);
    }

    private void changePiecePosition(int origin, int target) {
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
