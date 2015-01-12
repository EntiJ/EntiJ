package gr.entij.graphics2d.effects;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import gr.entij.graphics2d.Effect;
import gr.entij.graphics2d.GEntity;

public class BlinkImage implements Effect {

    private BufferedImage image;
    @SuppressWarnings("unused")
    private int blinkInterval;
    private int blinkDuration;
    
    private int blinkPeriod;
    
    
    
    public BlinkImage(BufferedImage image, int blinkInterval, int blinkDuration) {
        this.image = image;
        this.blinkInterval = blinkInterval;
        this.blinkDuration = blinkDuration;
        blinkPeriod = blinkDuration + blinkInterval;
    }

    @Override
    public void prepare(GEntity target, Graphics2D g, long startTime) {
//        long timeElapsed = System.currentTimeMillis() - startTime;
//        long offset = timeElapsed % blinkPeriod;
//        if (offset < blinkDuration) {
//            target.setImage(image);
//        } else {
//            target.setImage(null);
//        }
    }

    @Override
    public int apply(GEntity target, Graphics2D g, long startTime) {
        long timeElapsed = System.currentTimeMillis() - startTime;
        long offset = timeElapsed % blinkPeriod;
        if (offset < blinkDuration) {
            Dimension size = target.getSize();
            g.drawImage(image,
                    0, 0, size.width, size.height,
                    0, 0, image.getWidth(), image.getHeight(),
                    null);
            return (int) (blinkDuration - offset);
        } else {
            return (int) (blinkPeriod - offset);
        }
    }
    
}
