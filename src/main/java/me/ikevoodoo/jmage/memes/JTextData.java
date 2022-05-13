package me.ikevoodoo.jmage.memes;

import me.ikevoodoo.jmage.JFont;

import java.awt.*;

public class JTextData {

    private JFont font;
    private Point position;

    public JTextData(JFont font, Point position) {
        this.font = font;
        this.position = position;
    }

    public JFont getFont() {
        return font;
    }

    public Point getPosition() {
        return position;
    }

    public void setFont(JFont font) {
        this.font = font;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

}
