package gr.entij.function_records;

import gr.entij.Entity;
import gr.entij.FunctionRecord;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;

public class HashFunctionRecord implements FunctionRecord {
    
    private final FunctionRecord parent;
    private final Map<String, BiFunction<Entity, Object[], Object>> funcs = new HashMap<>();

    public HashFunctionRecord() {
        this(null);
    }

    HashFunctionRecord(FunctionRecord parent) {
        this.parent = parent;
    }
    
    @Override
    public <T> T apply(Entity en, String func, Object... params)
            throws NoSuchElementException, ClassCastException {
        BiFunction<Entity, Object[], Object> f = lookUp(func);
        if (f == null) {
            throw new NoSuchElementException("Not found function named: "+func);
        } else {
            return (T) f.apply(en, params);
        }
    }

    @Override
    public BiFunction<Entity, Object[], Object> lookUp(String func) {
        BiFunction<Entity, Object[], Object> f = funcs.get(func);
        return f != null || parent == null ? f : parent.lookUp(func);
    }

    @Override
    public FunctionRecord child() {
        return new HashFunctionRecord(this);
    }

    @Override
    public void init(Entity e) {}

    @Override
    public void setFunc(String funcName, BiFunction<Entity, Object[], Object> func)
            throws UnsupportedOperationException, NullPointerException {
        Objects.requireNonNull(func, "Function cannot be null");
        funcs.put(funcName, func);
    }
    
}
