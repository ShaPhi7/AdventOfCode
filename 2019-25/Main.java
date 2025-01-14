import adventOfCode2019.IntCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    
    static String filePath = "day25.txt";
    public static void main(String[] args) {
        List<Long> initialState = readInitialState();
        IntCode droid = new IntCode(initialState);

        getAllItemsAndGoToSecurityGate(droid);
        tryAllSecurityGateCombos(droid);

        while (true) { 
            List<Long> output = droid.getOutputsSinceLastInput();
            String text = convertAsciiToText(output);
            System.out.println(text);
            acceptCommand(droid);
        }
    }

    private static void tryAllSecurityGateCombos(IntCode droid) {
        List<String> items = new ArrayList<>();
        items.add("coin");
        items.add("wreath");
        items.add("dehydrated water");
        items.add("astrolabe");
        items.add("mutex");
        items.add("asterisk");
        items.add("monolith");
        items.add("astronaut ice cream");
        
        List<List<String>> allCombinations = getAllCombinations(items);

        for (List<String> itemsToTake : allCombinations)
        {
            items.forEach(i -> inputAsAsciiValues(droid, "drop " + i));
            itemsToTake.forEach(i -> inputAsAsciiValues(droid, "take " + i));
            inputAsAsciiValues(droid, "north");
            List<Long> output = droid.getOutputsSinceLastInput();
            String text = convertAsciiToText(output);
            if (!text.contains("lighter")
            && !text.contains("heavier"))
            {
                System.out.println(itemsToTake);
                System.out.println(text);
                break;
            }
            System.out.println(text);
        }
    }

    private static void getAllItemsAndGoToSecurityGate(IntCode droid) {
        inputAsAsciiValues(droid, "west");
        inputAsAsciiValues(droid, "take coin");
        inputAsAsciiValues(droid, "north");
        inputAsAsciiValues(droid, "east");
        inputAsAsciiValues(droid, "take astronaut ice cream");
        inputAsAsciiValues(droid, "west");
        inputAsAsciiValues(droid, "south");
        inputAsAsciiValues(droid, "east");
        inputAsAsciiValues(droid, "south");
        inputAsAsciiValues(droid, "take monolith");
        inputAsAsciiValues(droid, "east");
        inputAsAsciiValues(droid, "take asterisk");
        inputAsAsciiValues(droid, "west");
        inputAsAsciiValues(droid, "north");
        inputAsAsciiValues(droid, "north");
        inputAsAsciiValues(droid, "north");
        inputAsAsciiValues(droid, "take mutex");
        inputAsAsciiValues(droid, "west");
        inputAsAsciiValues(droid, "take astrolabe");
        inputAsAsciiValues(droid, "west");
        inputAsAsciiValues(droid, "take dehydrated water");
        inputAsAsciiValues(droid, "west");
        inputAsAsciiValues(droid, "take wreath");
        inputAsAsciiValues(droid, "east");
        inputAsAsciiValues(droid, "south");
        inputAsAsciiValues(droid, "east");
        inputAsAsciiValues(droid, "north");
    }

    private static List<List<String>> getAllCombinations(List<String> items) {
        List<List<String>> result = new ArrayList<>();
        for (int i = 0; i <= items.size(); i++) {
            generateCombinations(items, i, 0, new ArrayList<>(), result);
        }
        return result;
    }
    
    private static void generateCombinations(List<String> items, int size, int start, 
                                           List<String> current, List<List<String>> result) {
        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i < items.size(); i++) {
            current.add(items.get(i));
            generateCombinations(items, size, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static void acceptCommand(IntCode intCode) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter command: ");
        String command = scanner.nextLine();
        inputAsAsciiValues(intCode, command);
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

    private static List<Long> readInitialState() {
        List<Long> initialState = new ArrayList<>();
        
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
        }
        
        for (String line : lines) {
            for (String value : line.split(",")) {
                initialState.add(Long.valueOf(value.trim()));
            }
        }
        return initialState;
    }
}
