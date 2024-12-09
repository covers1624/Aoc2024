package net.covers1624.aoc2024;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@Fork (0) // For debugger attachment.
@Fork (3)
@Threads (4)
@State (Scope.Benchmark)
@BenchmarkMode (Mode.AverageTime)
@Warmup (iterations = 3, time = 5)
@Measurement (iterations = 3, time = 5)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
public class Day3 extends Day {

    private final String input = load("input.txt");

    @Benchmark
    public void part1(Blackhole bh) {
        int result = solveP1(input);
        assertResult(result, 173785482);
        bh.consume(result);
    }

    @Benchmark
    public void part2(Blackhole bh) {
        int result = solveP2(input);
        assertResult(result, 83158140);
        bh.consume(result);
    }

    private int solveP1(String string) {
        int sum = 0;
        Pattern pattern = Pattern.compile("mul\\((\\d+),(\\d+)\\)");
        Matcher matcher = pattern.matcher(string);
        while (matcher.find()) {
            sum += (Integer.parseInt(matcher.group(1)) * Integer.parseInt(matcher.group(2)));
        }
        return sum;
    }

    private int solveP2(String string) {
        int sum = 0;
        Pattern pattern = Pattern.compile("(?>do\\(\\))|(?>don't\\(\\))|(?>mul\\((\\d+),(\\d+)\\))");
        Matcher matcher = pattern.matcher(string);
        boolean mulEnabled = true;
        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            if (fullMatch.equals("do()")) {
                mulEnabled = true;
            } else if (fullMatch.equals("don't()")) {
                mulEnabled = false;
            } else if (mulEnabled) {
                sum += (Integer.parseInt(matcher.group(1)) * Integer.parseInt(matcher.group(2)));
            }
        }
        return sum;
    }
}
