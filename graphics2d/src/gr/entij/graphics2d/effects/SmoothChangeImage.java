package gr.entij.graphics2d.effects;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import gr.entij.graphics2d.Effect;
import gr.entij.graphics2d.GEntity;

public class SmoothChangeImage implements Effect {
    
    private int duration;
    @SuppressWarnings("unused")
    private int stepCount;
    private BufferedImage nextImage;
    
    private int stepInterval;
    
    
    public SmoothChangeImage(int duration, int stepCount,
            BufferedImage nextImage) {
        super();
        this.duration = duration;
        this.stepCount = stepCount;
        this.nextImage = nextImage;
        stepInterval = Math.round(duration / (float) stepCount);
    }

    public BufferedImage getNextImage() {
        return nextImage;
    }

    public void setNextImage(BufferedImage nextImage) {
        this.nextImage = nextImage;
    }


    @Override
    public void prepare(GEntity target, Graphics2D g, long startTime) {
        long timeElapsed = System.currentTimeMillis() - startTime;
        if (timeElapsed > duration) {
            return;
        }

        float alpha = timeElapsed / (float) duration;
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1-alpha);
        g.setComposite(comp);
    }

    @Override
    public int apply(GEntity target, Graphics2D g, long startTime) {
        long timeElapsed = System.currentTimeMillis() - startTime;
        if (timeElapsed > duration) {
            if (target.getImage() != nextImage) {
                target.setImage(nextImage);
                target.getParent().renderOne(target);
            }
            return -1;
        }
        
        if (nextImage == null) return stepInterval;

        float alpha = timeElapsed / (float) duration;
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(comp);
        Dimension size = target.getSize();
        g.drawImage(nextImage,
                0, 0, size.width, size.height,
                0, 0, nextImage.getWidth(), nextImage.getHeight(),
                null);
        return stepInterval;
    }

}
