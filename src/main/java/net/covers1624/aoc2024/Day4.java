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
public class Day4 extends Day {

    private final List<String> testInput = FastStream.of(loadLines("test.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();
    private final List<String> input = FastStream.of(loadLines("input.txt"))
            .map(String::trim)
            .filterNot(String::isEmpty)
            .toList();

    private final char[] CHARS = { 'X', 'M', 'A', 'S' };

    private final int[][] SEARCH_DIRECTIONS = {
            { 1, 0 },   // RIGHT
            { -1, 0 },  // LEFT
            { 0, 1 },   // DOWN
            { 0, -1 },  // UP
            { 1, 1 },   // DIAGONAL BOTTOM RIGHT
            { 1, -1 },  // DIAGONAL TOP RIGHT
            { -1, 1 },  // DIAGONAL BOTTOM LEFT
            { -1, -1 }, // DIAGONAL TOP LEFT
    };

    @Benchmark
    public void part1(Blackhole bh) {
        int result = solveP1(parse(input));
        assertResult(result, 2642);
        bh.consume(result);
    }

    @Benchmark
    public void part2(Blackhole bh) {
        int result = solveP2(parse(input));
        assertResult(result, 1974);
        bh.consume(result);
    }

    private int solveP1(Grid grid) {
        int occurrences = 0;
        for (int x = 0; x < grid.width; x++) {
            for (int y = 0; y < grid.height; y++) {
                for (int[] search : SEARCH_DIRECTIONS) {
                    if (matchP1(grid, x, y, search)) {
                        occurrences++;
                    }
                }
            }
        }
        return occurrences;
    }

    private boolean matchP1(Grid grid, int x, int y, int[] search) {
        for (int i = 0; i < CHARS.length; i++) {
            int aX = x + (search[0] * i);
            int aY = y + (search[1] * i);
            if (aX < 0 || aX >= grid.width) return false;
            if (aY < 0 || aY >= grid.height) return false;

            if (grid.data[aX][aY] != CHARS[i]) return false;
        }
        return true;
    }

    private int solveP2(Grid grid) {
        int occurrences = 0;
        for (int x = 1; x < grid.width - 1; x++) {
            for (int y = 1; y < grid.height - 1; y++) {
                if (matchP2(grid, x, y)) {
                    occurrences++;
                }
            }
        }
        return occurrences;
    }

    private boolean matchP2(Grid grid, int x, int y) {
        if (charMatchP2(grid, x, y, 'A') != 0) return false;

        int tl = charMatchP2(grid, x - 1, y - 1, 'M', 'S');
        int tr = charMatchP2(grid, x + 1, y - 1, 'M', 'S');
        if (tl == -1 || tr == -1) return false;

        if (charMatchP2(grid, x + 1, y + 1, tl == 0 ? 'S' : 'M') != 0) return false;
        if (charMatchP2(grid, x - 1, y + 1, tr == 0 ? 'S' : 'M') != 0) return false;

        return true;
    }

    private int charMatchP2(Grid grid, int x, int y, char... chars) {
        if (x < 0 || x >= grid.width) return -1;
        if (y < 0 || y >= grid.height) return -1;

        for (int i = 0; i < chars.length; i++) {
            if (grid.data[x][y] == chars[i]) {
                return i;
            }
        }
        return -1;
    }

    private Grid parse(List<String> lines) {
        int width = lines.size();
        int height = lines.getFirst().length();
        char[][] chars = new char[width][height];
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            line.getChars(0, line.length(), chars[i], 0);
        }
        return new Grid(width, height, chars);
    }

    private record Grid(
            int width,
            int height,
            char[][] data
    ) { }
}
