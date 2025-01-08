import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    static List<String> lines = new ArrayList<>();
    static List<List<String>> map = new ArrayList<>();
    static List<List<List<String>>> prevMaps = new ArrayList<>();
    static List<List<String>> duplicate = new ArrayList<>();
    

    public static void main(String[] args) throws IOException {
        String filePath = "day24.txt";
        lines = Files.readAllLines(Paths.get(filePath));

        map = lines.stream()
            .map(line -> line.chars()
                .mapToObj(ch -> String.valueOf((char) ch))
                .collect(Collectors.toList()))
            .collect(Collectors.toList());

        while (duplicate.isEmpty())
        {
            prevMaps.add(map);
            //printMap();
            progressTime();
            checkForDuplicate();
        }

        printMap();
        System.out.println(countUp());
    }

    private static void printMap() {
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                System.out.print(map.get(y).get(x));
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void progressTime() {
        List<List<String>> newMap = new ArrayList<>();
        for (int y=0; y<5; y++) {
            List<String> newRow = new ArrayList<>();
            for (int x=0; x<5; x++) {
                int adjBugs = getAdjacentBugs(x, y);
                String value = map.get(y).get(x);
                
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
                newRow.add(value);
            }
            newMap.add(newRow);
        }
        map = newMap;
    }

    private static int getAdjacentBugs(int x, int y) {
        int count = 0;

        if (x > 0)
        {
            String left = map.get(y).get(x-1);
            if (left.equals("#"))
            {
                count++;
            }
        }
        if (y > 0)
        {
            String above = map.get(y-1).get(x);
            if (above.equals("#"))
            {
                count++;
            }
        }
        if (x < 4)
        {
            String right = map.get(y).get(x+1);
            if (right.equals("#"))
            {
                count++;
            }
        }
        if (y < 4)
        {
            String below = map.get(y+1).get(x);
            if (below.equals("#"))
            {
                count++;
            }
        }

        return count;
    }

    private static void checkForDuplicate() {
        for (List<List<String>> prevMap : prevMaps) {
            if (compareMaps(prevMap))
            {
                duplicate = map;
                return;
            }
        }
    }

    private static boolean compareMaps(List<List<String>> prevMap) {
        for (int y=0; y<5; y++) {
            for (int x=0; x<5; x++) {
                if (!map.get(y).get(x).equals(prevMap.get(y).get(x)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static long countUp() {
        long total = 0;
        int power = 0;
        
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                if (map.get(y).get(x).equals("#")) {
                    total += Math.pow(2, power);
                }
                power++;
            }
        }
        return total;
    }
}
