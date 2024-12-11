package net.covers1624.aoc2024;

import net.covers1624.quack.collection.FastStream;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//@Fork (0) // For debugger attachment.
@Fork (3)
@Threads (4)
@State (Scope.Benchmark)
@BenchmarkMode (Mode.AverageTime)
@Warmup (iterations = 3, time = 5)
@Measurement (iterations = 3, time = 5)
@OutputTimeUnit (TimeUnit.NANOSECONDS)
public class Day6 extends Day {

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
        assertResult(result, 4982);
        bh.consume(result);
    }

    @Benchmark
    public void part2(Blackhole bh) {
        int result = solveP2(parse(input));
        assertResult(result, 1663);
        bh.consume(result);
    }

    private int solveP1(Grid grid) {
        grid.reset();
        while (true) {
            grid.visited[grid.guardX][grid.guardY] = true;
            // Get guard next step pos.
            int gX = grid.guardX + grid.guardDir.xOff;
            int gY = grid.guardY + grid.guardDir.yOff;

            // Guard left the area.
            if (gX < 0 || gX >= grid.width || gY < 0 || gY >= grid.height) {
                break;
            }
            // Guard would be on top of an obstacle. Rotate.
            if (grid.obstacles[gX][gY]) {
                grid.guardDir = grid.guardDir.rotate();
                continue;
            }
            grid.visited[gX][gY] = true;
            grid.guardX = gX;
            grid.guardY = gY;
        }

        int visited = 0;
        for (boolean[] booleans : grid.visited) {
            for (boolean aBoolean : booleans) {
                if (aBoolean) {
                    visited++;
                }
            }
        }
        return visited;
    }

    private int solveP2(Grid grid) {
        int stuck = 0;
        int customRock = -1;
        boolean[][] legalPositions = new boolean[grid.width][grid.height];
        outer:
        while (true) {
            // Clear custom rog
            if (customRock != -1) {
                grid.obstacles[customRockX(customRock, grid)][customRockY(customRock, grid)] = false;
            } else {
                grid.reset();
                solveP1(grid);
                for (int i = 0; i < grid.visited.length; i++) {
                    legalPositions[i] = grid.visited[i].clone();
                }
            }

            // Find legal position for new rog
            do {
                customRock++;
                if (customRock >= grid.height * grid.width) {
                    break outer;
                }
            }
            while (!legalPositions[customRockX(customRock, grid)][customRockY(customRock, grid)]);

            grid.obstacles[customRockX(customRock, grid)][customRockY(customRock, grid)] = true;
            grid.reset();

            while (true) {
                // Get guard next step pos.
                int gX = grid.guardX + grid.guardDir.xOff;
                int gY = grid.guardY + grid.guardDir.yOff;

                // Guard left the area.
                if (gX < 0 || gX >= grid.width || gY < 0 || gY >= grid.height) {
                    break;
                }
                // Guard would be on top of an obstacle. Rotate.
                if (grid.obstacles[gX][gY]) {
                    if (grid.lineStartX != -1 && grid.lineStartY != -1) {
                        Line line = new Line(grid.lineStartX, grid.lineStartY, grid.guardX, grid.guardY);
                        if (!grid.traversedLines.add(line)) {
                            stuck++;
                            break;
                        }
                    }

                    grid.lineStartX = grid.guardX;
                    grid.lineStartY = grid.guardY;
                    grid.guardDir = grid.guardDir.rotate();
                    continue;
                }
                grid.guardX = gX;
                grid.guardY = gY;
            }
        }
        return stuck;
    }

    private static int customRockX(int idx, Grid grid) {
        return idx % grid.width;
    }

    private static int customRockY(int idx, Grid grid) {
        return idx / grid.width;
    }

    private static Grid parse(List<String> lines) {
        int width = lines.getFirst().length();
        int height = lines.size();
        Grid grid = new Grid(
                width,
                height,
                new boolean[width][height],
                new boolean[width][height]
        );
        for (int y = 0; y < height; y++) {
            char[] chars = lines.get(y).toCharArray();
            for (int x = 0; x < width; x++) {
                char ch = chars[x];
                if (ch == '#') {
                    grid.obstacles[x][y] = true;
                } else if (ch == '^') {
                    grid.guardStartX = x;
                    grid.guardStartY = y;
                } else if (ch != '.') {
                    throw new RuntimeException("Unknown char: " + ch);
                }
            }
        }
        return grid;
    }

    public static final class Grid {

        private final int width;
        private final int height;
        private final boolean[][] obstacles;
        private final boolean[][] visited;

        private int guardStartX;
        private int guardStartY;

        private int guardX;
        private int guardY;
        private Direction guardDir = Direction.NORTH;

        // Part 2
        private int lineStartX;
        private int lineStartY;

        private Set<Line> traversedLines = new HashSet<>();

        public Grid(int width, int height, boolean[][] obstacles, boolean[][] visited) {
            this.width = width;
            this.height = height;
            this.obstacles = obstacles;
            this.visited = visited;
        }

        public void reset() {
            for (boolean[] booleans : visited) {
                Arrays.fill(booleans, false);
            }

            guardX = guardStartX;
            guardY = guardStartY;
            guardDir = Direction.NORTH;

            lineStartX = -1;
            lineStartY = -1;
            traversedLines.clear();
        }
    }

    private record Line(
            int startX,
            int startY,
            int endX,
            int endY
    ) { }

    public enum Direction {
        NORTH(0, -1),
        EAST(1, 0),
        SOUTH(0, 1),
        WEST(-1, 0);

        public final int xOff;
        public final int yOff;

        Direction(int xOff, int yOff) {
            this.xOff = xOff;
            this.yOff = yOff;
        }

        public Direction rotate() {
            return switch (this) {
                case NORTH -> EAST;
                case EAST -> SOUTH;
                case SOUTH -> WEST;
                case WEST -> NORTH;
            };
        }
    }
}
