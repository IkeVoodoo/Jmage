package me.ikevoodoo.jmage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JImage {

    private BufferedImage image;

    public JImage(BufferedImage image) {
        this.image = image;
    }

    public JImage resize(int width, int height) {
        Image scaled = this.image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = this.image.getGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return this;
    }

    public JImage mask(JImage mask) {
        BufferedImage img = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < this.image.getWidth(); x++)
            for (int y = 0; y < this.image.getHeight(); y++)
                img.setRGB(x, y, mask.image.getRGB(x, y) == 0xFFFFFFFF ? this.image.getRGB(x, y) : 0x00000000);
        this.image = img;
        return this;
    }

    public JImage write(String path) throws IOException {
        String extension = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : "png";
        ImageIO.write(this.image, extension, new File(path));
        return this;
    }

    public JImage merge(JImage image, int x, int y) {
        Graphics g = this.image.getGraphics();
        g.drawImage(image.image, x, y, null);
        g.dispose();
        return this;
    }

    public JImage text(JFont font, int x, int y, String text, Object... args) {
        Graphics g = this.image.getGraphics();
        String formatted = text;
        for (int i = 0; i < args.length; i++)
            formatted = formatted.replace("{" + i + "}", args[i].toString());
        font.drawString(g, formatted, x, y);
        g.dispose();
        return this;
    }

    @Override
    public String toString() {
        return "JImage[image=" + this.image + "]";
    }
}
