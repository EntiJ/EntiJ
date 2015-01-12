package gr.entij.graphics2d.positionings;

import gr.entij.graphics2d.PositionTransformation;

import java.awt.Dimension;
import java.awt.Point;

public class DefaultPositionTransformation
    implements PositionTransformation {

    public static final int RATIO_SIZE = 0;
    public static final int ABSOLUTE_SIZE = 1;
    
    private int logicalWidth, logicalHeight;
    
    
    public DefaultPositionTransformation(int logicalWidth, int logicalHeight) {
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
    }

    @Override
    public Point logicalToRealPosit(long posit, int positioningType, int w, int h) {
        return logicalToRealPositImpl(posit, positioningType, w, h);
    }
    
    public Point logicalToRealPositImpl(long posit, int positioningType, int w, int h) {
        Dimension usableSize = usableSize(w, h);
        int squareWidth = usableSize.width / logicalWidth;
        int squareHeight = usableSize.height / logicalHeight;
        return new Point((int) (posit%logicalWidth) * squareWidth,
                (int) (posit/logicalWidth) * squareHeight);
    }

    @Override
    public Dimension logicalToRealSize(int logicalW, int logicalH,
            int positioningType, int w, int h) {
        if (positioningType == ABSOLUTE_SIZE) {
            return new Dimension(logicalW, logicalH);
        }
        Dimension useSize = usableSize(w, h);
        return new Dimension(logicalW * (useSize.width/logicalWidth),
                logicalH * (useSize.height/logicalHeight));
    }

    private Dimension usableSize(int w, int h) {
        return new Dimension(w/logicalWidth*logicalWidth, h/logicalHeight*logicalHeight);
    }
    
}
