import adventOfCode2019.IntCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<Long> initialState = readInitialState();

        IntCode intCode = new IntCode(initialState);
        intCode.setDebugIntCode(false);
        
        //programPartOne(intCode);
        inputAsAsciiValues(intCode, "NOT A J");

        inputAsAsciiValues(intCode, "NOT B T");
        inputAsAsciiValues(intCode, "AND C T");
        inputAsAsciiValues(intCode, "AND D T");
        inputAsAsciiValues(intCode, "OR T J");
        
        inputAsAsciiValues(intCode, "NOT C T");
        inputAsAsciiValues(intCode, "AND D T");
        inputAsAsciiValues(intCode, "AND H T");
        inputAsAsciiValues(intCode, "OR T J");

        inputAsAsciiValues(intCode, "RUN");

        List<Long> output = intCode.getDiagnosticOutput(false);
        System.out.println(convertAsciiToText(output));
    }

    private static List<Long> readInitialState() throws IOException {
        List<Long> initialState = new ArrayList<>();
        String filePath = "day21.txt";
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        
        for (String line : lines) {
            for (String value : line.split(",")) {
                initialState.add(Long.valueOf(value.trim()));
            }
        }
        return initialState;
    }

    private static String convertAsciiToText(List<Long> asciiValues) {
        StringBuilder text = new StringBuilder();
        for (Long value : asciiValues) {
            if (value > 255) {
                text.append(value);
            }
            else {
                text.append((char) value.longValue());
            }
        }
        return text.toString();
    }

    private static void inputAsAsciiValues(IntCode intCode, String input) {
        //input = input.replace(" ", "");
        for (char c : input.toCharArray()) {
            intCode.addToUserInput((long) c);
        }
        intCode.addToUserInput(10L);
    }

    public static void programPartOne(IntCode intCode) {
        inputAsAsciiValues(intCode, "NOT A J");

        inputAsAsciiValues(intCode, "NOT B T");
        inputAsAsciiValues(intCode, "AND A T");
        inputAsAsciiValues(intCode, "AND C T");
        inputAsAsciiValues(intCode, "OR T J");

        inputAsAsciiValues(intCode, "NOT B T");
        inputAsAsciiValues(intCode, "NOT C T");
        inputAsAsciiValues(intCode, "AND D T");
        inputAsAsciiValues(intCode, "OR T J");

        inputAsAsciiValues(intCode, "WALK");
    }
}
