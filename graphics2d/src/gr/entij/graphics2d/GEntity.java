package gr.entij.graphics2d;

import gr.entij.event.MoveEvent;
import gr.entij.event.EntityEvent;
import gr.entij.event.StateEvent;
import gr.entij.Entity;
import gr.entij.Component;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import gr.entij.event.EntityEvent.Type;
import static gr.entij.graphics2d.GTerrain.THREAD_POOL;
import gr.entij.graphics2d.event.GtMouseEnterEvent;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

public class GEntity implements Component {
    
    private static class EffectRecord {
        final Effect effect;
        final long startTime;
        
        public EffectRecord(Effect effect, long startTime) {
            this.effect = effect;
            this.startTime = startTime;
        }
    }

    private int x, y;
    private int zIndex;
    private int mouseIndex;
    private Cursor cursor;
    private boolean visible = true;
    private boolean moveSmoth = true;
    private int logicalWidth = 1, logicalHeight = 1;
    private int positionType;
    private PositionTransformation positioning;
    private BufferedImage image;
    protected GTerrain parent; 
    private Entity target;
    
    private final List<EffectRecord> effects = new ArrayList<>();
    private final List<Consumer<GtMouseEnterEvent>> mouseEnterListeners = new LinkedList<>();
//    private final BlockingQueue smoothMoveQueue = new LinkedBlockingQueue();
//    private final Thread smouthMoveThread = new Thread(() -> {
//    });
    
    
    public GEntity() {}
    
    public GEntity(Entity target) {
        attach(target);
    }

    public GEntity(String imgFile)  throws FileNotFoundException, IOException {
        image = ImageIO.read(
                ClassLoader.getSystemResource(imgFile));
    }

    public GEntity(BufferedImage image) {
        super();
        this.image = image;
    }

    @Override
    public void attach(Entity target) {
        this.target = target;
        target.addMoveListener((MoveEvent e) ->
            THREAD_POOL.execute(() -> proccessMoveEvent(e)));
        target.addStateListener((StateEvent e) ->
            THREAD_POOL.execute(() -> processStateEvent(e)));
        target.addEntityListenerRemovable((EntityEvent e) -> {
            THREAD_POOL.execute(() -> {
                processEntityEvent(e);
            });
            return e.type != Type.DESTROYED;
        });
    }

    public void addEffect(Effect toAdd) {
        effects.add(new EffectRecord(toAdd, System.currentTimeMillis()));
        informUpdate();
    }
    
    public void removeEffect(Effect toRemove) {
        for (int i = 0; i < effects.size(); i++) {
            if (toRemove.equals(effects.get(i).effect)) {
                effects.remove(i);
                return;
            }
        }
        informUpdate();
    }
    
    public void addGtMouseEnterListener(Consumer<GtMouseEnterEvent> l) {
        Objects.requireNonNull(l, "listener cannot be null");
        mouseEnterListeners.add(l);
    }
    
    public void removeGtMouseEnterListener(Consumer<GtMouseEnterEvent> toRemove) {
        mouseEnterListeners.remove(toRemove);
    }
    
    void dispatchGtMouseEnterEvent(GtMouseEnterEvent evt) {
        new ArrayList<>(mouseEnterListeners)
                .forEach((Consumer<GtMouseEnterEvent> l) -> l.accept(evt));
    }
    
    public Entity getTarget() {
        return target;
    }
    
    public GTerrain getParent() {
        return parent;
    }

    void setParent(GTerrain parent) {
        this.parent = parent;
    }
    
