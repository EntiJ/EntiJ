package gr.entij.graphics2d.positionings;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import gr.entij.graphics2d.PositionTransformation;

public class MappingPositionTransformation implements PositionTransformation {

    public static class TargetTransformationRec {
        public PositionTransformation targetTransformation;
        public int targetPositionType;
        public TargetTransformationRec() {}
        
        public TargetTransformationRec(int targetPositionType,
                PositionTransformation targetTransformation) {
            this.targetPositionType = targetPositionType;
            this.targetTransformation = targetTransformation;
        }
    }
    
    private PositionTransformation defaultTransformation;
    private final Map<Integer, TargetTransformationRec> mapping = new HashMap<Integer, TargetTransformationRec>();
    
    public PositionTransformation getDefaultTransformation() {
        return defaultTransformation;
    }

    public void setDefaultTransformation(
            PositionTransformation defaultTransformation) {
        this.defaultTransformation = defaultTransformation;
    }

    public TargetTransformationRec set(int positionType,
            PositionTransformation targetTransformation, int targetPositionType) {
        return mapping.put(positionType, new TargetTransformationRec(targetPositionType, targetTransformation));
    }
    
    public TargetTransformationRec get(int positionType) {
        return mapping.get(positionType);
    }
    
    public Integer getTargetPositionType(int positionType) {
        TargetTransformationRec rec = get(positionType);
        return rec == null ? null : rec.targetPositionType;
    }
    
    public PositionTransformation getTargetTransformation(int positionType) {
        TargetTransformationRec rec = get(positionType);
        return rec == null ? null : rec.targetTransformation;
    }
    
    public TargetTransformationRec clear(int positionType) {
        return mapping.remove(positionType);
    }
    
    @Override
    public Point logicalToRealPosit(long posit, int positioningType, int w,
            int h) {
        TargetTransformationRec rec = get(positioningType);
        if (rec != null) {
            return rec.targetTransformation.logicalToRealPosit(
                    posit, rec.targetPositionType, w, h);
        } else if (defaultTransformation != null) {
            return defaultTransformation.logicalToRealPosit(
                    posit, positioningType, w, h);
        } else {
            return NOWHERE.logicalToRealPosit(posit, positioningType, w, h);
        }
    }

    @Override
    public Dimension logicalToRealSize(int logicalW, int logicalH,
            int positioningType, int w, int h) {
        TargetTransformationRec rec = get(positioningType);
        if (rec != null) {
            return rec.targetTransformation.logicalToRealSize(
                    logicalW, logicalH, rec.targetPositionType, w, h);
        } else if (defaultTransformation != null) {
            return defaultTransformation.logicalToRealSize(
                    logicalW, logicalH, positioningType, w, h);
        } else {
            return NOWHERE.logicalToRealSize(logicalW, logicalH, positioningType, w, h);
        }
    }

}
