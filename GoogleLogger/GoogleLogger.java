import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;

public class GoogleLogger {
    public void defaultLogging() throws InterruptedException, ExecutionException {
        final LogClient logClient = new LogClientImpl(10);
        List<CompletableFuture<Void>> tasks = new ArrayList<>();
        logClient.start("1", 1);
        logClient.start("2", 2);
        logClient.start("3", 3);
        logClient.end("3");
        logClient.end("2");
        tasks.add(runAsync(logClient::poll));
        tasks.add(runAsync(logClient::poll));
        logClient.end("1");
        tasks.add(runAsync(logClient::poll));
        allOf(tasks.toArray(CompletableFuture[]::new)).get();
    }

    public void concurrencyTest() throws ExecutionException, InterruptedException {
        final LogClient logClient = new LogClientImpl(10);
        final var size = 1000;
        final ExecutorService executorService = Executors.newFixedThreadPool(size);
        final Random random = new Random();
        final List<String> commands = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            commands.add("POLL");
            commands.add("END " + i);
        }
        Collections.shuffle(commands);
        Map<Integer, Integer> ends = new HashMap<>();
        for (int i = 0; i < size * 2; i++) {
            if (!commands.get(i).equals("POLL")) {
                ends.put(Integer.parseInt(commands.get(i).split(" ")[1]), i);
            }
        }
        int index = commands.size() - 1;
        while (index >= 0) {
            if (commands.get(index).startsWith("END ")) {
                final var taskId = Integer.parseInt(commands.get(index).split(" ")[1]);
                final var insertionPoint = random.nextInt(Math.min(ends.get(taskId), ends.getOrDefault(taskId + 1, commands.size() - 1)) + 1);
                commands.add(insertionPoint, "START " + taskId);
                if (insertionPoint <= index) {
                    index++;
                }
            }
            index--;
        }
        final List<CompletableFuture<Void>> tasks = new CopyOnWriteArrayList<>();
        for (final String command : commands) {
            if (command.equals("POLL")) {
                tasks.add(runAsync(logClient::poll, executorService));
            } else {
                final var id = command.split(" ")[1];
                if (command.startsWith("START ")) {
                    logClient.start(id, size - Long.parseLong(id) + 1);
                } else {
                    logClient.end(id);
                }
            }
        }
        allOf(tasks.toArray(CompletableFuture[]::new)).get();
        executorService.shutdown();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        GoogleLogger test = new GoogleLogger();
        test.defaultLogging();
        test.concurrencyTest();
    }
}

interface LogClient {
    /**
     * When a process starts, it calls 'start' with processId.
     */
    void start(String processId, long timestamp);

    /**
     * When the same process ends, it calls 'end' with processId.
     */
    void end(String processId);

    /**
     * Polls the first log entry of a completed process sorted by the start time of processes in the below format
     * {processId} started at {startTime} and ended at {endTime}
     * <p>
     * process id = 1 --> 12, 15
     * process id = 2 --> 8, 12
     * process id = 3 --> 7, 19
     * <p>
     * {3} started at {7} and ended at {19}
     * {2} started at {8} and ended at {12}
     * {1} started at {12} and ended at {15}
     */
    String poll();
}


class LogClientImpl implements LogClient, Closeable {
    PriorityBlockingQueue<Process> queue;
    ConcurrentHashMap<String, Process> processes;
    ExecutorService[] executors;

    LogClientImpl(int threads) {
        queue = new PriorityBlockingQueue<>(11, Comparator.comparingLong(Process::getStartTime));
        processes = new ConcurrentHashMap<>();
        executors = new ExecutorService[threads];

        for (int i = 0; i < executors.length; i++) {
            executors[i] = Executors.newSingleThreadExecutor();
        }
    }

    int getExecutorIdx(final String processId) {
        return processId.hashCode() % executors.length;
    }

    @Override
    public void start(final String processId, final long timestamp) {
        executors[getExecutorIdx(processId)].execute(() -> {
            Process newProcess = new Process(processId, timestamp);
            processes.put(processId, newProcess);
            queue.add(newProcess);
        });
    }

    @Override
    public void end(final String processId) {
        executors[getExecutorIdx(processId)].execute(() -> {
            long now = System.currentTimeMillis();
            Process process = processes.get(processId);
            process.setEndTime(now);
            processes.remove(processId);
        });
    }

    @Override
    public String poll() {
        try {
            Process process = queue.take();
            process
                    .waitForProcessEnd(1, TimeUnit.SECONDS);
            System.out.println(process);
            return process.toString();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        for (ExecutorService executor : executors) {
            executor.shutdown();
        }
    }
}

class Process {
    private final String processId;
    private final long startTime;
    private final CompletableFuture<Long> endTime;

    public Process(String processId, long startTime) {
        this.processId = processId;
        this.startTime = startTime;
        endTime = new CompletableFuture<>();
    }

    public long getStartTime() {
        return startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime.complete(endTime);
    }

    public void waitForProcessEnd(long timeOut, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        endTime.get(timeOut, timeUnit);
    }

    @Override
    public String toString() {
        long endTime = getOrDefault(this.endTime);
        return "Process{" +
               "processId='" + processId + '\'' +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               '}';
    }

    long getOrDefault(CompletableFuture<Long> endTime) {
        try {
            return endTime.get();
        } catch (InterruptedException | ExecutionException e) {
            return -1;
        }
    }
}
