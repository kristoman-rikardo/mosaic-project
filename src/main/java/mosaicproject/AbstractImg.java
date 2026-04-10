package mosaicproject;

import java.util.List;
import java.awt.Color;
import java.awt.image.BufferedImage;

abstract class AbstractImg implements Scorable {
    protected BufferedImage image;
    
    @Override
    public List<Integer> getColor() {
        int r = 0; 
        int g = 0; 
        int b = 0; 
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Color color = new Color(image.getRGB(x, y)); // for every pixel we get the color
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();
            }
        }
        int imgSize = getSize();
        return List.of(r/imgSize, g/imgSize, b/imgSize); // add the average color to the list
    }

    public int getWidth() {
        return this.image.getWidth();
    } 

     public int getHeight() {
        return this.image.getHeight();
    } 

    public int getSize() {
        return getHeight() * getWidth();
    }
}