    public boolean isVisible() {
        return visible;
    }

//    public int getWidth() {
//        return getSize().x;
//    }
//
//    public int getHeight() {
//        return getSize().y;
//    }
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            informUpdate();
            scheduleRepaintArea(50);
        }
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
        if (parent != null) {
            parent.updateGFigureZOrder(this);
            informUpdate();
        }
    }
    
    public int getMouseIndex() {
        return mouseIndex;
    }
    
    public void setMouseIndex(int mouseIndex) {
        this.mouseIndex = mouseIndex;
    }

    public Cursor getCursor() {
        return cursor;
    }

    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    public int getPositionType() {
        return positionType;
    }
    
    public void setPositionType(int positioningType) {
        informUpdate();
        this.positionType = positioningType;
        if (parent != null && target != null) {
            placeOnTerrain();
        }
        informUpdate();
    }

    public PositionTransformation getPositioning() {
        return positioning != null
                ? positioning
                : (parent != null ? parent.getPositionTransformation() : null);
    }

    public void setPositioning(PositionTransformation positioning) {
        informUpdate();
        this.positioning = positioning;
        if (parent != null && target != null) {
            placeOnTerrain();
        }
        informUpdate();
    }
    
    public void setPositioningSmooth(PositionTransformation positioning, int step, int interval) {
        informUpdate();
        this.positioning = positioning;
        if (parent != null && target != null) {
            Point finalPosit = getPositioning().logicalToRealPosit(target.getPosit(),
                    positionType, parent.getWidth(), parent.getHeight());
            smoothMove(finalPosit.x, finalPosit.y, step, interval);
        }
        informUpdate();
    }
    
    public int getLogicalWidth() {
        return logicalWidth;
    }

    public void setLogicalWidth(int logicalWidth) {
        this.logicalWidth = logicalWidth;
    }

    public int getLogicalHeight() {
        return logicalHeight;
    }

    public void setLogicalHeight(int logicalHeight) {
        this.logicalHeight = logicalHeight;
    }
    
    public void setLogicalSize(int logicalWidth, int logicalHeight) {
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public boolean isMoveSmoth() {
        return moveSmoth;
    }

    public void setMoveSmoth(boolean moveSmoth) {
        this.moveSmoth = moveSmoth;
    }

    public Dimension getSize() {
        return getPositioning()
                .logicalToRealSize(logicalWidth, logicalHeight,
                 positionType, parent.getWidth(), parent.getHeight());
    }
    
    protected void proccessMoveEvent(MoveEvent e) {
        Point finalPosit = getPositioning().logicalToRealPosit(e.nextPosit, positionType, parent.getWidth(), parent.getHeight());
        if (moveSmoth) {
            smoothMove(finalPosit.x, finalPosit.y, 2, 4);
        } else {
            informUpdate();
            x = finalPosit.x;
            y = finalPosit.y;
            informUpdate();
        }
    }
    
    protected void processStateEvent(StateEvent e) {
    }
    
    protected void processEntityEvent(EntityEvent e) {
        if (e.type == Type.DESTROYED) {
            if (parent != null) {
                parent.remove(this);
            }
        }
    }
    
    public void smoothMove(int finalX, int finalY, int step, int interval) {
        smoothMoveImpl(finalX, finalY, step, interval);
    }
    
    private void smoothMoveImpl(int finalX, int finalY, int step, int interval) {
        float dx = finalX - this.x;
        float dy = finalY - this.y;
        float d = (float) Math.sqrt(dx*dx+dy*dy);
        
        float stepX = (float) step * (dx/d);
        float stepY = (float) step * (dy/d);
        float fX = this.x;
        float fY = this.y;
        
        int stepCount = Math.round(d / (float) step);
        for (int i = 1; i < stepCount; i++) {
            informUpdate();
            fX += stepX;
            fY += stepY;
            this.x = Math.round(fX);
            this.y = Math.round(fY);
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
        informUpdate();
        
        // ensure correct placement
        this.x = finalX;
        this.y = finalY;
        informUpdate();
    }
    
    void placeOnTerrain() {
        Point posit = getPositioning()
                .logicalToRealPosit(target.getPosit(), positionType, parent.getWidth(), parent.getHeight());
        x = posit.x;
        y = posit.y;
    }
    
    Graphics2D offScreenRender() {
        return offScreenRender(null);
    }
    
    Graphics2D offScreenRender(Shape clip) {
        if (!visible) return null;
//        timesRendered++;
        Dimension size = getSize();
        if (size.width <= 0) {
            return null;
        }
        Graphics2D g = (Graphics2D) parent.getBackGraphics();
        if (g == null) return null;
        g = (Graphics2D) g.create(x, y, size.width, size.height);
        g.setClip(clip);
        prepareEffects(g);
        offScreenPaint(g);
        applyEffects(g);
        
        return g;
    }
    
    private void prepareEffects(Graphics2D g) {
        for (int i = 0; i < effects.size(); i++) {
            EffectRecord effectRecord = effects.get(i);
            effectRecord.effect.prepare(this, g, effectRecord.startTime);
        }
    }
    
    private void applyEffects(Graphics2D g) {
        for (int i = 0; i < effects.size(); i++) {
            EffectRecord effectRecord = effects.get(i);
            int delay = effectRecord.effect.apply(this, g, effectRecord.startTime);
            if (delay > 0) {
                scheduleInformUpdate(delay);
            } else if (delay == -1) {
                if (effects.remove(effectRecord)) {
                    i--;
                }
            }
        }
    }
    
    protected void offScreenPaint(Graphics2D g) {
        Dimension size = getSize();
        if (image != null) {
            g.drawImage(image,
                    0, 0, size.width, size.height,
                    0, 0, image.getWidth(), image.getHeight(),
                    null);
        }
    }
    
    public void informUpdate() {
        if (parent == null) return;
        parent.addModifiedArea(this);
    }
    
    public void scheduleRepaintArea(long millis) {
        if (parent == null) return;
        int x = this.x;
        int y = this.y;
        Dimension size = getSize();
        THREAD_POOL.execute(() -> {
            Util.sleep(millis);
            if (parent != null) {
                parent.addModifiedArea(x, y, x+size.width, y+size.height);
            }
        });
    }
    
    public void scheduleInformUpdate(long millis) {
        THREAD_POOL.execute(() -> {
            Util.sleep(millis);
            informUpdate();
        });
    }
//
//    /**
//     * returns the image.
//     *
//     * @return the image
//     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Sets the image and refreshes.
     *
     * @param img the image that will be displayed after this call
     */
    public void setImage(BufferedImage img) {
        if (img == image) {
            return;
        }
        image = img;
        if (parent != null) {
            parent.addModifiedArea(this);
        }
    }

    public Rectangle getBounds() {
        final Dimension size = getSize();
        return new Rectangle(x, y, size.width, size.height);
    }
    
    public Point getCenter() {
        final Dimension size = getSize();
        return new Point(x+size.width/2, y+size.height/2);
    }
    
    public boolean intersects(int rx, int ry, int rw, int rh) {
        // after math...
        boolean onX, onY;
        Dimension size = getSize();
        
        if (2*(x - rx) > size.width-rw) {
            onX = x < rx + rw;
        } else {
            onX = rx < x + size.width;
        }
        if (2*(y - ry) > size.height-rh) {
            onY = y < ry + rh;
        } else {
            onY = ry < y + size.height;
        }
        
        return onX && onY;
    }
    
    public boolean intersects(GEntity other) {
        Dimension otherSize = other.getSize();
        return intersects(other.x, other.y, otherSize.width, otherSize.height);
    }

    public boolean contains(int x, int y) {
        Dimension size = getSize();
        return this.x <= x && x <= this.x + size.width &&
               this.y <= y && y <= this.y + size.getHeight();
    }
    
    public Point translateParentCoords(int x, int y) {
        return new Point(x - this.x, y - this.y);
    }
    
}
