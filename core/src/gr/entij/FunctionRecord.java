package gr.entij;

import java.util.NoSuchElementException;
import java.util.function.BiFunction;

/**
 * A record of functions 
 */
public interface FunctionRecord {
    <T> T apply(Entity en, String func, Object... params)
            throws NoSuchElementException, ClassCastException;
    BiFunction<Entity, Object[], Object> lookUp(String func);
    default void init(Entity e) {}
    FunctionRecord child();
    default void setFunc(String funcName, BiFunction<Entity, Object[], Object> func)
            throws UnsupportedOperationException, NullPointerException {
        throw new UnsupportedOperationException("This function record is unmodifiable");
    }
}
