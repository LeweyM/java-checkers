package Checkers;

public class Move {
    public static final String NORMAL = "NORMAL";
    public static final String JUMP = "JUMP";
    private final int origin;
    private final int target;
    private final String type;

    public Move(int origin, int target) {
        this.origin = origin;
        this.target = target;
        this.type = Math.abs(origin - target) < 6 ? Move.NORMAL : Move.JUMP;
    }

    @Override
    public String toString() {
        return "Checkers.Move{" +
                "origin=" + origin +
                ", target=" + target +
                ", type='" + type + '\'' +
                '}';
    }

    public int origin() {
        return this.origin;
    }

    public int target() {
        return this.target;
    }

    public String type() {
        return type;
    }

    public boolean isJump() {
        return type.equals(JUMP);
    }
}
