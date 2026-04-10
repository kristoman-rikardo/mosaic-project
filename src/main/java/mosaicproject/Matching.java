package mosaicproject;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Matching {
    private List<List<Integer>> cellColorList = new ArrayList<>(); 
    private HashMap<File, List<Integer>> tileColorMap = new HashMap<>();
    private List<File> mosaicList = new ArrayList<>();
    private boolean firstCompute = true;
    private int nCellsWide;
    private final double PENALTY_PREVIOUS = 0.7;
    private final double PENALTY_RANDOMNESS = 0.05; // in my short time i will not bother with vertical similarity so i just introduce some randomness to make it better. 
    private final double PENALTY_SECOND_RANDOMNESS = 0.7;
    private final double PENALTY_ABOVEDIAG = 0.7;
    

    public Matching(List<List<Integer>> cellColorList, HashMap<File, List<Integer>> tileColorMap, int nCellsWide) {
        System.out.println("Matching successfully called. CellColorList size: " + cellColorList.size() + ". TileColorMap size: " + tileColorMap.size() + ".");
        if (cellColorList == null || tileColorMap == null || cellColorList.size() == 0 || tileColorMap.size() == 0 || nCellsWide < 0) throw new IllegalArgumentException("Cannot match empty lists with one another");
        this.cellColorList = cellColorList;
        this.tileColorMap = tileColorMap;
        this.nCellsWide = nCellsWide;

    }

    public List<File> match() {
        System.out.println("Match algorithm successfully called.");
        for (List<Integer> cellColors : cellColorList) {
            if (cellColorList.indexOf(cellColors) == 0) System.out.println("Comparing the first cell colors.");
            int r_c = cellColors.get(0);
            int g_c = cellColors.get(1);
            int b_c = cellColors.get(2);
            double topScore = 0;
            File topFile = null;
            for (Map.Entry<File, List<Integer>> entry : tileColorMap.entrySet()) {
                int r_t = entry.getValue().get(0);
                int g_t = entry.getValue().get(1);
                int b_t = entry.getValue().get(2);
                double score = computeScore(r_c, r_t, g_c, g_t, b_c, b_t);
                if (mosaicList.size() > 1 && mosaicList.get(mosaicList.size() - 1).equals(entry.getKey())) {
                    score -= PENALTY_PREVIOUS + Math.random() * PENALTY_RANDOMNESS; // add a small penalty if this tile was the best result for the former cell. 
                }
                if (mosaicList.size() > 2 && mosaicList.get(mosaicList.size() - 2).equals(topFile)) {
                    score -= Math.random() * PENALTY_SECOND_RANDOMNESS; // add a small penalty if this tile was the best result for the second last cell. 
                }
                int i = mosaicList.size();  // current index being filled
                File leftNeighbor  = (i % nCellsWide != 0)  ? mosaicList.get(i - 1)          : null;
                File aboveNeighbor = (i >= nCellsWide)       ? mosaicList.get(i - nCellsWide)  : null;
                // Apply penalty to current candidate:
                if (entry.getKey().equals(leftNeighbor) || entry.getKey().equals(aboveNeighbor)) {
                    score -= PENALTY_ABOVEDIAG;  // subtract to discourage (score closer to 1 = better)
                }

                if (score > topScore) {
                    topFile = entry.getKey();
                    topScore = score;
                }
            }
            mosaicList.add(topFile);
        }
        System.out.println("Matching algorithm done!");
        return mosaicList;
    }


    public double computeScore(int r_c, int r_t, int g_c, int g_t, int b_c, int b_t) { // using the low-cost weighted distance algorithm from https://www.compuphase.com/cmetric.htm 
        if (firstCompute) {
            System.out.println("Computing the distance of the first pair.");
            firstCompute = false;
        }
        double r_mean = (r_c + r_t) / 2;
        double dr = r_c - r_t;
        double dg = g_c - g_t;
        double db = b_c - b_t;
        return 1 - ((Math.sqrt((2 + r_mean/256) * dr*dr + 4*dg*dg + (2 + (255 - r_mean)/256) * db*db)) / 441.67);
    }
}