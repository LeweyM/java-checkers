package UI;

import Checkers.Checkers;
import Checkers.Move;
import Checkers.Subscriber;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class MySketch extends PApplet implements Subscriber {

    static final int SIZE = 500;
    static final int SQUARE_SIZE = SIZE / 8;
    private Checkers checkers;
    private int selectedCell;
    private List<Integer> highlightedSquares;
    private List<CheckerPiece> pieces;

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
        checkers.subscribeToMoveStream(this);
        buildPiecesFromState();
    }

    private void buildPiecesFromState() {
        pieces = new ArrayList<>();
        int[] stateSlice = checkers.stateSlice();
        for (int i = 0; i < stateSlice.length; i++) {
            int pieceType = stateSlice[i];
            switch (pieceType) {
                case 1:
                    pieces.add(new BluePawn(this, i));
                    break;
                case -1:
                    pieces.add(new RedPawn(this, i));
                    break;
            }
        }
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
        int index = boardCoordsToCheckersIndex(x, y);
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

    @Override
    public void nextMove(Move move) {
        System.out.println(move);
        buildPiecesFromState();
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
        pieces.forEach(CheckerPiece::draw);
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
                if (squareIsHighlighted(x, y)) fill(100,0,0);
                rect(x * SQUARE_SIZE, y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private boolean squareIsHighlighted(int x, int y) {
        return highlightedSquares.stream().anyMatch(i -> boardCoordsToCheckersIndex(x, y) == i);
    }

    private int boardCoordsToCheckersIndex(int x, int y) {
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
