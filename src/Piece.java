import java.util.ArrayList;
import java.util.List;

public class Piece {
    private List<PieceAbility> abilities;
    private Player player;

    public static Piece build(int[] board, int location, int value) {
        if (value == 1) {
            Piece p = new Piece(Player.ONE);
            p.setAbility(new CanGoDown(board, location, Player.ONE));
            return p;
        } else if (value == -1) {
            Piece p = new Piece(Player.TWO);
            p.setAbility(new CanGoUp(board, location, Player.TWO));
            return p;
        }
        return null;
    }

    public ArrayList<Move> getMoves() {
        ArrayList<Move> moves = new ArrayList<Move>() {};
        for (PieceAbility ability: abilities) {
            moves.addAll(ability.getMoves());
        }
        return moves;
    }

    public ArrayList<Move> getJumps() {
        ArrayList<Move> moves = new ArrayList<Move>() {};
        for (PieceAbility ability: abilities) {
            moves.addAll(ability.getJumps());
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

}
