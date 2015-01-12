package gr.entij.graphics2d.event;

import gr.entij.EntitySet;
import gr.entij.graphics2d.*;

import java.awt.event.MouseEvent;
import java.util.*;

public class GTMouseEvent {
    private final Collection<GEntity> gEntities;
    private EntitySet entitySet;
    private final MouseEvent original;
    
    public GTMouseEvent(Collection<GEntity> gEntities, MouseEvent original) {
        this.gEntities = Collections.unmodifiableCollection(gEntities);
        this.original = original;
    }
    
    public Collection<GEntity> getGEntities() {
        return gEntities;
    }
    
    public EntitySet getEntitySet() {
        if (entitySet == null) {
            entitySet = new EntitySet();
            gEntities.stream().map(GEntity::getTarget).forEach(entitySet::add);
        }
        
        return entitySet;
    }
    
    public MouseEvent getOriginal() {
        return original;
    }
    
    public boolean isClick() {
        return original.getID() == MouseEvent.MOUSE_CLICKED;
    }
    
    public boolean isRelease() {
        return original.getID() == MouseEvent.MOUSE_RELEASED;
    }
    
    public boolean isPress() {
        return original.getID() == MouseEvent.MOUSE_PRESSED;
    }
    
    public boolean isMove() {
        return original.getID() == MouseEvent.MOUSE_MOVED;
    }
    
    public boolean isDrag() {
        return original.getID() == MouseEvent.MOUSE_DRAGGED;
    }
}
