import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    static Map<Integer, String> fileMap = new HashMap<>();
    static Map<Character, Coordinate> keys = new HashMap<>();
    static Map<Character, Coordinate> doors = new HashMap<>();
    static Coordinate start;
    static Set<Coordinate> valids = new HashSet<>();
    static Map<Coordinate, Set<Coordinate>> validsToNeighbours = new HashMap<>();
    static Set<Coordinate> checked = new HashSet<>();
    static Set<Coordinate> availableKeys = new HashSet<>();
    static boolean doesAtSymbolBlock = false;
    static Set<Route> routes = new HashSet<>();
    static Map<Coordinate, Integer> exploredLocationsToDistance = new HashMap<>();
    static Map<Coordinate, Set<Character>> exploredLocationsToDoors = new HashMap<>();
    static Set<Coordinate> explored = new HashSet<>();
    static Coordinate currentOrigin = null;
    static HashSet<Journey> completedJourneys = new HashSet<>();
    static Journey shortestJourney = new Journey();
    static Journey longestJourney = new Journey();

    public static void main(String[] args) throws IOException {
        // Specify the path to your text file
        String filePath = "C:\\Users\\smphi\\Desktop\\eclipse-workspace\\adventOfCode\\2019-18\\eg4.txt";
        readInput(filePath);
        calculateShortestDistance(new Journey());
        System.out.println("The shortest journey is: " + shortestJourney);
    }
    
    private static void calculateShortestDistance(Journey journey) {
        if (shortestJourney.getTotalDistance() < journey.getTotalDistance()
          && shortestJourney.getTotalDistance() > 0)
        {
            return;
        }
        
        Set<Route> routesFromLocation = new HashSet<>(routes);
        routesFromLocation.removeIf(r -> !r.getStartAndEnd().contains(journey.getCurrentLocation()));

        Set<Route> possibleRoutes = new HashSet<>();

        for (Route route : routesFromLocation) {
            Set<Character> startAndEnd = route.getStartAndEnd();
            
            boolean doorsRequirement = true;
            for (Character key : route.getRequiredKeys())
            {
                if (!journey.getCollectedKeys().contains(Character.toLowerCase(key))
                 && doors.containsKey(key))
                 {
                    doorsRequirement = false;
                 }
            }
            
            for (Character character : startAndEnd) {
                if (!character.equals(journey.getCurrentLocation())
                  && !journey.getCollectedKeys().contains(character)
                  && doorsRequirement)
                {
                    possibleRoutes.add(route);
                }
            }
        }

        for (Route route : possibleRoutes) {
            Journey newJourney = new Journey(journey);
            //add to total
            newJourney.addCollectedKeys(route.getStartAndEnd());
            newJourney.addDistance(route.getDistance());
            Character newLocation = '@';
            for (Character routeEnd : route.getStartAndEnd()) {
                if (!routeEnd.equals(newJourney.getCurrentLocation()))
                {
                    newLocation = routeEnd;
                    newJourney.addRouteTaken(routeEnd);
                }
            } 
            newJourney.setCurrentLocation(newLocation);
            
            if (newJourney.isComplete(keys.size()+1)) //+1 for the @.
            {
                //completedJourneys.add(newJourney);
                if (newJourney.getTotalDistance() < shortestJourney.getTotalDistance()
                  || shortestJourney.getTotalDistance() == 0)
                {
                    shortestJourney = newJourney;
                    System.out.println("*New shortest journey* - " + newJourney);
                }
                else if (newJourney.getTotalDistance() > longestJourney.getTotalDistance())
                {
                    longestJourney = newJourney;
                    System.out.println("*New longest journey* - " + newJourney);
                }
                //System.out.println("Completed journey: " + newJourney);
            }
            else
            {
                calculateShortestDistance(newJourney);
            }
        }
    }
        
    private static void readInput(String filePath) throws IOException {
        buildFileMap(filePath);
        buildLocations();
        buildValids(start);
        buildValidsToNeighbours();
        currentOrigin = start;
        if (filePath.contains("eg"))
        {
            doesAtSymbolBlock = true;
        }
        buildRoutes(start, true);
        keys.values().forEach(c -> {
            explored.clear();
            exploredLocationsToDistance.clear();
            exploredLocationsToDoors.clear();
            currentOrigin = c;
            buildRoutes(c, doesAtSymbolBlock); 
        });
        sanityCheck();
    }

    private static void sanityCheck() {
        //System.out.println("Found " + routes.size() + " routes:");
        //routes.forEach(System.out::println);
        
        /*for (char c = 'a'; c <= 'z'; c++) {
            for (char d = 'a'; d <= 'z'; d++) {
                if (c == d)
                {
                    break;
                }
                Set<Character> compare = new HashSet<>();
                compare.add(c);
                compare.add(d);
                Set<Route> ro = routes.stream().filter(r -> r.getStartAndEnd().equals(compare)).collect(Collectors.toSet());
                
                if (ro.size() != 1)
                {
                    System.out.println(ro);
                    System.out.println(c + " and " + d + " are missing or duplicated!");
                }
            }
        }
        for (char c = 'a'; c <= 'z'; c++) {
                Set<Character> compare = new HashSet<>();
                compare.add(c);
                compare.add('@');
                Set<Route> ro = new HashSet<>(routes);
                ro.stream().filter(r -> r.getStartAndEnd().equals(compare));
                if (ro.isEmpty())
                {
                    System.out.println(c + " and @ are missing!");
                }
        }*/
    }

    private static void buildLocations() {
        for (char c = 'a'; c <= 'z'; c++) {
            if (findChar(c) != null) {
                keys.put(c, findChar(c));
            }
        }
        for (char c = 'A'; c <= 'Z'; c++) {
            if (findChar(c) != null) {
                doors.put(c, findChar(c));
            }
        }

        start = findChar('@');
    }

    public static void buildFileMap(String filePath) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineIndex = 0;

            while ((line = reader.readLine()) != null) {
                fileMap.put(lineIndex++, line);
            }
        }
    }

    static char getChar(Coordinate coordinate)
    {
        return getChar(coordinate.getLineIndex(), coordinate.getCharIndex());
    }

    public static char getChar(int lineIndex, int charIndex) {
        String line = fileMap.get(lineIndex);

        if (line == null) {
            return '#';
        }

        if (charIndex < 0 || charIndex >= line.length()) {
            return '#';
        }

        return line.charAt(charIndex);
    }

    static Coordinate findChar(char target) {
        for (Map.Entry<Integer, String> entry : fileMap.entrySet()) {
            int lineIndex = entry.getKey();
            String line = entry.getValue();

            int charIndex = line.indexOf(target);
            if (charIndex != -1) {
                return new Coordinate(lineIndex, charIndex);
            }
        }

        return null;
    }
    
    private static void buildValids(Coordinate coordinate) {
        while ((!valids.contains(coordinate)) && getChar(coordinate) != '#') {
            //getChar(coordinate);
            valids.add(coordinate);

            buildValids(coordinate.nextLine());
            buildValids(coordinate.prevLine());
            buildValids(coordinate.nextChar());
            buildValids(coordinate.prevChar());
        }
    }

    private static void buildValidsToNeighbours() {
        for (Coordinate valid : valids)
        {
            Set<Coordinate> neighbours = valid.getNeighbours();
            neighbours.removeIf(c -> getChar(c) == '#');
            validsToNeighbours.put(valid, neighbours);
        }
    }

    private static void buildRoutes(Coordinate startingPoint, boolean atAllowed) {

        explored.add(startingPoint);
        if (!doesAtSymbolBlock)
        {
            explored.add(findChar('@'));
        }
        
        if (getChar(startingPoint) == '#' || (!atAllowed && getChar(startingPoint) == '@')) {
            return;
        }

        //get coords 1 away
        Set<Coordinate> neighbours = startingPoint.getNeighbours();

        //remove the locations we've already checked.
        neighbours.removeIf(n -> explored.contains(n));

        //if any keys/doors, do stuff
        for (Coordinate n : neighbours) {

            int distance = exploredLocationsToDistance.getOrDefault(startingPoint, 0);
            exploredLocationsToDistance.put(n, ++distance);

            //sort doors out
            Set<Character> requiredDoors = new HashSet<>(exploredLocationsToDoors.getOrDefault(startingPoint, new HashSet()));
            if (Character.isUpperCase(getChar(n)))
            {
                requiredDoors.add(getChar(n));
            }
            exploredLocationsToDoors.put(n, requiredDoors);
            boolean addRoute = true;
            //sort keys out
            if (Character.isLowerCase(getChar(n)))
            {
                //we've got a key!
                Set<Character> startAndEnd = new HashSet<>();
                startAndEnd.add(getChar(currentOrigin));
                startAndEnd.add(getChar(n));
                Route route = new Route(exploredLocationsToDoors.get(n),
                                        exploredLocationsToDistance.get(n),
                                        startAndEnd);
                
                if (routes.contains(route))
                {
                    for (Route alreadyRecordedRoute : routes)
                    {
                        if (alreadyRecordedRoute.equals(route)
                          && alreadyRecordedRoute.getDistance() < route.getDistance())
                        {
                            addRoute = false;
                        }
                    }
                }

                if (addRoute == true)
                {
                    routes.add(route);
                }
            }
            buildRoutes(n, doesAtSymbolBlock);
        }
    }
}

