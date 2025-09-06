import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Supplier;

sealed interface TailCall<T> {
    static <T> TailCall<T> done(T value) {
        return new Done<>(value);
    }

    static <T> TailCall<T> call(Supplier<TailCall<T>> supplier) {
        return new Call<>(supplier);
    }

    record Done<T>(T value) implements TailCall<T> {
    }

    record Call<T>(Supplier<TailCall<T>> supplier) implements TailCall<T> {
    }

    default T eval() {
        TailCall<T> current = this;
        while (current instanceof Call<T>(Supplier<TailCall<T>> supplier)) {
            current = supplier.get();
        }

        return ((Done<T>) current).value();
    }
}

sealed interface IO<T> {
    record Pure<T>(T value) implements IO<T> {
    }

    record FlatMap<S, T>(IO<S> previous, Function<S, IO<T>> f) implements IO<T> {
    }

    record Suspend<T>(Supplier<T> action) implements IO<T> {
    }

    static <U> IO<U> pure(U value) {
        return new Pure<>(value);
    }

    static <U> IO<U> suspend(Supplier<U> action) {
        return new Suspend<>(action);
    }

    default <U> IO<U> flatMap(Function<T, IO<U>> f) {
        return new FlatMap<>(this, f);
    }

    default <U> IO<U> map(Function<T, U> f) {
        return pure(f.apply(this.run()));
    }

    default T run() {
        return runToTailCall().eval();
    }

    default TailCall<T> runToTailCall() {
        return switch (this) {
            case IO.Pure<T>(T value) -> TailCall.done(value);
            case IO.FlatMap<?, T> flatMap -> TailCall.call(() -> flattenFlatMap(flatMap).runToTailCall());
            case IO.Suspend<T>(Supplier<T> action) -> TailCall.done(action.get());
        };
    }

    private static <S, T> IO<T> flattenFlatMap(FlatMap<S, T> flatMap) {
        return flatMap.f().apply(flatMap.previous().run());
    }
}

public class IOMonad {
    public static void flatMapIsStackSafe() {
        IO<Integer> io = IO.pure(0);

        for (int i = 0; i < 100000; i++) {
            io = io.flatMap(v -> IO.pure(v + 1));
        }

        System.out.println(io.run() == 100000);
    }

    // Example usage
    public static void main(String[] args) {
        flatMapIsStackSafe();
        IO<Void> program = readLine()
                .map(String::toUpperCase)
                .flatMap(input -> printLine("You entered: " + input));

        program.run(); // Runs the effectful computation
    }

    static IO<String> readLine() {
        return IO.suspend(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Enter text: ");
                return scanner.nextLine();
            }
        });
    }

    static IO<Void> printLine(String message) {
        return IO.suspend(() -> {
            System.out.println(message);
            return null;
        });
    }
}
