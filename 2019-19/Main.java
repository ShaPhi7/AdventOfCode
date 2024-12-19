import adventOfCode2019.IntCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    static List<Long> initialState = new ArrayList<>();
    public static void main(String[] args) {        
        try {
            String filePath = "input.txt";
            List<String> lines = Files.readAllLines(Paths.get(filePath));
            
            for (String line : lines) {
                for (String value : line.split(",")) {
                    initialState.add(Long.valueOf(value.trim()));
                }
            }

            //part 1
            int count = 0;
            boolean[][] grid = new boolean[50][50];
            int yMin = 0;
            int yMax = 50;
            for (int x = 0; x < 50; x++) {
                for (int y = yMin; y < yMax; y++) {
                    if (checkIntCode(initialState, x, y)) {
                        count++;
                        grid[x][y] = true;
                    }
                }
            }

            //pic
            System.out.println("Total number of 1s: " + count);
            System.out.println("Grid:");
            for (int y = 0; y < 50; y++) {
                for (int x = 0; x < 50; x++) {
                    System.out.print(grid[x][y] ? "#" : ".");
                }
                System.out.println();
            }

            //part two
            for (int y = 790; y < 1500; y++) {
                for (int x = 542; x < 1500; x++) {
                    boolean topLeft = checkIntCode(initialState, x, y);
                    if (topLeft) {
                        boolean topRight = checkIntCode(initialState, x+99, y);
                        boolean bottomLeft = checkIntCode(initialState, x, y+99);
                        System.out.println("x: " + x + " y: " + y + " topLeft: " + topLeft + ", topRight: " + topRight + ", bottomLeft: " + bottomLeft);
                        if (topRight
                          && bottomLeft) {
                                System.out.println("Found 1 at " + x + ", " + y); //979, 1328
                                return;
                            }
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    private static boolean checkIntCode(List<Long> initialState, int x, int y) {
        IntCode intCode = new IntCode(initialState);
        intCode.addToUserInput(x);
        intCode.addToUserInput(y);
        
        intCode.setDebugIntCode(false); // Set debug mode off
        
        long output = intCode.getOutputLastDiagnosticStyle();
        return output == 1l;
    }
}
