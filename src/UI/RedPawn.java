package UI;

import processing.core.PApplet;

public class RedPawn extends CheckerPiece {
    private final PApplet s;

    public RedPawn(PApplet s, int location) {
        super(s, location);
        this.s = s;
    }

    @Override
    public void draw() {
        s.fill(0,0,200);
        super.draw();
    }
}
