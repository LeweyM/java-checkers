package UI;

import processing.core.PApplet;

public class RedPawn extends CheckerPiece {
    private final PApplet s;

    public RedPawn(PApplet s, TransitionSubscriber sub, int location) {
        super(s, sub, location);
        this.s = s;
    }

    @Override
    public void draw() {
        s.fill(200,0,0);
        super.draw();
    }
}
