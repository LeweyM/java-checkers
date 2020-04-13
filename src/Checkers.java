import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// board is index 1-32

public class Checkers {
    private static final int EMPTY = 0;
    private static final int OUT_OF_BOUNDS = 3;
    private static final int PLAYER_ONE_PAWN = 1;
    private static final int PLAYER_TWO_PAWN = -1;
    private static final int PLAYER_ONE = 1;
    private static final int PLAYER_TWO = 2;

    private int[] board;
    private int currentPlayer = PLAYER_ONE;

    public Checkers(int[] b) {
        board = b;
    }

    public Checkers() { }

    public void setup() {
        currentPlayer = PLAYER_ONE;
        board = new int[]{OUT_OF_BOUNDS, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    }

    public int[] stateSlice() {
        return board.clone();
    }

    public List<Move> getLegalMoves(int i) {
        List<Move> takingMoves = availableJumps(i);
        if (!takingMoves.isEmpty()) {
            return takingMoves;
        }

        ArrayList<Move> moves = new ArrayList<Move>() {};
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

    public List<Move> getAllLegalMoves() {
        List<Move> takingMoves = playerTakingMoves();

        if (takingMoves.size() > 0) {
            return takingMoves;
        }

        return getPawns(currentPlayer).stream()
                .map(this::getLegalMoves)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public void move(int origin, int target) throws IllegalArgumentException {
        if (isLegalMove(origin, target)) {
            changePiecePosition(origin, target);
            nextTurn();

            List<Move> takingMoves = playerTakingMoves();

            if (takingMoves.size() == 1) {
                Move take = takingMoves.get(0);
                take(take.origin(), take.target());
                nextTurn();
            }

        } else {
            throw new IllegalArgumentException("Illegal move: [" + origin + "->" + target + "]");
        }
    }



    private void take(int origin, int target) {
        changePiecePosition(origin, target);
        this.setSquare(inbetweenIndex(origin, target), EMPTY);
    }

    private int inbetweenIndex(int i1, int i2) {
        return isEven(row(i1)) ? (i1 + i2 + 1) / 2 : (i1 + i2 - 1) / 2;
    }

    private List<Integer> getPawns(int player) {
        List<Integer> playerPawnSquares = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (getSquare(i) == pawnValue(player)) {
                playerPawnSquares.add(i);
            }
        }
        return playerPawnSquares;
    }

    private List<Move> playerTakingMoves() {
        return getPawns(currentPlayer).stream()
                .map(this::availableJumps)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private List<Move> availableJumps(int i) {
        List<Move> moves = new ArrayList<>();
        if (this.getSquare(i) > 0) {
            if (canJumpDown(i, downLeft(downLeft(i)))) {
                moves.add(new Move(i, downLeft(downLeft(i))));
            }

            if (canJumpDown(i, downRight(downRight(i)))) {
                moves.add(new Move(i, downRight(downRight(i))));
            }
        } else if (this.getSquare(i) < 0) {
            if (canJumpUp(i, upLeft(upLeft(i)))) {
                moves.add(new Move(i, upLeft(upLeft(i))));
            }

            if (canJumpUp(i, upRight(upRight(i)))) {
                moves.add(new Move(i, upRight(upRight(i))));
            }
        }
        return moves;
    }

    private boolean canJumpUp(int origin, int destination) {
        return row(origin) - 2 == row(destination)
                && getSquare(destination) == EMPTY
                && isOpponentPiece(inbetweenIndex(origin, destination));
    }

    private boolean canJumpDown(int origin, int destination) {
        return row(origin) + 2 == row(destination)
                && getSquare(destination) == EMPTY
                && isOpponentPiece(inbetweenIndex(origin, destination));
    }

    private boolean isOpponentPiece(int origin) {
        return getSquare(origin) == pawnValue(opponent()); //todo - kings
    }

    private boolean isCurrentPlayerPiece(int origin) {
        return getSquare(origin) == pawnValue(currentPlayer); //todo - kings
    }

    private int opponent() {
        return currentPlayer == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE;
    }

    private void nextTurn() {
        currentPlayer = opponent();
    }

    private boolean isLegalMove(int origin, int target) {
        return isCurrentPlayerPiece(origin) && getLegalMoves(origin)
            .stream()
            .anyMatch(move -> move.target() == target);
    }

    private void changePiecePosition(int origin, int target) {
        int piece = getSquare(origin);
        setSquare(origin, EMPTY);
        setSquare(target, piece);
    }

    private int pawnValue(int player) {
        return player == PLAYER_ONE ? PLAYER_ONE_PAWN : PLAYER_TWO_PAWN;
    }

    private Move leftUpMove(int i) {
        int leftIndex = upLeft(i);
        if (row(i) - 1 == row(leftIndex) && getSquare(leftIndex) == EMPTY) {
            return new Move(i, leftIndex);
        }
        return null;
    }

    private Move rightUpMove(int i) {
        int rightIndex = upRight(i);
        if (row(i) - 1 == row(rightIndex) && getSquare(rightIndex) == EMPTY) {
            return new Move(i, rightIndex);
        }
        return null;
    }

    private Move leftDownMove(int i) {
        int leftIndex = downLeft(i);
        if (row(i) + 1 == row(leftIndex) && getSquare(leftIndex) == EMPTY) {
            return new Move(i, leftIndex);
        }
        return null;
    }

    private Move rightDownMove(int i) {
        int rightIndex = downRight(i);
        if (row(i) + 1 == row(rightIndex) && getSquare(rightIndex) == EMPTY) {
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

    private int downLeft(int i) {
        return isEven(row(i)) ? i + 4 : i + 3;
    }

    private int downRight(int i) {
        return isEven(row(i)) ? i + 5 : i + 4;
    }

    private void setSquare(int square, int value) {
        this.board[square] = value;
    };

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
        return Math.floorDiv(i-1, 4);
    }
}
