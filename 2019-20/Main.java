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
import java.util.stream.Collectors;

public class Main {

    static Map<Integer, String> fileMap = new HashMap<>();
    static Set<Coordinate> dots = new HashSet<>();
    static Set<Portal> portals = new HashSet<>();
    static Coordinate startingPoint = null;
    static Coordinate endPoint = null;
    static HashMap<Coordinate, Integer> exploredDistance = new HashMap<>();
    static HashSet<Coordinate> toExplore = new HashSet<>();
            
    public static void main(String[] args) throws IOException {
        // Specify the path to your text file
        String filePath = "";
        readInput(filePath);
    }
        
    private static void readInput(String filePath) throws IOException {
        buildFileMap(filePath);
        buildPortals();
        toExplore.add(startingPoint);
        exploredDistance.put(startingPoint, 0);
        explore();
        //printMap();
        System.out.println(exploredDistance.get(endPoint));
        for (Portal portal : portals) {
            System.out.println(portal);
        }
    }

    public static void explore() {
        while (!exploredDistance.keySet().contains(endPoint)) {
            Set<Coordinate> nextToExplore = new HashSet<>();
            
            for (Coordinate point : toExplore)
            {
                int distance = exploredDistance.get(point) + 1;

                point.getNeighbours().stream()
                                        .filter(c -> !exploredDistance.keySet().contains(c))
                                        .filter(c -> getChar(c) == '.')
                                        .forEach(c -> {
                                            exploredDistance.put(c, distance);
                                            nextToExplore.add(c);
                                        });
            }

            System.out.println(nextToExplore);
            toExplore = new HashSet<>(nextToExplore);
            // System.out.println(exploredDistance);
        }
    }

    // private static void printMap() {
    //     for (Map.Entry<Integer, String> entry : fileMap.entrySet()) {
    //         int lineIndex = entry.getKey();
    //         String line = entry.getValue();

    //         for (int i = 0; i < line.length(); i++) {
    //             Coordinate point = new Coordinate(lineIndex, i);
    //             if (exploredDistance.keySet().contains(point)) {
    //                 System.out.print('O');
    //             }
    //             else {
    //                 System.out.print(line.charAt(i));
    //             }
    //         }
    //         System.out.println();
    //     }
    // }

    public static void buildFileMap(String filePath) throws IOException {

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineIndex = 0;

            while ((line = reader.readLine()) != null) {
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (c == '.') {
                        dots.add(new Coordinate(lineIndex, i, 0));
                    }
                }
                fileMap.put(lineIndex++, line);
            }
        }
    }

    private static void buildPortals()
    {
        for (Coordinate dot : dots) {
            Set<Coordinate> neighbours = dot.getNeighbours().stream()
                                                              .filter(c -> Character.isUpperCase(getChar(c)))
                                                              .collect(Collectors.toSet());

            if (neighbours.size() == 1)
            {
                updatePortals(dot, neighbours);
            }
        }
    }

    private static void updatePortals(Coordinate dot, Set<Coordinate> neighbours) {
        
        Character portalEntrance = neighbours.stream()
                                             .map(c -> getChar(c))
                                             .findFirst()
                                             .get();

        Character portalExit = neighbours.iterator()
                                         .next()
                                         .getNeighbours()
                                         .stream()
                                         .map(c -> getChar(c))
                                         .filter(Character::isUpperCase)
                                         .findFirst().get();
                                         

        if (portalEntrance == 'A' && portalExit == 'A') {
            startingPoint = dot;
            return;
        }
        if (portalEntrance == 'Z' && portalExit == 'Z') {
            endPoint = dot;
            return;
        }

        HashSet<Character> label = new HashSet<>();
        label.add(portalEntrance);
        label.add(portalExit);

        boolean portalExists = false;
        for (Portal existingPortal : portals) {
            if (existingPortal.getLabel().equals(label)) {
                existingPortal.addCoordinate(dot);
                portalExists = true;
                break;
            }
        }

        if (!portalExists) {
            Portal newPortal = new Portal();
            newPortal.setLabel(label);
            newPortal.addCoordinate(dot);
            portals.add(newPortal);
        }
    }

    public static char getChar(Coordinate coordinate)
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

    public static Coordinate findChar(char target) {
        for (Map.Entry<Integer, String> entry : fileMap.entrySet()) {
            int lineIndex = entry.getKey();
            String line = entry.getValue();
    
            int charIndex = line.indexOf(target);
            if (charIndex != -1) {
                return new Coordinate(lineIndex, charIndex, 0);
            }
        }
    
        return null;
    }

    public static Coordinate mirrorAvailableFrom(Coordinate point) {
        Coordinate mirror = null;
        Portal portalToTake = null;
        for (Portal portal : portals) {
            for (Coordinate pc : portal.getCoordinates()) {
                if (pc.getLineIndex() == point.getLineIndex()
                 && pc.getCharIndex() == point.getCharIndex()) {
                    portalToTake = portal;
                    break;
                }
            }
        }

        if (portalToTake != null)
        {
            Set<Coordinate> mirrorSet = portalToTake.getCoordinates().stream()
            .filter(c -> c.getLineIndex() != point.getLineIndex() && c.getCharIndex() != point.getCharIndex())
            .filter(c -> getChar(c) == '.') //sanity check
            .collect(Collectors.toSet());

            mirror = mirrorSet.stream().findFirst().orElse(null);
            if (mirror != null)
            {
                mirror.setZ(point.getZ());

                if (isInner(point)) {
                    mirror.sink();
                }
                else {
                    mirror.rise();
                }

                if (mirror.getZ() < 0
                  || exploredDistance.keySet().contains(mirror)) {
                    mirror = null;
                }
                else {
                    System.out.println("Taking portal " + portalToTake.getLabel() + " from " + point + " to " + mirror);
                }
            }
        }

        return mirror;
    }

    public static boolean isInner(Coordinate dot) {
        boolean inner = false;
        if (dot.getLineIndex() > 3
        && dot.getLineIndex() < fileMap.size() - 3
        && dot.getCharIndex() > 3
        && dot.getCharIndex() < fileMap.get(0).length() - 3) {
            inner = true;
        }

        return inner;
    }
}

