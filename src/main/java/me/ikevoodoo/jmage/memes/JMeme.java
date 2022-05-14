package me.ikevoodoo.jmage.memes;

import me.ikevoodoo.jmage.JImage;
import me.ikevoodoo.jmage.JMage;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    public JMeme saveTo(File file) throws IOException {
        try(DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
            image.write(out);
            for (Map.Entry<String, JTextData> entry : metadata.entrySet()) {
                String text = entry.getKey();
                JTextData data = entry.getValue();
                out.writeUTF(text);
                data.write(out);
            }
        }
        return this;
    }

    public static JMeme loadFrom(File file) throws IOException {
        try(DataInputStream in = new DataInputStream(new FileInputStream(file))) {
            JImage image = JImage.read(in);
            HashMap<String, JTextData> metadata = new HashMap<>();
            while(in.available() > 0)
                metadata.put(in.readUTF(), JTextData.read(in));
            return new JMeme(image, metadata);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JMeme jMeme = (JMeme) o;
        return Objects.equals(image, jMeme.image) && Objects.equals(metadata, jMeme.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(image, metadata);
    }

    @Override
    public String toString() {
        return "JMeme{" +
                "image=" + image +
                ", metadata=" + metadata +
                '}';
    }
}
