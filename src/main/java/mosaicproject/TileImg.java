package mosaicproject;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class TileImg extends AbstractImg {

    public TileImg(File file) throws Exception {
        BufferedImage original = crop(ImageIO.read(file));
        BufferedImage small = new BufferedImage(3, 3, BufferedImage.TYPE_INT_RGB);
        small.getGraphics().drawImage(original, 0, 0, 3, 3, null);
        super.image = small;
    }


    private static BufferedImage crop(BufferedImage image) {
        int cropSize = Math.min(image.getWidth(), image.getHeight());
        int offsetX = (image.getWidth() - cropSize) / 2;
        int offsetY = (image.getHeight() - cropSize) / 2;
        return image.getSubimage(offsetX, offsetY, cropSize, cropSize);
    }
}
