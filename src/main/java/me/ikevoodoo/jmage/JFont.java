package me.ikevoodoo.jmage;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static me.ikevoodoo.jmage.JUtils.toBytes;
import static me.ikevoodoo.jmage.JUtils.fromBytes;

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

    public byte[] getBytes() {
        byte[] textBytes = font.getName().getBytes();
        byte[] bytes = new byte[textBytes.length + 12];
        System.arraycopy(textBytes, 0, bytes, 0, textBytes.length);
        toBytes(bytes, textBytes.length, font.getStyle());
        toBytes(bytes, textBytes.length + 4, font.getSize());
        toBytes(bytes, textBytes.length + 8, color.getRGB());
        return bytes;
    }

    public int calculateSize() {
        return font.getName().getBytes().length + 12;
    }

    public static JFont createFrom(byte[] bytes, int offset) {
        String name = new String(bytes, offset, bytes.length - 12);
        int style = fromBytes(bytes, offset + name.length());
        int size = fromBytes(bytes, offset + name.length() + 4);
        int color = fromBytes(bytes, offset + name.length() + 8);
        return new JFont(name, style, size, new Color(color));
    }

    public JFont write(DataOutputStream out) throws IOException {
        byte[] bytes = getBytes();
        out.writeInt(bytes.length);
        out.write(bytes);
        return this;
    }

    public static JFont read(DataInputStream in) throws IOException {
        int size = in.readInt();
        byte[] bytes = new byte[size];
        in.readFully(bytes);
        return createFrom(bytes, 0);
    }

    @Override
    public String toString() {
        return "JFont{" +
                "font=" + font +
                ", color=" + color +
                '}';
    }
}
