import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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
    static Set<Route> uniqueRoutes = new HashSet<>();
    static Set<Route> routes = new TreeSet<>();
    static Map<Coordinate, Integer> exploredLocationsToDistance = new HashMap<>();
    static Map<Coordinate, Set<Character>> exploredLocationsToDoors = new HashMap<>();
    static Map<Coordinate, Set<Character>> exploredLocationsToKeys = new HashMap<>();
    static Set<Coordinate> explored = new HashSet<>();
    static Coordinate currentOrigin = null;
    static HashSet<Journey> completedJourneys = new HashSet<>();
    static Journey shortestJourney = new Journey();
    static Journey longestJourney = new Journey();
    static HashMap<Character, Character> mandatoryRoutes = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // Specify the path to your text file
        String filePath = "C:\\Users\\smphi\\Desktop\\eclipse-workspace\\adventOfCode\\2019-18\\inputquad.txt";
        readInput(filePath);

        //Set<Character> collectedKeys = new HashSet<>(Arrays.asList('@', 'v', 'n', 't', 's', 'f', 'z', 'e', 'm', 'q', 'u', 'j', 'h', 'k', 'r', 'i'));
        //List<Character> routeTaken = new ArrayList<>(Arrays.asList('v', 'n', 't', 's', 'f', 'z', 'e', 'm', 'q', 'u', 'j', 'h', 'k', 'r', 'i'));

        //calculateShortestDistance(new Journey(collectedKeys, 1350, 'i', routeTaken));
        calculateShortestDistance(new Journey());
        System.out.println("The shortest journey is: " + shortestJourney);
    }

    private static void calculateShortestDistance(Journey journey) {
        if (tooLong(journey))
        {
            return;
        }

        for (Route route : getPossibleRoutes(journey)) {
            
            Journey newJourney = updateJourney(journey, route);
            if (newJourney.isComplete(keys.size()+1)) //+1 for the @.
            //if (newJourney.isComplete(16))
            {
                logCompletedJourney(newJourney);
            }
            else
            {
                calculateShortestDistance(newJourney);
            }
        }
    }

    private static void printJourneyDetail(Journey journey) {
        List<Character> routeTaken = journey.getRouteTaken();
        System.out.println(routes.stream().filter(r -> r.fromOrTo('@', routeTaken.get(0))).findFirst());
        for (int i=0; i<routeTaken.size()-1; i++)
        {
            Character from = routeTaken.get(i);
            Character to = routeTaken.get(i+1);
            System.out.println(routes.stream().filter(r -> r.fromOrTo(from, to)).findFirst());
        }
    }

    private static Journey updateJourney(Journey journey, Route route) {
        Journey newJourney = new Journey(journey);
        
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
        return newJourney;
    }
    private static Set<Route> getPossibleRoutes(Journey journey) {
        Set<Route> possibleRoutes = new TreeSet<>();
        Character currentLocation = journey.getCurrentLocation();
        
        if (mandatoryRoutes.containsKey(currentLocation))
        {
            Route mandatory = new Route(currentLocation, mandatoryRoutes.get(currentLocation));
            Route mandatoryRoute = routes.stream().filter(r -> r.equals(mandatory)).findFirst().orElse(null);

            if (meetsDoorsRequirement(journey, mandatoryRoute)
              && !journey.getCollectedKeys().contains(mandatoryRoute.getOtherCharacterFromStartAndEnd(currentLocation)))
            {
                HashSet<Route> retSet = new HashSet<>();
                retSet.add(mandatoryRoute);
                return retSet;
            }
            else 
            {
                //this won't be the longest route so abandon it.
                return new HashSet<>();
            }
        }

        // Character autoPick = characterEnrouteToDestination.get(currentLocation);
        // if (autoPick != null)
        // {
        //     //get Route where characters match
        //     Route route = new Route(currentLocation, autoPick);
        //     return routes.stream().filter(r -> r.equals(route)).collect(Collectors.toSet());
        // }

        for (Route route : routes) {

            Set<Character> startAndEnd = route.getStartAndEnd();
            for (Character character : startAndEnd) {

                if (!character.equals(currentLocation)
                  && currentLocation.equals(route.getOtherCharacterFromStartAndEnd(character))
                  && !journey.getCollectedKeys().contains(character)
                  && meetsDoorsRequirement(journey, route))
                {
                    possibleRoutes.add(route);
                }
            }
        }
        return possibleRoutes;
    }
    private static boolean meetsDoorsRequirement(Journey journey, Route route) {
        boolean doorsRequirement = true;
        for (Character key : route.getRequiredKeys())
        {
            if (!journey.getCollectedKeys().contains(Character.toLowerCase(key))
             && doors.containsKey(key))
             {
                doorsRequirement = false;
             }
        }
        return doorsRequirement;
    }
    private static boolean tooLong(Journey journey) {
        return shortestJourney.getTotalDistance() < journey.getTotalDistance()
          && shortestJourney.getTotalDistance() > 0;
    }
    private static void logCompletedJourney(Journey newJourney) {
        //completedJourneys.add(newJourney);
        if (newJourney.getTotalDistance() < shortestJourney.getTotalDistance()
          || shortestJourney.getTotalDistance() == 0)
        {
            shortestJourney = newJourney;
            System.out.println("*New shortest journey* - " + newJourney);
            printJourneyDetail(newJourney);
        }
        else if (newJourney.getTotalDistance() > longestJourney.getTotalDistance())
        {
            longestJourney = newJourney;
            System.out.println("*New longest journey* - " + newJourney);
        }
        //System.out.println("Completed journey: " + newJourney);
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
        buildRoutes(start, true, 0);
        keys.values().forEach(c -> {
            explored.clear();
            exploredLocationsToDistance.clear();
            exploredLocationsToDoors.clear();
            exploredLocationsToKeys.clear();
            currentOrigin = c;
            buildRoutes(c, doesAtSymbolBlock, 0); 
        });
        buildMandatoryRoutes();
        routes = new TreeSet<>(uniqueRoutes);
        sanityCheck();
    }

    

    private static void sanityCheck() {
        System.out.println("Found " + routes.size() + " routes:");
        routes.forEach(System.out::println);
        
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
        
        for (char c = 'a'; c <= 'z'; c++) {
                Set<Character> compare = new HashSet<>();
                compare.add(c);
                compare.add('@');
                Set<Route> ro = routes.stream().filter(r -> r.getStartAndEnd().equals(compare)).collect(Collectors.toSet());
                if (!ro.isEmpty())
                {
                    System.out.println(ro);
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
//TODO - can only be on the @ or next to the @ twice. Test for inputeg, then hopefully works!
    private static void buildRoutes(Coordinate currentLocation, boolean atAllowed, int atCount) {

        explored.add(currentLocation);
        if (!doesAtSymbolBlock)
        {
            explored.add(findChar('@'));
            if (getChar(currentLocation) == '@' 
              || anyNeighboursAreAtSymbol(currentLocation))
            {
                atCount++;
                if (atCount > 2)
                {
                    return; //about to loop back on ourselves
                }
            }
        }
        
        if (getChar(currentLocation) == '#' || (!atAllowed && getChar(currentLocation) == '@')) {
            return;
        }

        //get coords 1 away
        Set<Coordinate> neighbours = currentLocation.getNeighbours();

        //remove the locations we've already checked.
        neighbours.removeIf(n -> explored.contains(n));

        //if any keys/doors, do stuff
        for (Coordinate n : neighbours) {

            int distance = exploredLocationsToDistance.getOrDefault(currentLocation, 0);
            exploredLocationsToDistance.put(n, ++distance);

            //sort doors out
            Set<Character> requiredDoors = new HashSet<>(exploredLocationsToDoors.getOrDefault(currentLocation, new HashSet<>()));
            if (Character.isUpperCase(getChar(n)))
            {
                requiredDoors.add(getChar(n));
            }
            exploredLocationsToDoors.put(n, requiredDoors);

            Set<Character> passedKeys = new HashSet<>(exploredLocationsToKeys.getOrDefault(currentLocation, new HashSet<>()));
            exploredLocationsToKeys.put(n, passedKeys);

            boolean addRoute = true;
            //sort keys out
            if (Character.isLowerCase(getChar(n)))
            {
                //we've got a key!
                Set<Character> startAndEnd = new HashSet<>();
                startAndEnd.add(getChar(currentOrigin));
                startAndEnd.add(getChar(n));

                Route route = new Route(exploredLocationsToDoors.get(n),
                                        exploredLocationsToKeys.getOrDefault(n, new HashSet<>()),
                                        exploredLocationsToDistance.get(n),
                                        startAndEnd);
                if (uniqueRoutes.contains(route))
                {
                    for (Route alreadyRecordedRoute : uniqueRoutes)
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
                    uniqueRoutes.add(route);
                }

                Set<Character> updatePassedKeys = new HashSet<>(exploredLocationsToKeys.getOrDefault(currentLocation, new HashSet<>()));
                if (Character.isLowerCase(getChar(n)))
                {
                    updatePassedKeys.add(getChar(n));
                }
                exploredLocationsToKeys.put(n, updatePassedKeys);
            }

            buildRoutes(n, doesAtSymbolBlock, atCount);
        }
    }

    private static void buildMandatoryRoutes() {
        mandatoryRoutes.put('q', 'j');
        //mandatoryRoutes.put('j', 'h'); ? interrupts o
        mandatoryRoutes.put('h', 'k');
        mandatoryRoutes.put('k', 'r');
        mandatoryRoutes.put('r', 'i');
        mandatoryRoutes.put('l', 'p');
        mandatoryRoutes.put('g', 'd');
        mandatoryRoutes.put('a', 'y');
        mandatoryRoutes.put('c', 'b');
        mandatoryRoutes.put('z', 'e');
        mandatoryRoutes.put('e', 'w');
    }

    private static boolean anyNeighboursAreAtSymbol(Coordinate currentLocation) {
        return currentLocation.getNeighbours().stream().anyMatch(c -> c.equals(findChar('@')));
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

class Route implements Comparable<Route> {
    Set<Character> requiredKeys = new HashSet<>();
    Set<Character> passedKeys = new HashSet<>();
    int distance = 0;
    Set<Character> startAndEnd = new HashSet<>();

    public Route(Set<Character> requiredKeys, Set<Character> passedKeys, int distance, Set<Character> startAndEnd) {
        this.requiredKeys = requiredKeys;
        this.passedKeys = passedKeys;
        this.distance = distance;
        this.startAndEnd = startAndEnd;
    }

    public Route(Character from, Character to) {
        HashSet<Character> sae = new HashSet<>();
        sae.add(from);
        sae.add(to);
        this.startAndEnd = sae;
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
    public Set<Character> getPassedKeys() {
        return passedKeys;
    }
    public void setPassedKeys(Set<Character> passedKeys) {
        this.passedKeys = passedKeys;
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

    public Character getOtherCharacterFromStartAndEnd(Character character) {
        for (Character c : startAndEnd)
        {
            if (!c.equals(character))
            {
                return c;
            }
        }

        return null;
    }

    public boolean fromOrTo(Character character) {
        return startAndEnd.contains(character);
    }

    public boolean fromOrTo(Character from, Character to) {
        return fromOrTo(from) && fromOrTo(to);
    }

    @Override
    public int compareTo(Route other) {
        // Compare by distance first
        int distanceComparison = Integer.compare(this.distance, other.distance);
        if (distanceComparison != 0) {
            return distanceComparison;
        }
    
        // If distances are equal, compare by startAndEnd lexicographically
        Iterator<Character> thisIterator = this.startAndEnd.iterator();
        Iterator<Character> otherIterator = other.startAndEnd.iterator();
    
        while (thisIterator.hasNext() && otherIterator.hasNext()) {
            int charComparison = Character.compare(thisIterator.next(), otherIterator.next());
            if (charComparison != 0) {
                return charComparison;
            }
        }
    
        // If sets are of different sizes, compare by size (smaller set first)
        return Integer.compare(this.startAndEnd.size(), other.startAndEnd.size());
    }

    @Override
    public String toString() {
        return "Route [startAndEnd=" + startAndEnd + ", distance=" + distance + ", requiredKeys=" + requiredKeys + ", passedKeys=" + passedKeys + "]";
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        //result = prime * result + ((requiredKeys == null) ? 0 : requiredKeys.hashCode());
        //result = prime * result + ((requiredKeys == null) ? 0 : passedKeys.hashCode());
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
        // if (requiredKeys == null) {
        //     if (other.requiredKeys != null)
        //         return false;
        // } else if (!requiredKeys.equals(other.requiredKeys))
        //     return false;
        // if (passedKeys == null) {
        //     if (other.passedKeys != null)
        //         return false;
        // } else if (!passedKeys.equals(other.passedKeys))
        //     return false;
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

    public Journey(Set<Character> collectedKeys, int totalDistance, Character currentLocation, List<Character> routeTaken) {
        this.collectedKeys = new HashSet<>(collectedKeys);
        this.totalDistance = totalDistance;
        this.currentLocation = currentLocation;
        this.routeTaken = new ArrayList<>(routeTaken);
    }

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

    public List<Character> getRouteTaken() {
        return routeTaken;
    }
}