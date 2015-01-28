package gr.entij.graphics2d;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import javax.imageio.ImageIO;

/**
 *
 * @author sta
 */
public class Util {

    private Util() {
    }
    
// TODO? Toolkit already has this...
    public static BufferedImage readImage(String path) throws IOException {
//        Toolkit.getDefaultToolkit().createImage(path);
//        final Enumeration<URL> systemResources = ClassLoader.getSystemResources(path);
//        while (systemResources.hasMoreElements()) {
//            System.out.println(systemResources.nextElement());
//        }
//        
        return ImageIO.read(
                ClassLoader.getSystemResource(path));
    }
    
    public static void sleep(long millis) {
        long now = System.currentTimeMillis();
        long remaining = millis - (System.currentTimeMillis() - now);
        while (remaining > 0) {
            try {
                Thread.sleep(remaining, 0);
            } catch (InterruptedException e) {}
            remaining = millis - (System.currentTimeMillis() - now);
        }
    }
}
