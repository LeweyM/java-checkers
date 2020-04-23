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

    private int[] board;
    private Player currentPlayer = Player.ONE;
    private int jumpChainingPiece;

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
        if (!squareHasPiece(i)) return Collections.emptyList();

        Piece piece = Piece.build(i, getSquare(i));
        assert piece != null;

        if (anotherPieceIsJumping(i)) return Collections.emptyList(); //have isJumping be part of piece - piece has to move with jump

        return jumpsOrMoves(validMoves(piece));
    }

    private boolean squareHasPiece(int i) {
        int val = getSquare(i);
        return val <= 2 && val >= -2 && val != EMPTY;
    }

    private boolean anotherPieceIsJumping(int i) {
        return jumpChainingPiece > 0 && jumpChainingPiece != i;
    }

    public List<Move> getAllLegalMoves() {
        List<Piece> pieces = jumpChainingPiece > 0
                ? Collections.singletonList(Piece.build(jumpChainingPiece, getSquare(jumpChainingPiece)))
                : getPlayerPieces();

        List<Move> allMoves = pieces.stream()
                .map(this::validMoves)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return jumpsOrMoves(allMoves);
    }

    private List<Move> jumpsOrMoves(List<Move> moves) {
        List<Move> jumps = moves.stream()
                .filter(m -> m.type().equals(Move.JUMP))
                .collect(Collectors.toList());
        if (jumps.size() > 0) {
            return jumps;
        } else {
            return moves;
        }
    }

    public void move(int origin, int target) {
        Move move = makeMove(origin, target);

        switch (move.type()) {
            case Move.NORMAL:
                changePiecePosition(origin, target);
                break;
            case Move.JUMP:
                take(origin, target);
                Piece piece = Piece.build(target, getSquare(target));
                List<Move> jumps = getLegalJumps(piece);

                if (jumps.size() > 0) jumpChainingPiece = target;

                while (jumps.size() == 1) {
                    Move jump = jumps.get(0);
                    jumpChainingPiece = jump.target();
                    take(jump.origin(), jump.target());
                    piece.jump(jump.target());
                    jumps = getLegalJumps(piece);
                }

                if (jumps.size() > 1) return;

                jumpChainingPiece = -1;
                break;
        }

        nextTurn();

        List<Move> takingMoves = getCurrentPlayerJumps();
        if (takingMoves.size() == 1) {
            Move take = takingMoves.get(0);
            move(take.origin(), take.target());
        }
    }

    private Move makeMove(int origin, int target) {
        if (!isLegalMove(origin, target)) throw new IllegalArgumentException("Illegal move: [" + origin + "->" + target + "]");
        return new Move(origin, target, Math.abs(origin - target) < 6 ? Move.NORMAL : Move.JUMP);
    }

    public int whoseTurn() {
        return currentPlayer.value();
    }

    private List<Move> validMoves(Piece piece) {
        // better would be move.validate() ... maybe pass board as param
        return piece.getPossibleMoves().stream()
                .filter(m -> getSquare(m.target()) == EMPTY)
                .filter(m -> {
                    if (m.type() != Move.JUMP) return true;
                    return isOpponentPiece(inbetweenIndex(m.origin(), m.target()));
                }).collect(Collectors.toList());
    }

    private List<Move> getLegalJumps(Piece piece) {
        return piece.getJumps().stream()
                .filter(m -> getSquare(m.target()) == EMPTY)
                .filter(m -> isOpponentPiece(inbetweenIndex(m.origin(), m.target())))
                .collect(Collectors.toList());
    }

    private boolean isOpponentPiece(int i) {
        Piece piece = Piece.build(i, getSquare(i));
        return piece != null && piece.belongsTo(currentPlayer.opponent());
    }

    private List<Move> getCurrentPlayerJumps() {
        return getPlayerPieces().stream()
                .map(piece -> getLegalJumps(piece))
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
            Piece piece = Piece.build(i, getSquare(i));
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
        Piece piece = Piece.build(origin, getSquare(origin));
        if (piece == null) return false;
        return piece.belongsTo(currentPlayer) &&
                piece.getPossibleMoves().stream()
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
