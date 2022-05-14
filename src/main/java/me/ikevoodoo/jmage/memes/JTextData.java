package me.ikevoodoo.jmage.memes;

import me.ikevoodoo.jmage.JFont;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static me.ikevoodoo.jmage.JUtils.fromBytes;
import static me.ikevoodoo.jmage.JUtils.toBytes;

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

    public byte[] getBytes() {
        byte[] bytes = new byte[font.calculateSize() + 8];
        System.arraycopy(font.getBytes(), 0, bytes, 0, font.calculateSize());
        toBytes(bytes, bytes.length - 8, position.x);
        toBytes(bytes, bytes.length - 4, position.y);
        return bytes;
    }

    public static JTextData createFrom(byte[] bytes) {
        JFont font = JFont.createFrom(bytes, 0);
        Point position = new Point(fromBytes(bytes, bytes.length - 8), fromBytes(bytes, bytes.length - 4));
        return new JTextData(font, position);
    }

    public JTextData write(DataOutputStream out) throws IOException {
        font.write(out);
        out.writeInt(position.x);
        out.writeInt(position.y);
        return this;
    }

    public static JTextData read(DataInputStream in) throws IOException {
        return new JTextData(JFont.read(in), new Point(in.readInt(), in.readInt()));
    }

    @Override
    public String toString() {
        return "JTextData{" +
                "font=" + font +
                ", position=" + position +
                '}';
    }
}
