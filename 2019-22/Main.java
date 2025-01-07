import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    
    //part 2 credit goes to a lovely python solution, gist here: https://gist.github.com/Voltara/7d417c77bc2308be4c831f1aa5a5a48d
    private static List<String> lines;
    private static List<Integer> deck;
    private static Set<Long> prevIndexes = new HashSet<>();
    //private static long totalSize = 10L;
    //private static long index = 4L;
    private static long totalSize = 10007L;
    private static long index = 2019L;
    //private static long index = 5472L;
    //private static long totalSize = 119315717514047L;
    //private static long index = 2020L;
    private static long times = 101741582076661L;

    public static void main(String[] args) throws IOException {
    
        String filePath = "day22test.txt";
        lines = Files.readAllLines(Paths.get(filePath));

        // deck = new ArrayList<>();
        // for (int i=0; i<totalSize; i++)
        // {
        //     deck.add(i);
        // }
        
        // System.out.println(isPrime(totalSize)); //prime
        // System.out.println(isPrime(times)); //prime
        // System.out.println(gcd(totalSize, times)); //1 so coprime
    }

    private static void shuffleTrackingCard() {
        for (String line : lines) {
            if (line.startsWith("deal into new stack"))
            {
                index = totalSize - 1 - index;
            }
            else if (line.startsWith("cut"))
            {
                int n = Integer.parseInt(line.split(" ")[1]);
                index = ((index - n) % totalSize + totalSize) % totalSize;
            }
            else if (line.startsWith("deal with increment"))
            {
                int n = Integer.parseInt(line.split(" ")[3]);
                index = ((index * n) % totalSize + totalSize) % totalSize;
            }
        }
    }

    //Calculate all cards
    private static void shuffleTrackingAllCards() {
        for (String line : lines) {
            if (line.startsWith("deal into new stack"))
            {
                Collections.reverse(deck);
            }
            else if (line.startsWith("cut"))
            {
                int n = Integer.parseInt(line.split(" ")[1]);
                if (n > 0) {
                    List<Integer> cut = new ArrayList<>(deck.subList(0, n));
                    deck = new ArrayList<>(deck.subList(n, deck.size()));
                    deck.addAll(cut);
                } else {
                    n = deck.size() + n;
                    List<Integer> cut = new ArrayList<>(deck.subList(n, deck.size()));
                    deck = new ArrayList<>(deck.subList(0, n));
                    cut.addAll(deck);
                    deck = cut;
                }
            }
            else if (line.startsWith("deal with increment"))
            {
                int n = Integer.parseInt(line.split(" ")[3]);
                List<Integer> newDeck = new ArrayList<>(Collections.nCopies(deck.size(), 0));
                for (int i=0;i<deck.size();i++)
                {
                    newDeck.set((i*n) % deck.size(), deck.get(i));
                }
                deck = newDeck;
            }
        }
    }

    public static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static boolean isPrime(long n) {
        if (n <= 1) {
            return false;
        }
        if (n <= 3) {
            return true;
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (long i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
}