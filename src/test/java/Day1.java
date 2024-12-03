import org.junit.jupiter.api.Test;

import java.util.*;

public class Day1 extends Day {

    @Test
    public void run() {
        Numbers testNumbers = parse(loadLines("test.txt"));
        LOGGER.info(solveP1(testNumbers));
        LOGGER.info(solveP2(testNumbers));

        Numbers inputNumbers = parse(loadLines("input.txt"));
        LOGGER.info(solveP1(inputNumbers));
        LOGGER.info(solveP2(inputNumbers));
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
