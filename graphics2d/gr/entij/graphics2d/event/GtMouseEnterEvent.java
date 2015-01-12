package gr.entij.graphics2d.event;

import gr.entij.graphics2d.GEntity;
import java.awt.event.MouseEvent;

/**
 *
 * @author sta
 */
public class GtMouseEnterEvent {
    private final MouseEvent original;
    private final GEntity source;
    private final boolean mouseEnter;

    public GtMouseEnterEvent(MouseEvent original, GEntity source, boolean isMouseEnter) {
        this.original = original;
        this.source = source;
        this.mouseEnter = isMouseEnter;
    }

    public MouseEvent getOriginal() {
        return original;
    }

    public GEntity getSource() {
        return source;
    }

    public boolean isMouseEnter() {
        return mouseEnter;
    }
    
    
}
