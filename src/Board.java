import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {
    private Piece[] pieces;

    public Board(int[] state) {
        pieces = new Piece[33];
        for (int i = 1; i < state.length; i++) {
            int cell = state[i];
            if (cell != 0) {
                pieces[i] = Piece.build(cell);
            }
        }
    }

    public int[] stateArray() {
        int[] state = new int[33];
        state[0] = 3; //EOF
        for (int i = 1; i < pieces.length; i++) {
            if (pieces[i] == null) {
                state[i] = 0;
            } else {
                state[i] = pieces[i].getCode();
            }
        }
        return state;
    }

    public void take(int origin, int target) {
        move(origin, target);
        pieces[inbetweenIndex(origin, target)] = null;
    }

    public void move(int origin, int target) {
        pieces[target] = pieces[origin];
        pieces[origin] = null;
    }

    public List<Move> getLegalJumps(Player player) {
        return playerPieceLocationsStream(player)
                .mapToObj(this::getJumps)
                .flatMap(Collection::stream)
                .filter(this::isValidMove)
                .collect(Collectors.toList());
    }

    public List<Move> getLegalMoves(Player player) {
        List<Move> moves = playerPieceLocationsStream(player)
                .mapToObj(this::getMoves)
                .flatMap(Collection::stream)
                .filter(this::isValidMove)
                .collect(Collectors.toList());

        return jumpsOrMoves(moves);
    }

    public boolean isLegalMove(int origin, int target, Player player) {
        boolean playerPiece = origin > 0
                && origin <= 32
                && pieces[origin] != null
                && pieces[origin].belongsTo(player);

        return playerPiece && getMoves(origin).stream()
                .anyMatch(m -> target == m.target());
    }

    private ArrayList<Move> getJumps(int location) {
        return pieces[location].getJumps(location);
    }

    private ArrayList<Move> getMoves(int location) {
        return pieces[location].getPossibleMoves(location);
    }

    private IntStream playerPieceLocationsStream(Player player) {
        return IntStream.range(1, 32)
                .filter(loc -> pieces[loc] != null)
                .filter(loc -> pieces[loc].belongsTo(player));
    }

    private boolean isValidMove(Move move) {
        if (move.target() > 32 || move.target() < 1) return false;

        switch (move.type()) {
            case Move.NORMAL:
                return isEmptySquare(move.target());
            case Move.JUMP:
                return isEmptySquare(move.target()) && opponentBetween(move.origin(), move.target());
            default:
                return true;
        }
    }

    private boolean opponentBetween(int origin, int target) {
        int victim = inbetweenIndex(origin, target);
        return pieces[victim] != null && pieces[victim].belongsTo(getPlayer(origin).opponent());
    }

    private Player getPlayer(int i) {
        return pieces[i].belongsTo(Player.ONE) ? Player.ONE : Player.TWO;
    }

    private boolean isEmptySquare(int target) {
        return pieces[target] == null;
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

    private int inbetweenIndex(int i1, int i2) {
        return isEven(row(i1)) ? (i1 + i2 + 1) / 2 : (i1 + i2 - 1) / 2;
    }

    private boolean isEven(int row) {
        return row % 2 == 0;
    }

    private int row(int i) {
        return Math.floorDiv(i - 1, 4);
    }
}
