package mosaicproject;

import java.util.List;
import java.io.File;
import java.util.HashMap;

public class TileImgHandling {
    private HashMap<File, List<Integer>> tileColorMap = new HashMap<>();

    // idk how to read loads of files from the GUI, TODO. 
    public TileImgHandling(List<File> tileList, int minResolution) {
        System.out.println("Tile handling sucessfully called.");
        if (tileList == null || tileList.size() < minResolution) throw new IllegalArgumentException("List of tiles not long enough");
        for (File file : tileList) {
            if (tileList.indexOf(file) == 0) System.out.println("Assesing the uploaded tiles...");
            scoreTile(file);
        }
        System.out.println("Tiles assesed and uploaded.");
    }

    // suppose we have a list of tiles-files, lets work from there

    public void scoreTile(File file) { // creates a hashmap with K = File and V = list of RGB colors. 
        try {
            TileImg tile = new TileImg(file);
            List<Integer> tileColors = tile.getColor();
            tileColorMap.put(file, tileColors);
        }    
        catch (Exception e) {
            System.out.println("Could not load (Error: " + e.getMessage() + "): " + file.getName());
        }
    }

    public HashMap<File, List<Integer>> getTileColorMap() {
        return this.tileColorMap;
    }

    public static void main(String[] args) {
    String path = "/Users/kristofferswik/Documents/Programmering - VS/oop/mosaic-project/";
    List<File> tiles = List.of(
        new File(path + "cat.jpg"),
        new File(path + "city.png"),
        new File(path + "skull.jpg"),
        new File(path + "whale.jpg")
    );
    TileImgHandling handling = new TileImgHandling(tiles, 1);
    System.out.println(handling.getTileColorMap());
}

}

// Crop all images to square. 