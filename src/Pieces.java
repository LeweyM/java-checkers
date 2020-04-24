import java.util.*;
import java.util.stream.Collectors;

public class Pieces {
    private Piece[] pieces;
    private int chainJumpingPiece;

    public Pieces(int[] state) {
        chainJumpingPiece = -1;
        pieces = new Piece[33];
        for (int i = 1; i < state.length; i++) {
            int cell = state[i];
            if (cell != 0) {
                pieces[i] = Piece.build(i, cell);
            }
        }
    }

    public List<Piece> getPlayerPieces(Player player) {
        return getAllPieces().stream()
                .filter(p -> p.belongsTo(player))
                .collect(Collectors.toList());
    }

    public void move(int origin, int target) {
        pieces[target] = pieces[origin];
        pieces[origin] = null;
    }

    public void take(int origin, int target) {
        move(origin, target);
        int victim = inbetweenIndex(origin, target);
        pieces[victim] = null;
        pieces[target].jump(target);
        chainJumpingPiece = target;
    }

    public List<Move> getLegalMoves(Player player) {
        if (chainJumpingPiece > 0) {
            return pieces[chainJumpingPiece].getJumps().stream()
                .filter(this::validateMove)
                .collect(Collectors.toList());
        }

        List<Move> moves = getPlayerPieces(player).stream()
            .map(Piece::getPossibleMoves)
            .flatMap(Collection::stream)
            .filter(this::validateMove)
            .collect(Collectors.toList());

        return jumpsOrMoves(moves);
    }

    public boolean isLegalMove(int origin, int target, Player player) {
        boolean playerPiece = origin > 0
                && origin <= 32
                && pieces[origin] != null
                && pieces[origin].belongsTo(player);

        return playerPiece && pieces[origin].getPossibleMoves().stream()
                .anyMatch(m -> target == m.target());
    }

    public void finishChainJumping() {
        chainJumpingPiece = -1;
    }


    private boolean validateMove(Move move) {
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

    private List<Piece> getAllPieces() {
        return Arrays.stream(pieces)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
