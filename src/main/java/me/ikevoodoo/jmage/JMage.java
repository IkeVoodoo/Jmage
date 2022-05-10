package me.ikevoodoo.jmage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class JMage {

    private JMage() {}

    public static Future<Optional<JImage>> read(String url) throws IOException {
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("accept-encoding", "gzip, deflate, br");
        conn.setRequestProperty("keep-alive", "true");
        conn.setRequestProperty("host", u.getHost());
        conn.connect();
        return JMage.read(conn.getInputStream());
    }

    public static Future<Optional<JImage>> read(InputStream is) {
        return new Future<>() {
            private boolean done = false;

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return done;
            }

            @Override
            public Optional<JImage> get() {
                Optional<JImage> opt;
                try {
                    BufferedImage img = ImageIO.read(is);
                    opt = Optional.of(new JImage(img));
                } catch (IOException e) {
                    e.printStackTrace();
                    opt = Optional.empty();
                }
                done = true;
                return opt;
            }

            @Override
            public Optional<JImage> get(long timeout, TimeUnit unit)  {
                return get();
            }
        };
    }
}