class Coordinate {
    private int lineIndex;
    private int charIndex;

    // Constructor
    public Coordinate(int lineIndex, int charIndex) {
        this.lineIndex = lineIndex;
        this.charIndex = charIndex;
    }

    // Getters
    public int getLineIndex() {
        return lineIndex;
    }

    public int getCharIndex() {
        return charIndex;
    }

    // Setters
    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public void setCharIndex(int charIndex) {
        this.charIndex = charIndex;
    }

    public Coordinate nextLine() {
        return new Coordinate(lineIndex+1, charIndex);
    }

    public Coordinate prevLine() {
        return new Coordinate(lineIndex-1, charIndex);
    }

    public Coordinate nextChar() {
        return new Coordinate(lineIndex, charIndex+1);
    }

    public Coordinate prevChar() {
        return new Coordinate(lineIndex, charIndex-1);
    }

    public Set<Coordinate> getNeighbours() {
        Set neighbours = new HashSet<>();

        neighbours.add(this.nextLine());
        neighbours.add(this.prevLine());
        neighbours.add(this.nextChar());
        neighbours.add(this.prevChar());

        return neighbours;
    }

    // Method to calculate distance to another coordinate
    public double distanceTo(Coordinate other) {
        int dx = other.lineIndex - this.lineIndex;
        int dy = other.charIndex - this.charIndex;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Override toString for better readability
    @Override
    public String toString() {
        return "(" + lineIndex + ", " + charIndex + ")";
    }

    // Override equals for comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Coordinate that = (Coordinate) obj;
        return lineIndex == that.lineIndex && charIndex == that.charIndex;
    }

