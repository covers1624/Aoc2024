package net.covers1624.aoc2024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.TimeUnit;

//@Fork (0) // For debugger attachment.
@Fork (3)
@State (Scope.Benchmark)
@BenchmarkMode (Mode.AverageTime)
@Warmup (iterations = 3, time = 5)
@Measurement (iterations = 3, time = 5)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
public class Day1 extends Day {

    private final List<String> testInput = loadLines("test.txt");
    private final List<String> input = loadLines("input.txt");

//    @Benchmark
    public void part1TestInput(Blackhole bh) {
        int result = solveP1(parse(testInput));
        assertResult(result, 11);
        bh.consume(result);
    }

//    @Benchmark
    public void part2TestInput(Blackhole bh) {
        int result = solveP2(parse(testInput));
        assertResult(result, 31);
        bh.consume(result);
    }

    @Benchmark
    public void part1(Blackhole bh) {
        int result = solveP1(parse(input));
        assertResult(result, 1603498);
        bh.consume(result);
    }

    @Benchmark
    public void part2(Blackhole bh) {
        int result = solveP2(parse(input));
        assertResult(result, 25574739);
        bh.consume(result);
    }

    private static int solveP1(Numbers numbers) {
        List<Integer> left = new ArrayList<>(numbers.left);
        List<Integer> right = new ArrayList<>(numbers.right);
        int sum = 0;
        while (!left.isEmpty()) {
            Integer l = left.removeFirst();
            Integer r = right.removeFirst();
            sum += Math.abs(l - r);
        }
        return sum;
    }

    private static int solveP2(Numbers numbers) {
        Map<Integer, Integer> occuranceMap = new HashMap<>();
        for (Integer i : numbers.right) {
            occuranceMap.compute(i, (k, n) -> n != null ? n + 1 : 1);
        }
        int sum = 0;
        for (Integer i : numbers.left) {
            sum += i * occuranceMap.getOrDefault(i, 0);
        }
        return sum;
    }

    private static Numbers parse(List<String> lines) {
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            String[] segs = line.split(" ");

            left.add(Integer.parseInt(segs[0]));
            right.add(Integer.parseInt(segs[segs.length - 1]));
        }
        left.sort(Comparator.naturalOrder());
        right.sort(Comparator.naturalOrder());
        return new Numbers(left, right);
    }

    private record Numbers(List<Integer> left, List<Integer> right) { }
}
