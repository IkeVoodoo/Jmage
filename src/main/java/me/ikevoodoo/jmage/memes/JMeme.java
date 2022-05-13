package me.ikevoodoo.jmage.memes;

import me.ikevoodoo.jmage.JFont;
import me.ikevoodoo.jmage.JImage;
import me.ikevoodoo.jmage.JMage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class JMeme {

    private final JImage image;
    private final HashMap<String, JTextData> metadata = new HashMap<>();

    public JMeme(JImage template, Map<String, JTextData> metadata) {
        this.image = template;
        this.metadata.putAll(metadata);
    }

    public JMeme(File file, Map<String, JTextData> metadata) throws IOException, ExecutionException, InterruptedException {
        this(JMage.read(file).get().orElseThrow(), metadata);
    }

    public JMeme(URL url, Map<String, JTextData> metadata) throws IOException, ExecutionException, InterruptedException {
        this(JMage.read(url).get().orElseThrow(), metadata);
    }

    public JMeme(InputStream stream, Map<String, JTextData> metadata) throws IOException, ExecutionException, InterruptedException {
        this(JMage.read(stream).get().orElseThrow(), metadata);
    }

    public JMeme(String url, Map<String, JTextData> metadata) throws IOException, ExecutionException, InterruptedException {
        this(new URL(url), metadata);
    }

    public JImage getImage(String... text) {
        if(text.length % 2 != 0) {
            throw new IllegalArgumentException("Text must be in pairs");
        }

        JImage copy = image.copy();

        for (int i = 0; i < text.length; i += 2) {
            JTextData point = metadata.get(text[i]);
            if(point == null)
                throw new IllegalArgumentException("No text data for " + text[i]);
            copy.text(point.getFont(), point.getPosition().x, point.getPosition().y, text[i + 1]);
        }
        return copy;
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        HashMap<String, JTextData> metadata = new HashMap<>();
        metadata.put("top", new JTextData(new JFont("Comic Sans MS", 64), new Point(400, 100)));
        metadata.put("bottom", new JTextData(new JFont("Comic Sans MS", 64), new Point(400, 470)));
        metadata.put("footer", new JTextData(new JFont("Comic Sans MS", 32), new Point(400, 750)));
        new JMeme(
                "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmemegenerator.net%2Fimg%2Fimages%2F71690444.jpg&f=1&nofb=1",
                metadata).getImage(
                "top", "Making memes with\npaint",
                "bottom", "Using JMage to \nmake memes",
                "footer", "https://github.com/IkeVoodoo/JMage"
        ).write("meme2.png");

    }

}
