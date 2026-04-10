package mosaicproject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MosaicTest {

    // Helper to build a Matching with one cell and one tile
    private Matching makeMatching(List<List<Integer>> cells, HashMap<File, List<Integer>> tiles) {
        return new Matching(cells, tiles, 1);
    }

    // 1. Identical colors should yield a score of exactly 1.0
    @Test
    public void testComputeScore_identicalColors_returnsOne() {
        Matching m = makeMatching(
            List.of(List.of(100, 150, 200)),
            new HashMap<>(java.util.Map.of(new File("t.jpg"), List.of(100, 150, 200)))
        );
        assertEquals(1.0, m.computeScore(100, 100, 150, 150, 200, 200), 1e-9);
    }

    // 2. Black vs white (maximum contrast) should yield a score close to 0
    @Test
    public void testComputeScore_blackVsWhite_returnsNearZero() {
        Matching m = makeMatching(
            List.of(List.of(0, 0, 0)),
            new HashMap<>(java.util.Map.of(new File("t.jpg"), List.of(255, 255, 255)))
        );
        double score = m.computeScore(0, 255, 0, 255, 0, 255);
        assertTrue(score < 0.05, "Score for black vs white should be near 0, was: " + score);
    }

    // 3. Score should be symmetric: score(a, b) == score(b, a)
    @Test
    public void testComputeScore_isSymmetric() {
        Matching m = makeMatching(
            List.of(List.of(50, 100, 200)),
            new HashMap<>(java.util.Map.of(new File("t.jpg"), List.of(200, 100, 50)))
        );
        double ab = m.computeScore(50, 200, 100, 100, 200, 50);
        double ba = m.computeScore(200, 50, 100, 100, 50, 200);
        assertEquals(ab, ba, 1e-9);
    }

    // 4. Constructor should throw when cellColorList is null
    @Test
    public void testConstructor_throwsOnNullCellList() {
        HashMap<File, List<Integer>> tiles = new HashMap<>(
            java.util.Map.of(new File("t.jpg"), List.of(100, 100, 100))
        );
        assertThrows(IllegalArgumentException.class, () -> new Matching(null, tiles, 1));
    }

    // 5. Constructor should throw when tileColorMap is empty
    @Test
    public void testConstructor_throwsOnEmptyTileMap() {
        List<List<Integer>> cells = List.of(List.of(100, 100, 100));
        assertThrows(IllegalArgumentException.class, () -> new Matching(cells, new HashMap<>(), 1));
    }

    // 6. match() should return a list with the same number of entries as cells
    @Test
    public void testMatch_returnsCorrectSize() {
        List<List<Integer>> cells = new ArrayList<>();
        cells.add(List.of(255, 0, 0));
        cells.add(List.of(0, 255, 0));
        cells.add(List.of(0, 0, 255));

        HashMap<File, List<Integer>> tiles = new HashMap<>();
        tiles.put(new File("red.jpg"),   List.of(255, 0, 0));
        tiles.put(new File("green.jpg"), List.of(0, 255, 0));
        tiles.put(new File("blue.jpg"),  List.of(0, 0, 255));

        Matching m = new Matching(cells, tiles, 3);
        List<File> result = m.match();
        assertEquals(3, result.size());
    }
}
