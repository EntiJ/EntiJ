package gr.entij.graphics2d;

import java.awt.Rectangle;

class AdditiveRectange {
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    
    
    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getWidth() {
        return maxX - minX;
    }
    
    public int getHeight() {
        return maxY - minY;
    }
    
    public boolean addArea(int minX, int minY, int maxX, int maxY) {
        boolean changed = false;
        
        if (minX < this.minX) {
            this.minX = minX;
            changed = true;
        }
        if (minY < this.minY) {
            this.minY = minY;
            changed = true;
        }
        if (maxX > this.maxX) {
            this.maxX = maxX;
            changed = true;
        }
        if (maxY > this.maxY) {
            this.maxY = maxY;
            changed = true;
        }
        
        return changed;
    }
    
    public boolean addRectangle(int x, int y, int w, int h) {
        return addArea(x, y, x+w, y+h);
    }
    
    public boolean isValid() {
        return minX <= maxX && minY <= maxY;
    }
    
    public Rectangle getRectangle() {
        if (isValid()) {
            return new Rectangle(minX, minY, maxX-minX, maxY-minY);
        } else {
            return null;
        }
    }
}
