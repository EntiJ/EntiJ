package gr.entij.graphics2d;

import gr.entij.graphics2d.positionings.AreaTransformation;
import java.awt.*;

public interface PositionTransformation {
    Point logicalToRealPosit(long posit, int positioningType, int w, int h);
    Dimension logicalToRealSize(int logicalW, int logicalH, int positioningType, int w, int h);
    
    static final PositionTransformation NOWHERE = new PositionTransformation() {
        
        @Override
        public Dimension logicalToRealSize(int logicalW, int logicalH,
                int positioningType, int w, int h) {
            return new Dimension(0, 0);
        }
        
        @Override
        public Point logicalToRealPosit(long posit, int positioningType, int w,
                int h) {
            return new Point(-1, -1);
        }
    };
    
    static final PositionTransformation EVERYWHERE = new PositionTransformation() {
        final Point zero = new Point();
        
        @Override
        public Point logicalToRealPosit(long posit, int positioningType, int w, int h) {
            return zero;
        }

        @Override
        public Dimension logicalToRealSize(int logicalW, int logicalH, int positioningType, int w, int h) {
            return new Dimension(w, h);
        }
    };
    
    static PositionTransformation area(int x, int y, int w, int h) {
        return new AreaTransformation(x, y, w, h);
    }
    
    default PositionTransformation translateToArea(int xD, int yD, int wD, int hD) {
        return new PositionTransformation() {
            
            int translXD(int w) {
                return xD >= 0 ? xD : w+xD;
            }
            
            int translYD(int h) {
                return yD >= 0 ? yD : h+yD; 
            }
            
            int translW(int w) {
                return wD > 0 ? wD : (w+wD)-translXD(w);
            }

            int translH(int h) {
                return hD > 0 ? hD : (h+hD)-translYD(h);
            }
            
            @Override
            public Dimension logicalToRealSize(int logicalW, int logicalH,
                    int positioningType, int w, int h) {
                return PositionTransformation.this
                        .logicalToRealSize(logicalW, logicalH, positioningType,
                            translW(w), translH(h));
            }
            
            @Override
            public Point logicalToRealPosit(long posit, int positioningType, int w,
                    int h) {
                Point result = PositionTransformation.this.logicalToRealPosit(
                        posit, positioningType, translW(w), translH(h));
                result.x += translXD(w);
                result.y += translYD(h);
                return result;
            }
        };
    }
    
    default PositionTransformation squarize() {
        return squarize(0, 0);
    }
    
    default PositionTransformation squarize(int centeringX, int centeringY) {
        if (centeringX < -1 || centeringX > 1)
            throw new IllegalArgumentException("centeringX must be in {-1, 0, 1}");
        if (centeringY < -1 || centeringY > 1)
            throw new IllegalArgumentException("centeringY must be in {-1, 0, 1}");
        
        return new PositionTransformation() {
            
            @Override
            public Dimension logicalToRealSize(int logicalW, int logicalH,
                    int positioningType, int w, int h) {
                int size = Math.min(w, h);
                return PositionTransformation.this.logicalToRealSize(
                        logicalW, logicalH, positioningType, size, size);
            }
            
            @Override
            public Point logicalToRealPosit(long posit, int positioningType, int w,
                    int h) {
                int size = Math.min(w, h);
                Point result = PositionTransformation.this.logicalToRealPosit(
                        posit, positioningType, size, size);
                if (centeringX == 0) {
                    result.x += (w-size)/2;
                } else if (centeringX == 1) {
                    result.x += w-size;
                }
                if (centeringY == 0) {
                    result.y += (h-size)/2;
                } else if (centeringY == 1) {
                    result.y += h-size;
                }
                return result;
            }
        };
    }
    
    default PositionTransformation center(int width, int height) {
        return new PositionTransformation() {
            
            @Override
            public Dimension logicalToRealSize(int logicalW, int logicalH,
                    int positioningType, int w, int h) {
                if (width > 0) {
                    w = width;
                }
                if (height > 0) {
                    h = height;
                }
                return PositionTransformation.this.logicalToRealSize(
                        logicalW, logicalH, positioningType, w, h);
            }
            
            @Override
            public Point logicalToRealPosit(long posit, int positioningType, int w,
                    int h) {
                int wL = width > 0 ? width : w;
                int hL = height > 0 ? height : h;
                Point result = PositionTransformation.this.logicalToRealPosit(
                        posit, positioningType, wL, hL);
                if (width > 0) {
                    result.x += w/2 - width/2;
                }
                if (height > 0) {
                    result.y += h/2 - height/2;
                }
                return result;
            }
        };
    }
    
    default PositionTransformation centerX(int width) {
        return center(width, 0);
    }
    
    default PositionTransformation centerY(int height) {
        return center(0, height);
    }
}