import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckersTest {

    @Nested
    @DisplayName("From board Constructor")
    class FromBoardConstructor {


        @Test
        public void should_initialize_with_pieces_from_constructor() {
            int[] board = new int[]{1,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0, 0,0,0,0};
            Checkers checkers = new Checkers(board);

            String expectedState = "[1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]";
            assertEquals(checkers.toString(), expectedState);
        }


    }

    @Nested
    @DisplayName("Setup")
    class Setup {

        @Test
        public void setup_should_initialize_the_pieces() {
            Checkers checkers = new Checkers();
            checkers.setup();

            String expectedState = "[1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1]";
            assertEquals(checkers.toString(), expectedState);
        }

    }

    @Nested
    @DisplayName("getLegalMoves")
    class getLegalMoves {

        @Test
        public void should_get_empty_list_for_piece_without_legal_moves() {
            Checkers checkers = new Checkers();
            checkers.setup();

            List<Move> legalMoves = checkers.getLegalMoves(0);

            assertEquals(0, legalMoves.size());
        }

        @Test
        public void should_get_legal_moves_for_some_pieces() {
            Checkers checkers = new Checkers();
            checkers.setup();

            List<Move> legalMoves = checkers.getLegalMoves(8);

            assertEquals(1, legalMoves.size());
            assertEquals(8, legalMoves.get(0).origin());
            assertEquals(12, legalMoves.get(0).target());
        }
    }
}