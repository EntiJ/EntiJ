package gr.entij.graphics2d;

import gr.entij.graphics2d.event.GTMouseEvent;
import gr.entij.graphics2d.event.GtMouseEnterEvent;
import gr.entij.graphics2d.positionings.DefaultPositionTransformation;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.*;
import java.util.*;
import static java.util.Arrays.asList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.stream.Collectors.toCollection;
import static java.lang.Math.round;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class GTerrain extends Canvas {
    private static final long serialVersionUID = 6310699427787717365L;

    protected VolatileImage backBuffer;
    volatile Graphics2D backGraphics;
    private final ArrayList<GEntity> gEntities = new ArrayList<>(5);
    
    private PositionTransformation positTansform
        = new DefaultPositionTransformation(8, 8);
    private Image backgroundImage;

    private final LinkedList<Rectangle> modifiedAreas = new LinkedList<>();
    private volatile int fps;
    
    private Cursor defaultCursor;
    
    private final List<Consumer<GTMouseEvent>> mouseListeners = new LinkedList<>();
    private List<GEntity> lastMoused;
    
    static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
    
    
    private final AtomicInteger realFps = new AtomicInteger();
    private final ScheduledThreadPoolExecutor renderingService 
            = new ScheduledThreadPoolExecutor(2);
    private Consumer<Integer> fpsLogger;
    
    private final Runnable renderingRunable = () -> {
        if (!modifiedAreas.isEmpty()) {
            try {
                render();
                realFps.incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    
    private final Runnable countFpsRunnable = () -> {
        int oldValue = realFps.getAndSet(0);
        Consumer<Integer> fpsLogg = this.fpsLogger;
        if (fpsLogg != null) {
            THREAD_POOL.submit(() -> fpsLogg.accept(oldValue));
        }
    };
    
    
//    // locks. if both lov
//    private final Object renderLock = new Object();
//    private final Object gEntitiesLock = new Object();
    
    public GTerrain() {
        setBackground(Color.black);
        addComponentListener(new SizeListener());
        final MouseAdapter mouseAdapter = new MouseAdapter() {
            
            @Override public void mouseReleased(MouseEvent e) { process(e); }
            @Override public void mousePressed (MouseEvent e) { process(e); }
            @Override public void mouseClicked (MouseEvent e) { process(e); }
            @Override public void mouseMoved (MouseEvent e) { process(e); }
            @Override public void mouseDragged (MouseEvent e) { process(e); }
            
            void process(MouseEvent e) {
                // TODO should synchronize on gEntities
                final List<GEntity> atMouse = getGEntitiesByMouseIndexAt(e.getX(), e.getY());
                GTMouseEvent evt = new GTMouseEvent(
                        atMouse, e);
                if (evt.isMove()) {
                    boolean cursorSet = false;
                    for (ListIterator<GEntity> it = atMouse.listIterator(atMouse.size()); it.hasPrevious();) {
                        GEntity ge = it.previous();
                        if (ge.getCursor() != null) {
                            cursorSet= true;
                            setCursor(ge.getCursor());
                            break;
                        }
                    }
                    if (!cursorSet) setCursor(defaultCursor);
                    List<GEntity> mouseEntered = new LinkedList<>(atMouse);
                    if (lastMoused != null) {
                        mouseEntered.removeAll(lastMoused);
                        List<GEntity> mouseExited = lastMoused;
                        mouseExited.removeAll(atMouse);
                        mouseExited.forEach((GEntity ge) -> ge.dispatchGtMouseEnterEvent(new GtMouseEnterEvent(e, ge, false)));
                    }
                    mouseEntered.forEach((GEntity ge) -> ge.dispatchGtMouseEnterEvent(new GtMouseEnterEvent(e, ge, true)));
                }
                dispatchGTMouseEvent(evt);
                lastMoused = atMouse;
            }
        };
        
        addMouseMotionListener(mouseAdapter);
        addMouseListener(mouseAdapter);
        
    }
    
    public PositionTransformation getPositionTransformation() {
        return positTansform;
    }
    
    public void setPositionTransformation(PositionTransformation positTansform) {
        this.positTansform = positTansform;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Cursor getDefaultCursor() {
        return defaultCursor;
    }

    public void setDefaultCursor(Cursor defaultCursor) {
        this.defaultCursor = defaultCursor;
    }
    
    public List<GEntity> getGEntities() {
        return new ArrayList<>(gEntities);
    }
    
    public int getGEntityCount() {
        return gEntities.size();
    }
    
    public void addGtMouseListener(Consumer<GTMouseEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
        synchronized (mouseListeners) {
            mouseListeners.add(toAdd);
        }
    }
    
    public boolean removeGtMouseListener(Consumer<GTMouseEvent> toRemove) {
        synchronized (mouseListeners) {
            return mouseListeners.remove(toRemove);
        }
    }
    
    void dispatchGTMouseEvent(GTMouseEvent e) {
        synchronized (mouseListeners) {
            mouseListeners.stream().forEach((l) -> l.accept(e));
        }
    }

    public synchronized void add(GEntity gf) {
        synchronized (gEntities) {
            gEntities.add(findInsertionPoint(gf.getZIndex()), gf);
        }
        gf.setParent(this);
        gf.placeOnTerrain();
        addModifiedArea(gf);
    }
    
    public synchronized void remove(GEntity gf) {
        boolean removed;
        synchronized (gEntities) {
            removed = gEntities.remove(gf);
        }
        if (removed) {
            addModifiedArea(gf);
            gf.setParent(null);
        }
    }
    
    public synchronized void clear() {
        for (GEntity gf : gEntities) {
            gf.setParent(null);
        }
        synchronized (gEntities) {
            gEntities.clear();
        }
        paintIfValid();
    }
    
    synchronized void updateGFigureZOrder(GEntity gf) {
        synchronized (gEntities) {
            gEntities.remove(gf);
            gEntities.add(findInsertionPoint(gf.getZIndex()), gf);
        }
        
    }
    
    Graphics2D getBackGraphics() {
        return backGraphics;
    }
    
    public Point logicalToRealPosit(long posit, int positionType) {
        return positTansform.logicalToRealPosit(posit, positionType, getWidth(), getHeight());
    }
    
    public synchronized List<GEntity> getGEntitiesAt(int x, int y) {
        return gEntities.stream()
                .filter(gent -> gent.contains(x, y))
                .collect(toCollection(ArrayList::new));
    }
    
    private List<GEntity> getGEntitiesByMouseIndexAt(int x, int y) {
        List<GEntity> result = new LinkedList<>();
        int maxMouseIndex = Integer.MIN_VALUE;
        
        for (GEntity gent : gEntities) {
            int gentMouseIndex = gent.getMouseIndex();
            if (gent.isVisible() && gentMouseIndex >= maxMouseIndex && gent.contains(x, y)) {
                if (gentMouseIndex > maxMouseIndex) {
                    maxMouseIndex = gentMouseIndex;
                    result.clear();
                }
                result.add(gent);
            }
        }
        
        return result;
    }
    
    private void createBackGraphics() {
        backBuffer = this.getGraphicsConfiguration().createCompatibleVolatileImage(
                getWidth(), getHeight());
        Graphics oldGraphics = backGraphics;
        backGraphics =  backBuffer.createGraphics();
        paintBackground();
        backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if (oldGraphics != null) {
            oldGraphics.dispose();
        }
    }

    public void addModifiedArea(GEntity ent) {
        if (ent.parent != this) {
            throw new IllegalArgumentException("Given entity belongs to diffrent parent");
        }
        Dimension size = ent.getSize();
        addModifiedArea(ent.getX(), ent.getY(), size.width, size.height);
    }
    
    
    public void addModifiedArea(final int x, final int y, final int w, final int h) {
        if (w <= 0 || h <= 0 || x+w < 0 || y+h < 0 || x > getWidth() || y > getHeight())
            return;
//        threadPool.execute(
//            new Runnable() {
//                @Override public void run() {
                    addModifiedAreaImpl(x, y, w, h);
////                  render();
//                }
//            });
    }
    
    private void addModifiedAreaImpl(int x, int y, int w, int h) {
        synchronized (this.modifiedAreas) {
            modifiedAreas.add(new Rectangle(x, y, w, h));
        }
    }
    
    private void clearModifiedAreas() {
        synchronized (this.modifiedAreas) {
            modifiedAreas.clear();
        }
    }
    
    
    public synchronized void render() {
        if (backGraphics == null) return;
        
        List<Rectangle> modifiedAreas;
        synchronized (this.modifiedAreas) {
            if (this.modifiedAreas.isEmpty()) {
                return;
            }
            modifiedAreas = new ArrayList<>(this.modifiedAreas);
            this.modifiedAreas.clear();
        }
        
        paintBackground(modifiedAreas);
        renderClipped(calcClips(modifiedAreas));
    }
    
    
    public synchronized void renderOne(GEntity toRender) {
        if (backGraphics == null) return;
        
        if (toRender.getParent() != this) {
            throw new IllegalArgumentException("The given GEntity does not belong to this GTerain");
        }
        final Rectangle bounds = toRender.getBounds();
        paintBackground(bounds.x, bounds.y, bounds.width, bounds.height);
        renderClipped(calcClips(asList(bounds)));
    }
    
    private List<Area> calcClips(List<Rectangle> modifiedAreas) {
        List<Area> clips = new ArrayList<>(gEntities.size());
        clips.addAll(Collections.nCopies(gEntities.size(), null));
        for (int i = 0; i < gEntities.size(); i++) {
            GEntity gi = gEntities.get(i);
            if (!gi.isVisible()) continue;
            Area clip = clips.get(i);
            final Rectangle giBounds = gi.getBounds();
            for (Rectangle rec : modifiedAreas) {
                if (gi.intersects(rec.x, rec.y, rec.width, rec.height)) {
                    final Rectangle intersectionRec = giBounds.intersection(rec);
                    intersectionRec.translate(-gi.getX(), -gi.getY());
                    final Area intersection = new Area(intersectionRec);
                    if (clip == null) {
                        clip = intersection;
                        clips.set(i, clip);
                    } else {
                        clip.add(intersection);
                    }
                }
            }
        }
        return clips;
    }
    
    private void renderClipped(List<? extends Shape> clips) {
        AdditiveRectange boundsToPaint = new AdditiveRectange();
        
        for (int i = 0; i < clips.size(); i++) {
            Shape clip = clips.get(i);
            if (clip == null) continue;
            GEntity gEnt = gEntities.get(i);
            Dimension size = gEnt.getSize();
            boundsToPaint.addRectangle(gEnt.getX(), gEnt.getY(), size.width, size.height);
            
            Graphics2D g = gEnt.offScreenRender(clip);
            if (g != null) {
                g.dispose();
            }
        }
        paintArea(boundsToPaint.getMinX(), boundsToPaint.getMinY(),
                boundsToPaint.getMaxX(), boundsToPaint.getMaxY());
    }
    
    public synchronized void renderAll() {
        if (backGraphics == null) return;
        
        paintBackground();
        for (GEntity gf : gEntities) {
            if (! gf.isVisible()) continue;
            Graphics g = gf.offScreenRender();
            if (g != null) {
                g.dispose();
            }
        }
        clearModifiedAreas();
        paintIfValid();
    }
    
    
    public synchronized void placeAll() {
        gEntities.stream().forEach(gf -> gf.placeOnTerrain());
        renderAll();
    }
    
    public synchronized void refresh() {
        paintBackground();
        placeAll();
        paintIfValid();
    }
    
    @Override
    public void paint(Graphics g) {
//        System.out.println("Paint area size "+getWidth()+", "+getHeight());
        if (backBuffer != null) {
            g.drawImage(backBuffer,
                        0, 0, getWidth(), getHeight(),
                        null);
        }
    }
    
    @Override
    public void update(Graphics g) {
        paint(g);
    }
    
    
    
    public void setFPS(final int fps) {
        if (fps == this.fps) return;
        renderingService.remove(countFpsRunnable);
        renderingService.remove(renderingRunable);
        realFps.set(0);
        if (fps != 0) {
            renderingService.scheduleAtFixedRate(countFpsRunnable, 1000L, 1000L, TimeUnit.MILLISECONDS);
            renderingService.scheduleWithFixedDelay(renderingRunable, 0L, 1000L/fps, TimeUnit.MILLISECONDS);
        }
        this.fps = fps;
    }

    public int getFPS() {
        return fps;
    }

    public void setFpsLogger(Consumer<Integer> fpsLogger) {
        this.fpsLogger = fpsLogger;
    }
    
    private void paintIfValid() {
        Graphics g = getGraphics();
        if (g != null) {
            paint(g);
            g.dispose();
            forcePaintToWork();
        }
    }
    
    private void paintBackground() {
        if (getBackground() != null) {
            backGraphics.setColor(getBackground());
            backGraphics.fillRect(0, 0, getWidth(), getHeight());
        }
        if (backgroundImage != null) {
            backGraphics.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }
    
    private void paintBackground(List<? extends Rectangle> areas) {
        if (getBackground() == null && backgroundImage == null) return;
        
        backGraphics.setColor(getBackground());
        if (getBackgroundImage() == null) {
            areas.forEach(area -> {
                backGraphics.fillRect(area.x, area.y, area.width, area.height);
            });
        } else if (getBackground() == null) {
            final float factorX = backgroundImage.getWidth(null) / (float) getWidth();
            final float factorY = backgroundImage.getHeight(null) / (float) getHeight();
            areas.forEach(area -> {
                backGraphics.clearRect(area.x, area.y, area.width, area.height);
                backGraphics.drawImage(backgroundImage, area.x, area.y, area.x+area.width, area.y+area.height,
                    round(area.x*factorX), round(area.y*factorY), round((area.x+area.width)*factorX), round((area.y+area.width)*factorY), null);
            });
        } else {
            final float factorX = backgroundImage.getWidth(null) / (float) getWidth();
            final float factorY = backgroundImage.getHeight(null) / (float) getHeight();
            areas.forEach(area -> {
                backGraphics.fillRect(area.x, area.y, area.width, area.height);
                backGraphics.drawImage(backgroundImage, area.x, area.y, area.x+area.width, area.y+area.height,
                    round(area.x*factorX), round(area.y*factorY), round((area.x+area.width)*factorX), round((area.y+area.width)*factorY), null);
            });
        }
    }
    
    private void paintBackground(int x, int y, int w, int h) {
        if (getBackground() == null && backgroundImage == null) return;
        backGraphics.clearRect(x, y, w, h);
        
        if (getBackground() != null) {
            backGraphics.setColor(getBackground());
            backGraphics.fillRect(x, y, w, h);
        }
        if (backgroundImage != null) {
            final float factorX = backgroundImage.getWidth(null) / (float) getWidth();
            final float factorY = backgroundImage.getHeight(null) / (float) getHeight();
            backGraphics.drawImage(backgroundImage, x, y, x+w, y+h,
                    round(x*factorX), round(y*factorY), round((x+w)*factorX), round((y+w)*factorY), null);
        }
    }
    
    void paintArea(int minX, int minY, int maxX, int maxY) {
        Graphics g = getGraphics();
        if (g != null && backBuffer != null) {
            g.drawImage(backBuffer,
                        minX, minY, maxX, maxY,
                        minX, minY, maxX, maxY,
                        this);
            g.dispose();
            forcePaintToWork();
        }
    }
    
    private void forcePaintToWork() {
        MouseInfo.getPointerInfo().getLocation(); // It works!! (who cares why!!!)
    }
    
    private int findInsertionPoint(int zIndex) {
        if (gEntities.isEmpty()) {
            return 0;
        } else {
            return findInsertionPoint(zIndex, 0, gEntities.size()-1);
        }
    }
    
    private int findInsertionPoint(int zIndex, int from, int to) {
        for (int i = to; i >= from; i--) {
            if (gEntities.get(i).getZIndex() <= zIndex) {
                return i+1;
            }
        }
        return from;
//        int m = (from+to)/2;
//        if (gEntities.get(m).getZIndex() < zIndex) {
//            if (m == gEntities.size()-1) {
//                return gEntities.size();
//            } else if (gEntities.get(m+1).getZIndex() > zIndex) {
//                return m+1;
//            } else {
//                return findInsertionPoint(zIndex, from, m);
//            }
//        } else if (gEntities.get(m).getZIndex() == zIndex) {
//            return m;
//        } else {
//            return findInsertionPoint(zIndex, m, to);
//        }
    }
    
    private class SizeListener extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
                createBackGraphics();
                placeAll();
                paintIfValid();
                System.gc();
            }
        }
    
    
}
