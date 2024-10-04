import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class BrainFuck {
    private final Deque<Integer> startLoopPos = new ArrayDeque<>();
    private final ArrayList<Integer> memory = new ArrayList<>();
    private int ip = 0;
    private int dp = 0;
    private int currentValue = 0;

    void ensureCapacity() {
        while (memory.size() <= dp)
            memory.add(0);
    }

    void run(String program) throws IOException {
        while (ip >= 0 && ip < program.length()) {
            if (dp < 0) {
                throw new IllegalStateException("Data Pointer is negative");
            }

            final char command = program.charAt(ip);

            ensureCapacity();

            switch (command) {
                case '+':
                    memory.set(dp, memory.get(dp) + 1);
                    ip++;
                    break;
                case '-':
                    memory.set(dp, memory.get(dp) - 1);
                    ip++;
                    break;

                case '>':
                    dp++;
                    ip++;
                    break;
                case '<':
                    dp--;
                    ip++;
                    break;

                case '.':
                    System.out.print((char) memory.get(dp).intValue());
                    System.out.flush();
                    ip++;
                    break;               

                case ',':
                    this.memory.set(this.dp, System.in.read());
                    break;

                case '[':
                    currentValue = memory.get(dp);
                    if (currentValue == 0) {
                        int openingBracket = 1;
                        while (openingBracket != 0 && ip < program.length()) {
                            final char c = program.charAt(++ip);
                            if (c == '[')
                                openingBracket++;
                            else if (c == ']')
                                openingBracket--;
                        }
                        ip++;
                    } else {
                        startLoopPos.addLast(ip);
                        ip++;
                    }
                    break;

                case ']':
                    currentValue = memory.get(dp);
                    if (currentValue == 0) {
                        startLoopPos.removeLast();
                        ip++;
                    } else {
                        ip = startLoopPos.removeLast();
                    }
                    break;

                default:
                    throw new IllegalStateException("Unrecognized command: " + command);
            }

        }

    }

    record BF (String code, String output) {}
    public static void main(String[] args) throws IOException {
        List<BF> bfList = List.of(
            new BF("++++++++++[>+++++++<-]>.", "F"),
            new BF("++++++++++[>+>+++>+++++++>++++++++++<<<<-]>>>++.>+.+++++++..+++.<<++++++++++++++.------------.>+++++++++++++++.>.+++.------.--------.<<+.", "Hello World!")
        );

        for (var bf: bfList) {
            String program = bf.code();
            String expected = bf.output();
            BrainFuck brainFuck = new BrainFuck();
            System.out.print("Running: ");
            brainFuck.run(program);
            System.out.println("\nExpected: " + expected);
        }
    }
}