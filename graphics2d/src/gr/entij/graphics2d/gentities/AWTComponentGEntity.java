package gr.entij.graphics2d.gentities;

import gr.entij.Entity;
import gr.entij.graphics2d.GEntity;

import java.awt.Component;
import java.awt.Graphics2D;

public class AWTComponentGEntity extends GEntity {
    private Component component;
    
    public AWTComponentGEntity() {
    }

    public AWTComponentGEntity(Entity target) {
        super(target);
    }
    
    public AWTComponentGEntity(Component component) {
        this.component = component;
    }

    public AWTComponentGEntity(Entity target, Component component) {
        super(target);
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
        informUpdate();
    }

    @Override
    protected void offScreenPaint(Graphics2D g) {
        super.offScreenPaint(g);
        if (component != null) {
            component.paint(g);
        }
    }
}