    // Override hashCode for hash-based collections
    @Override
    public int hashCode() {
        int result = Integer.hashCode(lineIndex);
        result = 31 * result + Integer.hashCode(charIndex);
        return result;
    }
}

class Route {
    Set<Character> requiredKeys = new HashSet<>();
    int distance = 0;
    Set<Character> startAndEnd = new HashSet<>();

    public Route(Set<Character> requiredKeys, int distance, Set<Character> startAndEnd) {
        this.requiredKeys = requiredKeys;
        this.distance = distance;
        this.startAndEnd = startAndEnd;
    }

    public Set<Character> getRequiredKeysLowerCase() {
        return requiredKeys.stream()
                           .map(k -> Character.toLowerCase(k))
                           .collect(Collectors.toSet());
    }

    public Set<Character> getRequiredKeys() {
        return requiredKeys;
    }
    public void setRequiredKeys(Set<Character> requiredKeys) {
        this.requiredKeys = requiredKeys;
    }
    public int getDistance() {
        return distance;
    }
    public void setDistance(int distance) {
        this.distance = distance;
    }
    public Set<Character> getStartAndEnd() {
        return startAndEnd;
    }
    public void setStartAndEnd(Set<Character> startAndEnd) {
        this.startAndEnd = startAndEnd;
    }
    @Override
    public String toString() {
        return "Route [startAndEnd=" + startAndEnd + ", requiredKeys=" + requiredKeys + ", distance=" + distance + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((requiredKeys == null) ? 0 : requiredKeys.hashCode());
        //result = prime * result + distance;
        result = prime * result + ((startAndEnd == null) ? 0 : startAndEnd.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Route other = (Route) obj;
        if (requiredKeys == null) {
            if (other.requiredKeys != null)
                return false;
        } else if (!requiredKeys.equals(other.requiredKeys))
            return false;
        // if (distance != other.distance)
        //     return false;
        if (startAndEnd == null) {
            if (other.startAndEnd != null)
                return false;
        } else if (!startAndEnd.equals(other.startAndEnd))
            return false;
        return true;
    }    
}

class Journey {
    Set<Character> collectedKeys = new HashSet<>();
    int totalDistance = 0;
    Character currentLocation = '@';
    List<Character> routeTaken = new ArrayList<>();

    Journey() {}

    Journey(Journey journey) {
        this.collectedKeys = new HashSet<>(journey.collectedKeys);
        this.totalDistance = journey.totalDistance;
        this.currentLocation = journey.currentLocation;
        this.routeTaken = new ArrayList<>(journey.routeTaken);
    }

    public Set<Character> getCollectedKeys() {
        return collectedKeys;
    }

    public void addCollectedKeys(Set<Character> collectedKeys) {
        this.collectedKeys.addAll(collectedKeys);
    }

    public void setCollectedKeys(Set<Character> collectedKeys) {
        this.collectedKeys = collectedKeys;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void addDistance(int distance) {
        this.totalDistance += distance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Character getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Character currentLocation) {
        this.currentLocation = currentLocation;
    }

    public void addRouteTaken(Character character) {
        this.routeTaken.add(character);
    }

    public boolean isComplete(int totalKeys) {
        return collectedKeys.size() == totalKeys;
    }

    @Override
    public String toString() {
        return "Journey [routeTaken=" + routeTaken + ", totalDistance=" + totalDistance + "]";
    }
}