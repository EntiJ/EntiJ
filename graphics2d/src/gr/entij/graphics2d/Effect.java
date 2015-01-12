package gr.entij.graphics2d;

import java.awt.Graphics2D;

public interface Effect {
    void prepare(GEntity target, Graphics2D g, long startTime);
    int apply(GEntity target, Graphics2D g, long startTime);
}
