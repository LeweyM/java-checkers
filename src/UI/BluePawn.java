package UI;

import processing.core.PApplet;

public class BluePawn extends CheckerPiece {
    private final PApplet s;

    public BluePawn(PApplet s, TransitionSubscriber sub, int location) {
        super(s, sub, location);
        this.s = s;
    }

    @Override
    public void draw() {
        s.fill(0,0,200);
        super.draw();
    }
}
