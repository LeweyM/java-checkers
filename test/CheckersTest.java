import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CheckersTest {

    @Nested
    @DisplayName("From board Constructor")
    class FromBoardConstructor {

        @Test
        public void should_initialize_with_pieces_from_constructor() {
            int[] board = new int[]{3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            Checkers checkers = new Checkers(board);

            assertArrayEquals(checkers.stateSlice(), board);
        }
    }

    @Nested
    @DisplayName("Setup")
    class Setup {

        @Test
        public void setup_should_initialize_the_pieces() {
            Checkers checkers = new Checkers();
            checkers.setup();

            assertArrayEquals(checkers.stateSlice(), getAfterSetupState());
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
                int[] board = pad(new int[]{3, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0});
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
                assertThat(legalMoves, hasItem(matchingMove(8, 12)));
                assertThat(legalMoves, hasItem(matchingMove(8, 11)));
            }
        }

        @Nested
        @DisplayName("when on an even row")
        class EvenRow {
            Checkers checkers;

            @BeforeEach
            void setUp() {
                int[] board = new int[]{3, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
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

        @Nested
        @DisplayName("When takes are available")
        class Takes {
            private List<Move> legalMoves;

            @BeforeEach
            void setUp() {
                int[] b = buildState(
                        row(1, 1, 1, 1),
                        row(0, -1, 0, 0),
                        row(0, 0, 0, 0));
                Checkers checkers = new Checkers(b);
                legalMoves = checkers.getLegalMoves(1);
            }

            @Test
            void should_only_return_available_takes() {
                assertEquals(1, legalMoves.size());
                assertThat(legalMoves, hasItem(matchingMove(1, 10)));
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
            List<Move> moves = checkers.getAllLegalMoves();

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
            checkers.move(9, 13);

            List<Move> moves = checkers.getAllLegalMoves();

            assertEquals(moves.size(), 7);
            assertThat(moves, hasItem(matchingMove(21, 17)));
            assertThat(moves, hasItem(matchingMove(22, 17)));
            assertThat(moves, hasItem(matchingMove(22, 18)));
            assertThat(moves, hasItem(matchingMove(23, 18)));
            assertThat(moves, hasItem(matchingMove(23, 19)));
            assertThat(moves, hasItem(matchingMove(24, 19)));
            assertThat(moves, hasItem(matchingMove(24, 20)));
        }

        @Nested
        @DisplayName("When takes are available")
        class Takes {

            private List<Move> legalMoves;

            @BeforeEach
            void setUp() {
                int[] simpleBoard = {3, 1, 1, 1, 1, 0, -1, 0, 0, 0, 0, 0, 0};
                Checkers checkers = new Checkers(simpleBoard);
                legalMoves = checkers.getAllLegalMoves();
            }

            @Test
            void should_only_return_available_takes() {
                assertThat(legalMoves, hasItem(matchingMove(1, 10)));
                assertThat(legalMoves, hasItem(matchingMove(2, 9)));
                assertEquals(2, legalMoves.size());
            }

        }
    }

    @Nested
    @DisplayName("move")
    class move {

        private Checkers checkers;

        @BeforeEach
        void setUp() {
            checkers = new Checkers();
            checkers.setup();
        }

        @Test
        void should_throw_exception_for_invalid_move() {
            assertThrows(IllegalArgumentException.class, () -> checkers.move(1, 30));
        }

        @Test
        void should_throw_error_if_wrong_turn() {
            assertThrows(IllegalArgumentException.class, () -> checkers.move(21, 17));
        }

        @Test
        void should_move_player_one_piece() {
            checkers.move(9, 13);

            int[] expectedState = getAfterSetupState();
            swap(expectedState, 9, 13);

            assertArrayEquals(checkers.stateSlice(), expectedState);
        }

        @Test
        void should_move_player_two_piece() {
            checkers.move(9, 13);
            checkers.move(21, 17);

            int[] expectedState = getAfterSetupState();
            swap(expectedState, 9, 13);
            swap(expectedState, 21, 17);

            assertArrayEquals(checkers.stateSlice(), expectedState);
        }

        @Nested
        @DisplayName("taking pieces")
        class ForcedMoves {

            private Checkers checkers;

            @Nested
            @DisplayName("When only one forced move is available after a player moves")
            class ForcedAfterMove {

                @BeforeEach
                void setUp() {
                    int[] b = buildState(
                            row(1, 0, 0, 0),
                            row(0, 0, 0, 0),
                            row(0, -1, 0, 0)
                    );
                    checkers = new Checkers(b);
                    checkers.move(1, 6);
                }

                @Test
                void the_other_player_must_take() {
                    int[] expectedState = buildState(
                            row(-1, 0, 0, 0),
                            row(0, 0, 0, 0),
                            row(0, 0, 0, 0)
                    );

                    assertArrayEquals(expectedState, checkers.stateSlice());
                }

                @Test
                void should_go_back_to_other_players_turn() {
                    assertThrows(IllegalArgumentException.class, () -> {
                        checkers.move(12, 16);
                    });
                }

            }
        }
    }

    private int[] row(int... cells) {
        return cells;
    }

    private int[] buildState(int[]... rows) {
        ArrayList<int[]> rowList = new ArrayList<>(Arrays.asList(rows));
        rowList.add(0, new int[]{3});
        return pad(rowList.stream()
                .flatMapToInt(IntStream::of)
                .toArray());
    }

    private int[] pad(int[] board) {
        return Arrays.copyOf(board, 32);
    }

    private void swap(int[] state, int origin, int target) {
        int temp = state[origin];
        state[origin] = state[target];
        state[target] = temp;
    }

    private int[] getAfterSetupState() {
        return new int[]{3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
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

}
