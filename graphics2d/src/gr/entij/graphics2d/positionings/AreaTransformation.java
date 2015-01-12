/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.entij.graphics2d.positionings;

import gr.entij.graphics2d.PositionTransformation;
import java.awt.Dimension;
import java.awt.Point;


public class AreaTransformation implements PositionTransformation {
    
    final PositionTransformation delegate;
    
    public AreaTransformation(int x, int y, int w, int h) {
        delegate = new DefaultPositionTransformation(1, 1).translateToArea(x, y, w, h);
    }

    @Override
    public Point logicalToRealPosit(long posit, int positioningType, int w, int h) {
        return delegate.logicalToRealPosit(posit, positioningType, w, h);
    }

    @Override
    public Dimension logicalToRealSize(int logicalW, int logicalH, int positioningType, int w, int h) {
        return delegate.logicalToRealSize(logicalW, logicalH, positioningType, w, h);
    }
    
    
}
