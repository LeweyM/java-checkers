import java.util.ArrayList;
import java.util.List;

public class Piece {
    private List<PieceAbility> abilities;
    private Player player;

    public static Piece build(int value) {
        if (value == 1) {
            Piece p = new Piece(Player.ONE);
            p.setAbility(new CanGoDown());
            return p;
        } else if (value == -1) {
            Piece p = new Piece(Player.TWO);
            p.setAbility(new CanGoUp());
            return p;
        }
        return null;
    }

    public ArrayList<Move> getPossibleMoves(int location) {
        ArrayList<Move> moves = new ArrayList<Move>() {};
        for (PieceAbility ability: abilities) {
            moves.addAll(ability.getMoves(location));
            moves.addAll(ability.getJumps(location));
        }
        return moves;
    }

    public ArrayList<Move> getJumps(int location) {
        ArrayList<Move> moves = new ArrayList<Move>() {};
        for (PieceAbility ability: abilities) {
            moves.addAll(ability.getJumps(location));
        }
        return moves;
    }

    public boolean belongsTo(Player p) {
        return this.player == p;
    }

    private Piece(Player player) {
        this.player = player;
        abilities = new ArrayList<>();
    }

    private void setAbility(PieceAbility ability) {
        this.abilities.add(ability);
    }

    public int getCode() {
        if (player == Player.ONE) {
            return 1;
        } else if (player == Player.TWO) {
            return -1;
        } else {
            return 0;
        }
    }
}
