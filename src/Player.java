public enum Player {
    ONE(1) {
        @Override
        public Player opponent() {
            return TWO;
        }
    },
    TWO(2) {
        @Override
        public Player opponent() {
            return ONE;
        }
    };

    private final int value;
    Player(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public abstract Player opponent();
}
