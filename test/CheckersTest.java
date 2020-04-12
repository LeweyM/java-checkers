import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.*;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
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
        public void should_get_empty_list_for_non_board_square() {
            Checkers checkers = new Checkers();
            checkers.setup();

            List<Move> legalMoves = checkers.getLegalMoves(100);

            assertEquals(0, legalMoves.size());
        }

        @Test
        public void should_get_empty_list_for_piece_without_legal_moves() {
            Checkers checkers = new Checkers();
            checkers.setup();

            List<Move> legalMoves = checkers.getLegalMoves(1);

            assertEquals(0, legalMoves.size());
        }
        
        @Nested
        @DisplayName("when on an odd row")
        class OddRow {
            Checkers checkers;

            @BeforeEach
            void setUp() {
                int[] board = new int[]{0,0,0,0, 1,0,0,1, 0,0,0,0};
                checkers = new Checkers(board);
            }

            @Test
            void should_get_one_legal_move_for_left_edge_piece() {
                List<Move> legalMoves = checkers.getLegalMoves(5);
                assertEquals(legalMoves.size(), 1);
                assertThat(legalMoves, hasItem(matchingMove(5, 9)));
            }

            @Test
            void should_get_two_legal_moves_for_right_edge_piece() {
                List<Move> legalMoves = checkers.getLegalMoves(8);
                assertEquals(legalMoves.size(), 2);
                assertThat(legalMoves, hasItem(matchingMove(8, 11)));
                assertThat(legalMoves, hasItem(matchingMove(8, 12)));
            }
        }

        @Nested
        @DisplayName("when on an even row")
        class EvenRow {
            Checkers checkers;

            @BeforeEach
            void setUp() {
                int[] board = new int[]{1,0,0,1, 0,0,0,0, 0,0,0,0};
                checkers = new Checkers(board);
            }

            @Test
            void should_get_two_legal_moves_for_left_edge_piece() {
                List<Move> legalMoves = checkers.getLegalMoves(1);

                assertEquals(2, legalMoves.size());
                assertThat(legalMoves, hasItem(matchingMove(1, 5)));
                assertThat(legalMoves, hasItem(matchingMove(1, 6)));
            }

            @Test
            public void should_get_one_legal_move_for_right_edge_piece() {
                List<Move> legalMoves = checkers.getLegalMoves(4);

                assertEquals(1, legalMoves.size());
                assertThat(legalMoves, hasItem(matchingMove(4, 8)));
            }
        }
    }

    @Nested
    @DisplayName("getAllLegalMoves")
    class getAllLegalMoves {
        private Checkers checkers;

        @BeforeEach
        void setUp() {
            checkers = new Checkers();
            checkers.setup();
        }

        @Test
        void should_get_all_player_one_legal_moves() {
            List<Move> moves = checkers.getAllLegalMoves(1);

            assertEquals(moves.size(), 7);
            assertThat(moves, hasItem(matchingMove(9, 13)));
            assertThat(moves, hasItem(matchingMove(9, 14)));
            assertThat(moves, hasItem(matchingMove(10, 14)));
            assertThat(moves, hasItem(matchingMove(10, 15)));
            assertThat(moves, hasItem(matchingMove(11, 15)));
            assertThat(moves, hasItem(matchingMove(11, 16)));
            assertThat(moves, hasItem(matchingMove(12, 16)));
        }

        @Test
        void should_get_all_player_two_legal_moves() {
            List<Move> moves = checkers.getAllLegalMoves(2);

            assertEquals(moves.size(), 7);
            assertThat(moves, hasItem(matchingMove(21, 17)));
            assertThat(moves, hasItem(matchingMove(22, 17)));
            assertThat(moves, hasItem(matchingMove(22, 18)));
            assertThat(moves, hasItem(matchingMove(23, 18)));
            assertThat(moves, hasItem(matchingMove(23, 19)));
            assertThat(moves, hasItem(matchingMove(24, 19)));
            assertThat(moves, hasItem(matchingMove(24, 20)));
        }
    }



    private Matcher<Move> matchingMove(int origin, int target) {
        return new TypeSafeMatcher<Move>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("move");
            }

            public boolean matchesSafely(Move actual) {
                return actual.target() == target
                    && actual.origin() == origin;
            }

            public void describeMismatchSafely(Move move, Description mismatchDescription) {
                mismatchDescription.appendText("Expected [" + origin + "->" + target + "]"
                        + " but got [" + move.origin() + "->" + move.target() + "]");
            }
        };

    }

};
