import adventOfCode2019.IntCode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    
    static List<IntCodeComputer> computers = new ArrayList<>();
    static List<List<Long>> packets = new ArrayList<>();
    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            List<Long> initialState = readInitialState();
            IntCode intCode = new IntCode(initialState);
            intCode.addToUserInput(i);
            intCode.setDebugIntCode(false);
            System.out.println("i: " + i + ", result: " + intCode.getDiagnosticOutput(false));
            IntCodeComputer computer = new IntCodeComputer(i, intCode);
            computers.add(computer);
        }

        boolean done = false;
        while(done == false)
        {
            for (IntCodeComputer computer : computers) {
                int ip = computer.getIp();
                List<List<Long>> listOfPackets = packets.stream().filter(p -> p.get(0) == computer.getIp()).collect(Collectors.toList());
                
                IntCode intCode = computer.getIntCode();
                if (!listOfPackets.isEmpty()) {
                    List<Long> packet = listOfPackets.get(0);
                    intCode.addToUserInput(packet.get(1));
                    intCode.addToUserInput(packet.get(2));
                    packets.remove(packet);

                } else {
                    intCode.addToUserInput(-1);
                }
                List<Long> outputsSinceLastInput = intCode.getOutputsSinceLastInput();
                // if (outputsSinceLastInput.size() != 3)
                // {
                //     System.out.println("Size was " + outputsSinceLastInput.size() + ", ip: " + ip + " output: " + outputsSinceLastInput);
                // }
                if (!outputsSinceLastInput.isEmpty())
                {
                    if (outputsSinceLastInput.get(0) == 255)
                    {
                        done = true;
                        System.out.println(outputsSinceLastInput.get(2));
                    }
                    
                    List<List<Long>> listsOfPackets = IntStream.range(0, (outputsSinceLastInput.size() + 2) / 3)
                        .mapToObj(i -> outputsSinceLastInput.subList(3 * i, Math.min(3 * (i + 1), outputsSinceLastInput.size())))
                        .collect(Collectors.toList());
                    packets.addAll(listsOfPackets);    
                }
            }
        }
    }

    private static List<Long> readInitialState() {
        List<Long> initialState = new ArrayList<>();
        String filePath = "day23.txt";
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (Exception e) {
        }
        
        for (String line : lines) {
            for (String value : line.split(",")) {
                initialState.add(Long.valueOf(value.trim()));
            }
        }
        return initialState;
    }
}

class IntCodeComputer {
    private int ip;
    private IntCode intCode;

    public IntCodeComputer(int ip, IntCode intCode) {
        this.ip = ip;
        this.intCode = intCode;
    }

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public IntCode getIntCode() {
        return intCode;
    }

    public void setIntCode(IntCode intCode) {
        this.intCode = intCode;
    }
}
