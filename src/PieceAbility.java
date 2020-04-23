import java.util.List;

public interface PieceAbility {
    List<Move> getMoves(int location);
    List<Move> getJumps(int location);
}
