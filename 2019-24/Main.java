import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    static List<String> lines = new ArrayList<>();
    //static List<List<String>> map = new ArrayList<>();
    static List<List<List<String>>> levels = new ArrayList<>();
    //static List<List<List<String>>> prevMaps = new ArrayList<>();
    //static List<List<String>> duplicate = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        String filePath = "day24.txt";
        lines = Files.readAllLines(Paths.get(filePath));

        List<List<String>> startingMap = lines.stream()
                .map(line -> line.chars()
                        .mapToObj(ch -> String.valueOf((char) ch))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());
        levels.add(startingMap);
        //part 1
        // while (duplicate.isEmpty())
        // {
        //     prevMaps.add(map);
        //     //printMap();
        //     progressTime();
        //     checkForDuplicate();
        // }

        for (int t=0; t<200; t++)
        {
            padLevels();
            //printMap();
            progressTime();
        }

        //printMap();
        //System.out.println(countUpBinary());
        System.out.println(countUpHashes());

    }

    private static boolean isEmptyMap(List<List<String>> obj) {
        return compareMaps(obj, emptyMap());
    }

    private static List<List<String>> emptyMap() {
        List<List<String>> emptyMap = new ArrayList<>();
        for (int y = 0; y < 5; y++) {
            List<String> line = new ArrayList<>();
            for (int x = 0; x < 5; x++) {
                line.add(".");
            }
            emptyMap.add(line);
        }

        emptyMap.get(2).set(2, "?");
        return emptyMap;
    }

    private static void printMap() {
        for (int z = 0; z < levels.size(); z++) {
            System.out.println("level " + z + ":");
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    System.out.print(levels.get(z).get(y).get(x));
                }
                System.out.println();
            }
            System.out.println();
        }
        System.out.println("______________");
    }

    private static void progressTime() {
        List<List<List<String>>> newLevels = new ArrayList<>();
        for (int z=0; z<levels.size(); z++) {
            List<List<String>> newMap = new ArrayList<>();
            for (int y=0; y<5; y++) {
                List<String> newRow = new ArrayList<>();
                for (int x=0; x<5; x++) {
                    int adjBugs = getAdjacentBugs(x, y, z);
                    String value = levels.get(z).get(y).get(x);
                    
                    value = switchValueIfNeeded(adjBugs, value);
                    newRow.add(value);
                }
                newMap.add(newRow);
            }                
            newLevels.add(newMap);
        }
        levels = newLevels;
    }

    private static String switchValueIfNeeded(int adjBugs, String value) {
        if (value.equals("#"))
        {
            if (adjBugs != 1)
            {
                value = ".";
            }
        }
        else if (value.equals("."))
        {
            if (adjBugs == 1 || adjBugs == 2)
            {
                value = "#";
            }
        }

        return value;
    }

    private static int getAdjacentBugs(int x, int y, int z) {
        int count = 0;

        if (z > 0
          && z < levels.size()-1)
        {
            count += countAdjacentBugs(Direction.UP, x, y, z);
            count += countAdjacentBugs(Direction.DOWN, x, y, z);
            count += countAdjacentBugs(Direction.LEFT, x, y, z);
            count += countAdjacentBugs(Direction.RIGHT, x, y, z);
        }

        return count;
    }

    private static int countAdjacentBugs(Direction direction, int x, int y, int z) {
        int count = 0;
        String adjValue = "";
        switch (direction) {
            case UP -> {
                if (y == 0)
                {
                    adjValue = levels.get(z-1).get(1).get(2);
                }
                else
                {
                    adjValue = levels.get(z).get(y-1).get(x);
                }
            }
            case DOWN -> {
                if (y == 4)
                {
                    adjValue = levels.get(z-1).get(3).get(2);
                }
                else
                {
                    adjValue = levels.get(z).get(y+1).get(x);
                }
            }
            case LEFT -> {
                if (x == 0)
                {
                    adjValue = levels.get(z-1).get(2).get(1);
                }
                else
                {
                    adjValue = levels.get(z).get(y).get(x-1);
                }
            }
            case RIGHT -> {
                if (x == 4)
                {
                    adjValue = levels.get(z-1).get(2).get(3);
                }
                else
                {
                    adjValue = levels.get(z).get(y).get(x+1);
                }
            }
            default -> throw new AssertionError();
        }

        if (adjValue.equals("#"))
        {
            count++;
        }
        else if (adjValue.equals("?"))
        {
            switch (direction) {
                case UP -> {
                    count = countAllBugsForRow(4, z+1);
                }
                case DOWN -> {
                    count = countAllBugsForRow(0, z+1);
                }
                case LEFT -> {
                    count = countAllBugsForColumn(4, z+1);
                }
                case RIGHT -> {
                    count = countAllBugsForColumn(0, z+1);
                }
                default -> throw new AssertionError();
            }
        }

        return count;
    }

    private static int countAllBugsForRow(int y, int z) {
        return (int) levels.get(z).get(y).stream().filter(v -> v.equals("#")).count();
    }

    private static int countAllBugsForColumn(int x, int z) {
        return (int) levels.get(z).stream().map(c -> c.get(x)).filter(v -> v.equals("#")).count();
    }

    private static enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }

    private static int countUpHashes() {
        int count = 0;
        for (List<List<String>> z : levels) {
            for (List<String> y : z) {
                for (String x : y) {
                    if (x.equals("#")) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static boolean compareMaps(List<List<String>> map, List<List<String>> otherMap) {
        for (int y=0; y<5; y++) {
            for (int x=0; x<5; x++) {
                if (!map.get(y).get(x).equals(otherMap.get(y).get(x)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    //Part 1 below here

    // private static void progressTimePart1() {
    //     List<List<String>> newMap = new ArrayList<>();
    //     for (int y=0; y<5; y++) {
    //         List<String> newRow = new ArrayList<>();
    //         for (int x=0; x<5; x++) {
    //             int adjBugs = getAdjacentBugs(x, y);
    //             String value = map.get(y).get(x);
                
    //             value = switchValueIfNeeded(adjBugs, value);
    //             newRow.add(value);
    //         }
    //         newMap.add(newRow);
    //     }
    //     map = newMap;
    // }

    // private static void checkForDuplicate() {
    //     for (List<List<String>> prevMap : prevMaps) {
    //         if (compareMaps(prevMap))
    //         {
    //             duplicate = map;
    //             return;
    //         }
    //     }
    // }

    // private static long countUpBinary() {
    //     long total = 0;
    //     int power = 0;
        
    //     for (int y = 0; y < 5; y++) {
    //         for (int x = 0; x < 5; x++) {
    //             if (map.get(y).get(x).equals("#")) {
    //                 total += Math.pow(2, power);
    //             }
    //             power++;
    //         }
    //     }
    //     return total;
    // }

    private static void padLevels() {
        if (!isEmptyMap(levels.get(0)))
        {
            levels.add(0, emptyMap());
        }
        if (!isEmptyMap(levels.get(1)))
        {
            levels.add(0, emptyMap());
        }

        if (!isEmptyMap(levels.get(levels.size()-1)))
        {
            levels.add(emptyMap());
        }
        if (!isEmptyMap(levels.get(levels.size()-2)))
        {
            levels.add(levels.size()-1, emptyMap());
        }
    }
}
