package me.ikevoodoo.jmage;

import java.awt.*;

public class JFont {

    private final Font font;
    private final Color color;

    public JFont(String name, int style, int size, Color color) {
        font = new Font(name, style, size);
        this.color = color;
    }

    public JFont(String name, int size, Color color) {
       this(name, Font.PLAIN, size, color);
    }

    public JFont(String name, int size) {
       this(name, size, Color.BLACK);
    }

    public void drawString(Graphics g, String text, int x, int y) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x, y);
    }

    public int getSize() {
        return font.getSize();
    }

}
