package net.covers1624.aoc2024;

import net.covers1624.quack.collection.FastStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.concurrent.TimeUnit;

//@Fork (0) // For debugger attachment.
@Fork (3)
@Threads (4)
@State (Scope.Benchmark)
@BenchmarkMode (Mode.AverageTime)
@Warmup (iterations = 3, time = 5)
@Measurement (iterations = 3, time = 5)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
public class Day2 extends Day {

    private final List<String> input = FastStream.of(loadLines("input.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();

    @Benchmark
    public void part1(Blackhole bh) {
        int result = solve(input, false);
        assertResult(result, 390);
        bh.consume(3);
    }

    @Benchmark
    public void part2(Blackhole bh) {
        int result = solve(input, true);
        assertResult(result, 439);
        bh.consume(3);
    }

    private int solve(List<String> reports, boolean part2) {
        int safe = 0;
        for (String report : reports) {
            Integer[] ints = FastStream.of(report.split(" "))
                    .map(Integer::parseInt)
                    .toArray(new Integer[0]);
            boolean up = ints[0] < ints[1];
            boolean borked = false;
            boolean ignored = false;
            for (int i = 1; i < ints.length; i++) {
                int diff = ints[i] - ints[i - 1];
                if (up && (diff < 1 || diff > 3) || !up && (diff > -1 || diff < -3)) {
                    if (part2 && !ignored) {
                        ignored = true;
                        continue;
                    }
                    borked = true;
                    break;
                }
            }
            if (!borked) {
//                System.out.println("Safe: " + report);
                safe++;
            } else {
//                System.out.println("Unsafe: " + report);
            }
        }
        return safe;
    }
}