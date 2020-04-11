public class Move {
    private final int origin;
    private final int target;

    public Move(int origin, int target) {
        this.origin = origin;
        this.target = target;
    }

    public int origin() {
        return this.origin;
    }

    public int target() {
        return this.target;
    }
}
