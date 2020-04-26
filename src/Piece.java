import java.util.ArrayList;
import java.util.List;

public class Piece {
    private List<PieceAbility> abilities;
    private Player player;
    private int location;

    public static Piece build(int location, int value) {
        if (value == 1) {
            Piece p = new Piece(Player.ONE, location);
            p.setAbility(new CanGoDown());
            return p;
        } else if (value == -1) {
            Piece p = new Piece(Player.TWO, location);
            p.setAbility(new CanGoUp());
            return p;
        }
        return null;
    }

    public ArrayList<Move> getPossibleMoves() {
        ArrayList<Move> moves = new ArrayList<Move>() {};
        for (PieceAbility ability: abilities) {
            moves.addAll(ability.getMoves(location));
            moves.addAll(ability.getJumps(location));
        }
        return moves;
    }

    public ArrayList<Move> getJumps() {
        ArrayList<Move> moves = new ArrayList<Move>() {};
        for (PieceAbility ability: abilities) {
            moves.addAll(ability.getJumps(location));
        }
        return moves;
    }

    public boolean belongsTo(Player p) {
        return this.player == p;
    }

    private Piece(Player player, int location) {
        this.player = player;
        this.location = location;
        abilities = new ArrayList<>();
    }

    private void setAbility(PieceAbility ability) {
        this.abilities.add(ability);
    }

    public void move(int target) {
        this.location = target;
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
