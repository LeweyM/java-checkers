public enum Player {
    ONE {
        @Override
        public Player opponent() {
            return TWO;
        }
    },
    TWO {
        @Override
        public Player opponent() {
            return ONE;
        }
    };

    public abstract Player opponent();
}
