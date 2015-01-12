package gr.entij.graphics2d.effects;

import gr.entij.graphics2d.Effect;
import gr.entij.graphics2d.GEntity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;


public class Border implements Effect {
    private int upWidth, rightWidth, downWidth, leftWidth;
    private Color color;

    public Border(int upWidth, int rightWidth, int downWidth, int leftWidth, Color color) {
        this.upWidth = upWidth;
        this.rightWidth = rightWidth;
        this.downWidth = downWidth;
        this.leftWidth = leftWidth;
        this.color = color;
    }

    public Border(Color color) {
        this.color = color;
        this.upWidth = 1;
        this.rightWidth = 1;
        this.downWidth = 1;
        this.leftWidth = 1;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getUpWidth() {
        return upWidth;
    }

    public void setUpWidth(int upWidth) {
        this.upWidth = upWidth;
    }

    public int getRightWidth() {
        return rightWidth;
    }

    public void setRightWidth(int rightWidth) {
        this.rightWidth = rightWidth;
    }

    public int getDownWidth() {
        return downWidth;
    }

    public void setDownWidth(int downWidth) {
        this.downWidth = downWidth;
    }

    public int getLeftWidth() {
        return leftWidth;
    }

    public void setLeftWidth(int leftWidth) {
        this.leftWidth = leftWidth;
    }

    public void setWidths(int upWidth, int rightWidth, int downWidth, int leftWidth) {
        this.upWidth = upWidth;
        this.rightWidth = rightWidth;
        this.downWidth = downWidth;
        this.leftWidth = leftWidth;
    }
    
    @Override
    public void prepare(GEntity target, Graphics2D g, long startTime) {
        
    }

    @Override
    public int apply(GEntity target, Graphics2D g, long startTime) {
        final Dimension size = target.getSize();
        final int width = size.width;
        final int height = size.height;
        
        g.setColor(color);
        if (upWidth > 0) {
            g.fillRect(0, 0, width, upWidth);
        }
        if (rightWidth > 0) {
            g.fillRect(width-rightWidth, 0, rightWidth, height);
        }
        if (downWidth > 0) {
            g.fillRect(0, height-downWidth, width, downWidth);
        }
        if (leftWidth > 0) {
            g.fillRect(0, 0, leftWidth, height);
        }
        
        return 0;
    }
    
}
