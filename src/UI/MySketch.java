package UI;

import Checkers.Checkers;
import Checkers.Move;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class MySketch extends PApplet {

    private static final int SIZE = 500;
    private static final int SQUARE_SIZE = SIZE / 8;
    private Checkers checkers;
    private static final float HALF_SQUARE = SQUARE_SIZE / 2f;
    private int selectedCell;
    private List<Integer> highlightedSquares;

    public static void main(String[] args) {
        String[] processingArgs = {"MySketch"};
        MySketch mySketch = new MySketch();
        PApplet.runSketch(processingArgs, mySketch);
    }

    public void settings() {
        size(SIZE, SIZE);
    }
    public void setup() {
        checkers = new Checkers();
        checkers.setup();
        highlightedSquares = new ArrayList<>();
        selectedCell = -1;
    }

    public void draw() {
        background(64);
        drawBoard();
        drawPieces();
    }

    @Override
    public void mouseClicked() {
        int y = mouseY / SQUARE_SIZE;
        int x = mouseX / (SQUARE_SIZE);
        int index = boardCoordsToCheckersIndex(y, x);
        if (index > 0) {
            if (isSelected(index)) {
                unselectCell();
            } else if (isHighlighted(index)) {
                checkers.move(selectedCell, index);
                unselectCell();
            } else {
                selectCell(index);
            }
        }

    }

    private boolean isSelected(int index) {
        return index == selectedCell;
    }

    private boolean isHighlighted(int index) {
        return highlightedSquares.stream().anyMatch(c -> c == index);
    }

    private void unselectCell() {
        selectedCell = 0;
        highlightedSquares.clear();
    }

    private void selectCell(int i) {
        selectedCell = i;
        highlightedSquares.clear();
        List<Move> legalMoves = checkers.getLegalMoves(i);
        for (Move move: legalMoves) {
            highlightSquare(move.origin());
            highlightSquare(move.target());
        }
    }

    private void highlightSquare(int i) {
        highlightedSquares.add(i);
    }

    private void drawPieces() {
        int[] state = checkers.stateSlice();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int i = boardCoordsToCheckersIndex(y, x);
                if (i > 0) {
                    if (state[i] == 1) {
                        fill(200, 0, 0);
                        drawChecker(y, x);
                    } else if (state[i] == -1) {
                        fill(0,0,200);
                        drawChecker(y, x);
                    }
                }
            }
        }
    }

    private void drawChecker(float y, float x) {
        ellipse(
                x * SQUARE_SIZE + HALF_SQUARE,
                y * SQUARE_SIZE + HALF_SQUARE,
                SQUARE_SIZE * 0.8f,
                SQUARE_SIZE * 0.8f);
    }

    private void drawBoard() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (isEven(y)) {
                    if (isEven(x)) {
                        fill(0);
                    } else {
                        fill(255 - (30 * y+x), 255 - (20 * x+y), 255 - (10 * x+y));
                    }
                } else {
                    if (isEven(x)) {
                        fill(255 - (30 * y+x), 255 - (20 * x+y), 255 - (10 * x+y));
                    } else {
                        fill(0);
                    }
                }
                if (squareIsHighlighted(y, x)) fill(100,0,0);
                rect(x * SQUARE_SIZE, y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private boolean squareIsHighlighted(int y, int x) {
        return highlightedSquares.stream().anyMatch(i -> boardCoordsToCheckersIndex(y, x) == i);
    }

    private int boardCoordsToCheckersIndex(int y, int x) {
        int index = (y*8) + x;
        if (isEven(y)) {
            if (isEven(index)) return -1;
            return((index / 2) + 1);
        } else {
            if (!isEven(index)) return -1;
            return(((index) / 2) + 1);
        }
    }

    private boolean isEven(int i) {
        return i % 2 == 0;
    }

}
