package peanuty.framework.util;

import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class ImageHelper {

    /**
     * Return scaled image.
     * Pre-conditions: (source != null) && (width > 0) && (height > 0)
     *
     * @param source the image source
     * @param width the new image's width
     * @param height the new image's height
     * @return the new image scaled
     */
    public static BufferedImage getScaleImage(BufferedImage source,
                                              int width, int height) {
        //assert(source != null && width > 0 && height > 0);
        BufferedImage image = new BufferedImage(width, height, source.getType());
        image.createGraphics().drawImage(source, 0, 0, width, height, null);
        return image;
    }

    /**
     * Return scaled image.
     * Pre-conditions: (source != null) && (xscale > 0) && (yscale > 0)
     *
     * @param source the image source
     * @param xscale the percentage of the source image's width
     * @param yscale the percentage of the source image's height
     * @return the new image scaled
     */
    public static BufferedImage getScaleImage(BufferedImage source,
                                              double xscale, double yscale) {
        //assert(source != null && width > 0 && height > 0);
        return getScaleImage(source,
                (int)(source.getWidth() * xscale), (int)(source.getHeight() * yscale));
    }

    /**
     * Read the input image file, scaled then write the output image file.
     *
     * @param input the input image file
     * @param output the output image file
     * @param width the output image's width
     * @param height the output image's height
     * @return true for sucessful,
     * false if no appropriate reader or writer is found.
     * @throws IOException IOException
     */
    public static boolean scaleImage(File input, File output,
                                     int width, int height) throws IOException {
        BufferedImage image = ImageIO.read(input);
        if (image == null) { return false; }
        image = getScaleImage(image, width, height);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.')+1).toLowerCase();
        return ImageIO.write(image, format, output);
    }

    /**
     * Read the input image file, scaled then write the output image file.
     *
     * @param input the input image file
     * @param output the output image file
     * @param xscale the percentage of the input image's width for output image's width
     * @param yscale the percentage of the input image's height for output image's height
     * @return true for sucessful,
     * false if no appropriate reader or writer is found.
     * @throws IOException IOException
     */
    public static boolean scaleImage(File input, File output,
                                     double xscale, double yscale) throws IOException {
        BufferedImage image = ImageIO.read(input);
        if (image == null) { return false; }
        image = getScaleImage(image, xscale, yscale);
        String name = output.getName();
        String format = name.substring(name.lastIndexOf('.')+1).toLowerCase();
        return ImageIO.write(image, format, output);
    }
}