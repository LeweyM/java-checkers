package UI;

import Checkers.Checkers;
import Checkers.Move;
import Checkers.Subscriber;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MySketch extends PApplet implements Subscriber, TransitionSubscriber {

    static final int SIZE = 500;
    static final int SQUARE_SIZE = SIZE / 8;
    private Checkers checkers;
    private int selectedCell;
    private List<Integer> highlightedSquares;
    private List<CheckerPiece> pieces;
    private Queue<Move> moveQueue;
    private boolean isAnimating;
    private List<int[]> arrows;

    public MySketch() {
    }

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
        arrows = new ArrayList<>();
        selectedCell = -1;
        checkers.subscribeToMoveStream(this);
        moveQueue = new ArrayDeque<>();
        buildPiecesFromState();
    }

    private void buildPiecesFromState() {
        pieces = new ArrayList<>();
        int[] stateSlice = checkers.stateSlice();
        for (int i = 0; i < stateSlice.length; i++) {
            int pieceType = stateSlice[i];
            switch (pieceType) {
                case 1:
                    pieces.add(new BluePawn(this, this, i));
                    break;
                case -1:
                    pieces.add(new RedPawn(this, this, i));
                    break;
            }
        }
    }

    public void draw() {
        background(64);
        drawBoard();
        drawPieces();
        if (!isAnimating) startNextTransition();
        drawArrows();
    }

    @Override
    public void transitionEnded() {
        isAnimating = false;
    }

    private void startNextTransition() {
        Move move = moveQueue.poll();
        if (move != null) {
            isAnimating = true;
            addArrow(move.origin(), move.target());
//            animate(move);
        } else {
            buildPiecesFromState();
        }
    }

    private void drawArrows() {
        arrows.forEach(arrow -> {
                    PVector from = new PVector(xPosition(arrow[0]), yPosition(arrow[0]));
                    PVector to = new PVector(xPosition(arrow[1]), yPosition(arrow[1]));
                    drawArrow(from, to);
                }
        );
    }

    private void drawArrow(PVector from, PVector to) {
        float arrowSize = dist(from.x, from.y, to.x, to.y);
        PVector up = new PVector(0, arrowSize);

        pushMatrix();

        translate(from.x, from.y);
        rotate(PVector.angleBetween(up, to.sub(from)));

        strokeWeight(2);
        line(up.x, up.y, up.x + 15, up.y - 15);
        line(up.x, up.y, up.x - 2, up.y - 15);

        to.normalize();

        float arch = to.x * 20;

        noFill();
        stroke(0, 0, 0, 0);
        bezier(0, 0, arch, arrowSize*0.35f, arch, arrowSize*0.65f, 0, arrowSize);
        stroke(0, 0, 0, 50);
        bezier(0, 0, -arch, arrowSize*0.35f, -arch, arrowSize*0.65f, 0, arrowSize);

        pushMatrix();
        translate(up.x, up.y);
        rotate(PI / 8 * 6 + (to.x / 10));
        line(0, 0, 0, 15);
        popMatrix();

        pushMatrix();
        translate(up.x, up.y);
        rotate(PI / 8 * 10 + (to.x / 10));
        line(0, 0, 0, 15);
        popMatrix();

        popMatrix();
    }

    private int xPosition(int i) {
        return xOnBoardGrid(i) * SQUARE_SIZE + (SQUARE_SIZE / 2);
    }

    private int yPosition(int i) {
        return yOnBoardGrid(i) * SQUARE_SIZE + (SQUARE_SIZE / 2);
    }

    private int yOnBoardGrid(int i) {
        return row(i);
    }

    private int xOnBoardGrid(int i) {
        return isEvenRow(i)
                ? ((i - 1) % 4) * 2 + 1
                : ((i - 1) % 4) * 2;
    }

    private boolean isEvenRow(int i) {
        return row(i) % 2 == 0;
    }

    private int row(int i) {
        return Math.floorDiv(i - 1, 4);
    }

    private void addArrow(int from, int to) {
        arrows.add(new int[]{from, to});
    }

    private void animate(Move move) {
        Optional<CheckerPiece> pieceOptional = pieces.stream().filter(p -> p.getLocation() == move.origin()).findFirst();
        if (pieceOptional.isPresent()) {
            CheckerPiece piece = pieceOptional.get();
            piece.translate(move);
            if (move.isJump()) {
                System.out.println(move);
                System.out.println(inbetweenIndex(move.origin(), move.target()));
                pieces.stream().filter(p -> p.getLocation() == inbetweenIndex(move.origin(), move.target()))
                        .findAny()
                        .orElseGet(null)
                        .remove();
            }
        }
    }

    private int inbetweenIndex(int i1, int i2) {
        return isEven(Math.floorDiv(i1 - 1, 4)) ? (i1 + i2 + 1) / 2 : (i1 + i2 - 1) / 2;
    }

    @Override
    public void mouseClicked() {
        int y = mouseY / SQUARE_SIZE;
        int x = mouseX / (SQUARE_SIZE);
        int index = boardCoordsToCheckersIndex(x, y);
        if (index <= 0) return;

        if (isSelected(index)) {
            unselectCell();
        } else if (isHighlighted(index)) {
            movePiece(index);
        } else {
            selectCell(index);
        }

    }

    private void movePiece(int index) {
        arrows.clear();
        checkers.move(selectedCell, index);
        transitionEnded();
        unselectCell();
    }

    @Override
    public void nextMove(Move move) {
        moveQueue.add(move);
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
        for (Move move : legalMoves) {
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
                        fill(255 - (30 * y + x), 255 - (20 * x + y), 255 - (10 * x + y));
                    }
                } else {
                    if (isEven(x)) {
                        fill(255 - (30 * y + x), 255 - (20 * x + y), 255 - (10 * x + y));
                    } else {
                        fill(0);
                    }
                }
                if (squareIsHighlighted(x, y)) fill(100, 0, 0);
                rect(x * SQUARE_SIZE, y * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private boolean squareIsHighlighted(int x, int y) {
        return highlightedSquares.stream().anyMatch(i -> boardCoordsToCheckersIndex(x, y) == i);
    }

    private int boardCoordsToCheckersIndex(int x, int y) {
        int index = (y * 8) + x;
        if (isEven(y)) {
            if (isEven(index)) return -1;
            return ((index / 2) + 1);
        } else {
            if (!isEven(index)) return -1;
            return (((index) / 2) + 1);
        }
    }

    private boolean isEven(int i) {
        return i % 2 == 0;
    }
}
