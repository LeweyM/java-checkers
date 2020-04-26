package UI;

import processing.core.PApplet;

public class CheckerPiece {
        private static final int SQUARE_SIZE = MySketch.SQUARE_SIZE;
        private static final int HALF_SQUARE = SQUARE_SIZE / 2;
        private final PApplet s;
        private final int location;

        public CheckerPiece(PApplet s, int location) {
            this.s = s;
            this.location = location;
        }

        public void draw() {
            drawChecker();
        }

        private void drawChecker() {
            s.ellipse(
                    xOnBoardGrid() * SQUARE_SIZE + HALF_SQUARE,
                    yOnBoardGrid() * SQUARE_SIZE + HALF_SQUARE,
                    SQUARE_SIZE * 0.8f,
                    SQUARE_SIZE * 0.8f);
        }

        private int yOnBoardGrid() {
            return row();
        }

        private int xOnBoardGrid() {
            return isEvenRow()
                    ? ((location-1) % 4) * 2 +1
                    : ((location-1) % 4) * 2;
        }

        private boolean isEvenRow() {
            return row() % 2 == 0;
        }

        private int row() {
            return Math.floorDiv(location - 1, 4);
        }
}
