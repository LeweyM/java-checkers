package UI;

import processing.core.PApplet;

public class BluePawn extends CheckerPiece {
    private final PApplet s;

    public BluePawn(PApplet s, int location) {
        super(s, location);
        this.s = s;
    }

    @Override
    public void draw() {
        s.fill(200,0,0);
        super.draw();
    }
}
