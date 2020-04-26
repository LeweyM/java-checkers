import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Console {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Checkers checkers = new Checkers();

        put("Enter any key to start...");
        getInput(br);
        checkers.setup();

        while (true) {
            put(checkers.prettyString());
            List<Move> allLegalMoves = checkers.getAllLegalMoves();
            allLegalMoves.forEach(m -> put(m.origin() + " " + m.target()));
            put("Enter a move in format of two numbers: i.e. 4 8");
            String input = getInput(br);
            try {
                int[] command = parseInput(input);
                checkers.move(command[0], command[1]);
            } catch (IllegalArgumentException e) {
                put(e.getMessage());
            }
        }
    }

    private static int[] parseInput(String input) {
        int[] pair = new int[2];
        String[] split = input.split(" ");
        int from = Integer.parseInt(split[0]);
        int to = Integer.parseInt(split[1]);
        pair[0] = from;
        pair[1] = to;
        return pair;
    }

    private static void put(String msg) {
        System.out.println(msg);
    }

    private static String getInput(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}
