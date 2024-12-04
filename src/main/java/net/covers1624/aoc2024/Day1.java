package net.covers1624.aoc2024;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;
import net.covers1624.quack.collection.FastStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

//@Fork (0) // For debugger attachment.
@Fork (3)
@Threads (4)
@State (Scope.Benchmark)
@BenchmarkMode (Mode.AverageTime)
@Warmup (iterations = 3, time = 20)
@Measurement (iterations = 5, time = 10)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
public class Day1 extends Day {

    private final List<String> testInput = FastStream.of(loadLines("test.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();
    private final List<String> input = FastStream.of(loadLines("input.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();

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
    public void part1Vector(Blackhole bh) {
        int result = solveP1Vector(parse(input));
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
        int sum = 0;
        for (int i = 0; i < numbers.left.length; i++) {
            sum += Math.abs(numbers.left[i] - numbers.right[i]);
        }
        return sum;
    }

    private static int solveP1Vector(Numbers numbers) {
        int sum = 0;
        int upperBound = IntVector.SPECIES_PREFERRED.loopBound(numbers.left.length);
        for (int i = 0; i < upperBound; i += IntVector.SPECIES_PREFERRED.length()) {
            IntVector left = IntVector.fromArray(IntVector.SPECIES_PREFERRED, numbers.left, i);
            IntVector right = IntVector.fromArray(IntVector.SPECIES_PREFERRED, numbers.right, i);
            sum += left.sub(right)
                    .abs()
                    .reduceLanes(VectorOperators.ADD);
        }
        return sum;
    }

    private static int solveP2(Numbers numbers) {
        Int2IntMap occurrenceMap = new Int2IntOpenHashMap();
        for (int i : numbers.right) {
            occurrenceMap.mergeInt(i, 1, Integer::sum);
        }
        int sum = 0;
        for (int i : numbers.left) {
            sum += i * occurrenceMap.get(i);
        }
        return sum;
    }

    private static Numbers parse(List<String> lines) {
        int[] left = new int[lines.size()];
        int[] right = new int[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            int firstSpace = line.indexOf(' ');
            int lastSpace = line.lastIndexOf(' ');

            left[i] = Integer.parseInt(line, 0, firstSpace, 10);
            right[i] = Integer.parseInt(line, lastSpace + 1, line.length(), 10);
        }
        Arrays.sort(left);
        Arrays.sort(right);
        return new Numbers(left, right);
    }

    private record Numbers(int[] left, int[] right) { }
}
