package gr.entij.sound;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.sound.sampled.*;

public class SoundUtil {
    private SoundUtil() {}
    
    static class CacheEntry {
        long timestamp;
        Clip clip;
        
        CacheEntry() {}

        public CacheEntry(long timestamp, Clip clip) {
            super();
            this.timestamp = timestamp;
            this.clip = clip;
        }
    }
    
    
    private static final Map<String, CacheEntry> cache = new HashMap<>();
    
    static CacheEntry loadClip(File file) {
        AudioInputStream audioStream;
        try {
            long timeStamp = file.lastModified();
            audioStream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            Clip result = (Clip) AudioSystem.getLine(info);
            result.open(audioStream);
            return new CacheEntry(timeStamp, result);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // TODO handle exceptions (such as file not found)!
    public static Clip getClip(String filename) {
        File file = new File(ClassLoader.getSystemResource(filename).getPath());
        CacheEntry cached = cache.get(filename);
        
        if (cached == null || cached.timestamp < file.lastModified()) {
            cached = loadClip(file);
            cache.put(filename, cached);
        }
        
        return cached.clip;
    }
    
    // TODO handle exceptions (such as file not found)!
    public static void playClip(String filename) {
        System.out.println("Trying to play: "+filename);
        Clip clip = getClip(filename);
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }
}
