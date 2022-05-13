package me.ikevoodoo.jmage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

@SuppressWarnings("unused")
public class JImage {

    private BufferedImage image;

    public JImage(BufferedImage image) {
        if(image.getColorModel().getNumComponents() < 4) {
            BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g = img.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            this.image = img;
            return;
        }
        this.image = image;
    }

    public JImage resize(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, this.image.getTransparency());
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g.drawImage(this.image, 0, 0, width, height, null);
        g.dispose();
        this.image = img;
        return this;
    }

    public JImage mask(JImage mask) {
        int width = Math.min(this.image.getWidth(), mask.image.getWidth());
        int height = Math.min(this.image.getHeight(), mask.image.getHeight());
        WritableRaster maskRaster = mask.image.getRaster();
        WritableRaster imageRaster = image.getRaster();
        execute(this.image, width, height, data -> {
            maskRaster.getPixel(data.x, data.y, data.pixels);
            if(data.pixels[0] == 255 && data.pixels[1] == 255 && data.pixels[2] == 255)
                imageRaster.getPixel(data.x, data.y, data.pixels);
            else {
                if(data.pixels.length == 4)
                    data.pixels[3] = 0;
                else {
                    data.pixels[0] = 0;
                    data.pixels[1] = 0;
                    data.pixels[2] = 0;
                }
            }
            return data.pixels;
        });
        return this;
    }

    public JImage replace(Color color, Color replacement) {
        return replace(color, replacement, 15);
    }

    public JImage replace(Color color, Color replacement, int tolerance, Color... exclude) {
        int rgb = replacement.getRGB();
        int colorRgb = color.getRGB();
        WritableRaster raster = this.image.getRaster();
        execute(this.image, this.image.getWidth(), this.image.getHeight(), data -> {
            raster.getPixel(data.x, data.y, data.pixels);
            int pixelRgb = data.pixels[0] << 16 | data.pixels[1] << 8 | data.pixels[2];
            if(distance(pixelRgb, colorRgb) <= tolerance) {
                for (Color excludeColor : exclude) {
                    if(distance(pixelRgb, excludeColor.getRGB()) <= tolerance)
                        return data.pixels;
                }
                data.pixels[0] = (rgb >> 16) & 0xFF;
                data.pixels[1] = (rgb >> 8) & 0xFF;
                data.pixels[2] = rgb & 0xFF;
                if(data.pixels.length == 4)
                    data.pixels[3] = (rgb >> 24) & 0xFF;
            }
            return data.pixels;
        });
        return this;
    }

    public JImage write(String path) throws IOException {
        ImageIO.write(this.image, path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : "png", new File(path));
        return this;
    }

    public JImage merge(JImage image, int x, int y) {
        Graphics g = this.image.getGraphics();
        g.drawImage(image.image, x, y, null);
        g.dispose();
        return this;
    }

    public JImage flipHorizontal() {
        Graphics g = this.image.getGraphics();
        g.drawImage(this.image, 0, 0, this.image.getWidth(), this.image.getHeight(), this.image.getWidth(), 0, 0, this.image.getHeight(), null);
        g.dispose();
        return this;
    }

    public JImage flipVertical() {
        BufferedImage img = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.drawImage(this.image, 0, 0, this.image.getWidth(), this.image.getHeight(), 0, this.image.getHeight(), this.image.getWidth(), 0, null);
        g.dispose();
        this.image = img;
        return this;
    }

    public JImage text(JFont font, int x, int y, String text, Object... args) {
        Graphics g = this.image.getGraphics();
        String formatted = text;
        for (int i = 0; i < args.length; i++)
            formatted = formatted.replace("{" + i + "}", args[i].toString());
        String[] lines = formatted.split("\n");
        for (int i = 0; i < lines.length; i++) {
            font.drawString(g, lines[i], x, y + i * font.getSize());
        }
        //font.drawString(g, formatted, x, y);
        g.dispose();
        return this;
    }

    public byte[] toBytes() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(this.image, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    public void draw(Graphics g) {
        g.drawImage(this.image, 0, 0, null);
    }

    private void execute(BufferedImage image, int width, int height, Function<ExecuteData, int[]> function) {
        WritableRaster raster = image.getRaster();
        ExecuteData data = new ExecuteData();
        data.pixels = new int[image.getColorModel().getNumComponents()];
        for (data.x = 0; data.x < width; data.x++) {
            for (data.y = 0; data.y < height; data.y++) {
                raster.setPixel(data.x, data.y, function.apply(data));
            }
        }
    }

    private int distance(int rgb1, int rgb2) {
        int r1 = (rgb1 >> 16) & 0xFF;
        int g1 = (rgb1 >> 8) & 0xFF;
        int b1 = rgb1 & 0xFF;
        int r2 = (rgb2 >> 16) & 0xFF;
        int g2 = (rgb2 >> 8) & 0xFF;
        int b2 = rgb2 & 0xFF;
        return (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2);
    }

    @Override
    public String toString() {
        return "JImage[image=" + this.image + "]";
    }

    private static class ExecuteData {
        public int x;
        public int y;
        public int[] pixels;
    }
}
