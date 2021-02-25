package UI;

import Checkers.Move;
import processing.core.PApplet;

public class CheckerPiece {
    private static final int SQUARE_SIZE = MySketch.SQUARE_SIZE;
    private static final int HALF_SQUARE = SQUARE_SIZE / 2;
    private static final int ANIMATION_STEP_COUNT = 20;
    private final PApplet s;
    private int location;
    private int animationCounter;
    private boolean isAnimating;
    private TransitionSubscriber subscriber;
    private boolean isDead;
    private int preLocation;

    public CheckerPiece(PApplet s, TransitionSubscriber subscriber, int location) {
        this.s = s;
        this.subscriber = subscriber;
        this.location = location;
        this.isDead = false;
    }

    public int getLocation() {
        return location;
    }

    public void draw() {
        if (isAnimating) updateAnimation();
        if (!isDead) drawChecker();
    }

    private void updateAnimation() {
        if (animationCounter == 0) {
            endAnimation();
            return;
        }
        animationCounter--;
    }

    private void endAnimation() {
        isAnimating = false;
        subscriber.transitionEnded();
    }

    private void drawChecker() {
        float xDiff = xPosition(preLocation) - xPosition(location);
        float yDiff = yPosition(preLocation) - yPosition(location);

        float xOffset = xDiff / ANIMATION_STEP_COUNT * (animationCounter);
        float yOffset = yDiff / ANIMATION_STEP_COUNT * (animationCounter);

        s.ellipse(
                xPosition(location) + xOffset,
                yPosition(location) + yOffset,
                SQUARE_SIZE * 0.8f,
                SQUARE_SIZE * 0.8f);
    }

    private int xPosition(int i) {
        return xOnBoardGrid(i) * SQUARE_SIZE + HALF_SQUARE;
    }

    private int yPosition(int i) {
        return yOnBoardGrid(i) * SQUARE_SIZE + HALF_SQUARE;
    }

    private int yOnBoardGrid(int i) {
        return row(i);
    }

    private int xOnBoardGrid(int i) {
        return isEvenRow(i)
                ? ((i-1) % 4) * 2 +1
                : ((i-1) % 4) * 2;
    }

    private boolean isEvenRow(int i) {
        return row(i) % 2 == 0;
    }

    private int row(int i) {
        return Math.floorDiv(i - 1, 4);
    }

    public void translate(Move move) {
        isAnimating = true;
        animationCounter = ANIMATION_STEP_COUNT;
        preLocation = location;
        location = move.target();
    }

    public void remove() {
        isDead = true;
    }
}
