package gr.entij.graphics2d.effects;

import gr.entij.graphics2d.Effect;
import gr.entij.graphics2d.GEntity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 *
 * @author sta
 */
public class Background implements Effect {
    final Color color;
    final float alpha;
    final Image image;

    public Background(Color color, float alpha) {
        this.color = color;
        this.alpha = alpha;
        image = null;
    }
    
    public Background(float alpha, Image image) {
        this.alpha = alpha;
        this.image = image;
        color = null;
    }



    public Background(Color color, float alpha, Image image) {
        this.color = color;
        this.alpha = alpha;
        this.image = image;
    }



    @Override
    public void prepare(GEntity target, Graphics2D g, long startTime) {
        final Dimension size = target.getSize();
        final int width = size.width;
        final int height = size.height;
        g = (Graphics2D) g.create();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        if (color != null) {
            g.setColor(color);
            g.fillRect(0, 0, width, height);
        }
        if (image != null) {
            g.drawImage(image, 0, 0, width, height, null);
        }
        g.dispose();
    }

    @Override
    public int apply(GEntity target, Graphics2D g, long startTime) {
        return 0;
    }
}
