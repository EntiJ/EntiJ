package gr.entij.graphics2d.gentities;

import gr.entij.Entity;
import gr.entij.graphics2d.GEntity;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class ColorGEntity extends GEntity {
    private Color color;
    private Composite comp;
    
    public ColorGEntity(Entity target, Color color, float alpha) {
        super(target);
        this.color = color;
        comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
    }
    
    public void setColor(Color color, float alpha) {
        this.color = color;
        comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        informUpdate();
    }
    
    @Override
    protected void offScreenPaint(Graphics2D g) {
        if (color != null) {
            final Composite oldComp = g.getComposite();
            g = (Graphics2D) g.create();
            g.setComposite(comp);
            g.setColor(color);
            Dimension size = getSize();
            g.fillRect(0, 0, size.width, size.height);
            g.setComposite(oldComp);
        }
        super.offScreenPaint(g);
    }
}
