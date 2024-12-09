package net.covers1624.aoc2024;

import net.covers1624.quack.collection.ColUtils;
import net.covers1624.quack.collection.FastStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Arrays;
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
public class Day5 extends Day {

    private final List<String> testInput = FastStream.of(loadLines("test.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();

    private final List<String> input = FastStream.of(loadLines("input.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();

    @Benchmark
    public void part1(Blackhole bh) {
        int result = solveP1(parse(input));
        assertResult(result, 5391);
        bh.consume(result);
    }

    @Benchmark
    public void part2(Blackhole bh) {
        int result = solveP2(parse(input));
        assertResult(result, 6142);
        bh.consume(result);
    }

    private int solveP1(PageUpdates updates) {
        int added = 0;
        for (int[] sequence : updates.pageSequences) {
            if (ColUtils.allMatch(updates.rules, rule -> rule.isValid(sequence))) {
//                LOGGER.info("Sequence valid: {}", Arrays.toString(sequence));
                added += sequence[sequence.length / 2];
            } else {
//                LOGGER.info("Sequence invalid: {}", Arrays.toString(sequence));
            }
        }
        return added;
    }

    private int solveP2(PageUpdates updates) {
        int added = 0;
        for (int[] sequence : updates.pageSequences) {
            if (ColUtils.allMatch(updates.rules, rule -> rule.isValid(sequence))) continue;
            int[] seq = sequence.clone();

//            LOGGER.info("Seq: {}", Arrays.toString(seq));
            outer:
            while (true) {
                for (Rule rule : updates.rules) {
                    if (!rule.isValid(seq)) {
//                        LOGGER.info("  Rule {}|{} violated. Swapping.", rule.x, rule.y);
                        int xI = indexOf(seq, rule.x);
                        int yI = indexOf(seq, rule.y);
                        int num = seq[xI];
                        seq[xI] = seq[yI];
                        seq[yI] = num;
                        continue outer;
                    }
                }
                break;
            }
            added += seq[seq.length / 2];
//            LOGGER.info("Corrected sequence: {}", Arrays.toString(seq));
        }
        return added;
    }

    private PageUpdates parse(List<String> lines) {
        List<Rule> rules = new ArrayList<>();
        List<int[]> pageUpdates = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) continue;
            if (line.contains("|")) {
                String[] segs = line.split("\\|");
                rules.add(new Rule(Integer.parseInt(segs[0]), Integer.parseInt(segs[1])));
            } else if (line.contains(",")) {
                String[] segs = line.split(",");
                int[] pages = new int[segs.length];
                for (int i = 0; i < segs.length; i++) {
                    pages[i] = Integer.parseInt(segs[i]);
                }
                pageUpdates.add(pages);
            } else {
                throw new RuntimeException("Unknown input: " + line);
            }
        }
        return new PageUpdates(rules, pageUpdates);
    }

    private static int indexOf(int[] ints, int find) {
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] == find) {
                return i;
            }
        }
        return -1;
    }

    private record Rule(int x, int y) {

        public boolean isValid(int[] numbers) {
            int p = indexOf(numbers, x);
            int r = indexOf(numbers, y);
            return p == -1 || r == -1 || p < r;
        }
    }

    private record PageUpdates(List<Rule> rules, List<int[]> pageSequences) { }
}