class Portal {
    
    Set<Coordinate> coordinates = new HashSet<>();
    Set<Character> label = new HashSet<>();

    public Set<Coordinate> getCoordinates() {
        return coordinates;
    }
    public void setCoordinates(Set<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }
    public void addCoordinate(Coordinate coordinates) {
        this.coordinates.add(coordinates);
    }
    public Set<Character> getLabel() {
        return label;
    }
    public void setLabel(Set<Character> label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "Portal" + label + " [coordinates=" + coordinates + "]";
    }
}

class Coordinate {
    private int lineIndex;
    private int charIndex;
    private int z;

    // Constructor
    public Coordinate(int lineIndex, int charIndex, int z) {
        this.lineIndex = lineIndex;
        this.charIndex = charIndex;
        this.z = z;
    }

    // Getters
    public int getLineIndex() {
        return lineIndex;
    }

    public int getCharIndex() {
        return charIndex;
    }

    public int getZ() {
        return z;
    }

    // Setters
    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public void setCharIndex(int charIndex) {
        this.charIndex = charIndex;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Coordinate nextLine() {
        return new Coordinate(lineIndex+1, charIndex, z);
    }

    public Coordinate prevLine() {
        return new Coordinate(lineIndex-1, charIndex, z);
    }

    public Coordinate nextChar() {
        return new Coordinate(lineIndex, charIndex+1, z);
    }

    public Coordinate prevChar() {
        return new Coordinate(lineIndex, charIndex-1, z);
    }

    public void sink() {
        z++;
    }

    public void rise() {
        z--;
    }

    public Set<Coordinate> getNeighbours() {
        
        Coordinate mirror = Main.mirrorAvailableFrom(this);
        
        if (mirror != null) {
            return Set.of(mirror);
        }

        Set<Coordinate> neighbours = new HashSet<>();

        neighbours.add(this.nextLine());
        neighbours.add(this.prevLine());
        neighbours.add(this.nextChar());
        neighbours.add(this.prevChar());

        return neighbours;
    }

    // Override toString for better readability
    @Override
    public String toString() {
        return "(" + lineIndex + ", " + charIndex + ", " + z + ")";
    }

    // Override equals for comparison
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Coordinate that = (Coordinate) obj;
        return lineIndex == that.lineIndex && charIndex == that.charIndex && z == that.z;
    }

    // Override hashCode for hash-based collections
    @Override
    public int hashCode() {
        int result = Integer.hashCode(lineIndex);
        result = 31 * result + Integer.hashCode(charIndex);
        result = 31 * result + Integer.hashCode(z);
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