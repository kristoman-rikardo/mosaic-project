package mosaicproject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class MasterImgHandling {
    private BufferedImage masterImg; 
    private List<List<Integer>> cellColorList = new ArrayList<>();

    private int minResolution;
    private int nCellsTall;
    private int nCellsWide;
    private int cellSize;

    public MasterImgHandling(File file, int minResolution) {
        System.out.println("Constructor invoked.");
        try {
            this.masterImg = ImageIO.read(file);
            System.out.println("Reading from file...");
            if (this.masterImg != null && (minResolution > 0 && minResolution <= 2048)) {
                System.out.println("Successfully read from file!");
                this.minResolution = minResolution;
                squaresGrid();
            }
            else {throw new IllegalStateException("Something with the reading or minresolution is off...");}
        }
        catch(Exception e) {
            System.out.println("" + e.getMessage());
        }
    }

    public int getMinResolution() {
        return this.minResolution;
    }

    public List<List<Integer>> getCellColorList() {
        return this.cellColorList;
    }

    public int getCellSize() {
        return this.cellSize;
    }

    public void squaresGrid() {
        System.out.println("Successfully called the grid method!");
        int width = this.masterImg.getWidth();
        int height = this.masterImg.getHeight();
        this.cellSize = Math.min(width, height) / this.minResolution;
        System.out.println("Master sizes: Height: " + height + " px. Width: " + width + " px. Cell size: " + cellSize + "px (square).");
        if (width == height) {
            System.out.println("The master image is square.");
            this.nCellsWide = minResolution;
            this.nCellsTall = minResolution;
        }
        else if (width > height) {
            System.out.println("The master image is landscape.");
            this.nCellsWide = width / cellSize;
            this.nCellsTall = minResolution;
        }
        else {
            System.out.println("The master image is portrait.");
            this.nCellsWide = minResolution;
            this.nCellsTall = height / cellSize; 
        }
        // walk around the picture always adding the subimage. Only going to cellSize * nCellsLimitingSide, which implies dropping a fraction of the last row/column of the non-limiting side. 
        int startY = 0;
        for (int y = 0; y < nCellsTall; y++) { // taking the full row then jumping one cell down and full row
            int startX = 0; 
            for (int x = 0; x < nCellsWide; x++) { // all in first row first, jumping one cell at a time
                scoreCell(startX, startY, this.cellSize);
                startX += this.cellSize; 
                if (x == 1) System.out.println("We made it to row " + y);
            }
            startY += this.cellSize;
        }
    }

    public int getnCellsTall() {
        return this.nCellsTall;
    }

    public int getnCellsWide() {
        return this.nCellsWide;
    }

    public void scoreCell(int startX, int startY, int cellSize) { // scores the cell and adds the color to the list, as long as its ordered theres no need for reference to actual cell image
        BufferedImage image = this.masterImg.getSubimage(startX, startY, cellSize, cellSize);
        MasterCellImg cell = new MasterCellImg(image);
        cellColorList.add(cell.getColor());
        image = null; // empties buffer
    } 

    public static void main(String[] args) {
        MasterImgHandling control = new MasterImgHandling(new File("/Users/kristofferswik/Documents/Programmering - VS/oop/mosaic-project/skull.jpg"), 4);
        System.out.println(control.getCellSize());
        System.out.println(control.getMinResolution());
        System.out.println(control.getCellColorList());
    }
}


// All cells in the grid has got to be square
// colorlist does only need to be in order and immutable. 

