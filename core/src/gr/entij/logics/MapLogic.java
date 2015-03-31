package gr.entij.logics;

import gr.entij.*;
import java.util.*;
import java.util.function.*;

public class MapLogic implements Logic {
    static final class Entry {
        BiPredicate<Entity, Object> matcher;
        BiFunction<Entity,  Object, Reaction> logic;
        Entry next;

        public Entry(BiPredicate<Entity, Object> matcher,
                BiFunction<Entity, Object, Reaction> logic) {
            this.matcher = matcher;
            this.logic = logic;
        }

        Reaction applyMatching(Entity e, Object m) {
            Entry current = this;
            while (current != null) {
                if (current.matcher == null || current.matcher.test(e, m)) {
                    return current.logic.apply(e, m);
                }
                current = current.next;
            }
            return null;
        }
        
        Entry findTail() {
            Entry current ;
            for (current = this; current.next != null; current = current.next);
            return current;
        }
    }

    private final Map<Object, Entry> map = new HashMap<>(5);
    private Entry head;
    private Entry tail;

    @Override
    public Reaction reaction(Entity e, Object move) {
        Entry mapping = map.get(move); // search for the exact (or equal) move
        if (mapping != null) {
            return mapping.applyMatching(e, move);
        }
        mapping = map.get(move.getClass()); // search for the move's class
        if (mapping != null) {
            return mapping.applyMatching(e, move);
        }

        if (head != null) {
            return head.applyMatching(e, move);
        }
        return null;
    }

    public MapLogic map(Object move, BiPredicate<Entity, Object> matcher,
            BiFunction<Entity, Object, Reaction> logic) {
        Objects.requireNonNull(move, "move cannot be null");
        Objects.requireNonNull(logic, "logic cannot be null");
        if (move.getClass() == Class.class)
            throw new IllegalArgumentException("move cannot be of class Class");
        
        Entry entry = new Entry(matcher, logic);
        Entry mapping = map.get(move);
        if (mapping != null) {
            mapping.findTail().next = entry;
        } else {
            map.put(move, entry);
        }
        return this;
    }
    public MapLogic map(Object move, BiFunction<Entity, Object, Reaction> logic) {
        return map(move, null, logic);
    }

    public <T> MapLogic mapClass(Class<T> clazz, BiPredicate<Entity, T> matcher,
            BiFunction<Entity, T, Reaction> logic) {
        Objects.requireNonNull(clazz, "move cannot be null");
        Objects.requireNonNull(logic, "logic cannot be null");
        
        Entry entry = new Entry((BiPredicate<Entity, Object>)matcher,
                (BiFunction<Entity, Object, Reaction>)logic);
        Entry mapping = map.get(clazz);
        if (mapping != null) {
            mapping.findTail().next = entry;
        } else {
            map.put(clazz, entry);
        }
        return this;
    }
    
    public <T> MapLogic mapClass(Class<T> clazz, BiFunction<Entity, T, Reaction> logic) {
        return mapClass(clazz, null, logic);
    }
    
    public MapLogic match(BiPredicate<Entity, Object> matcher,
            BiFunction<Entity, Object, Reaction> logic) {
        Objects.requireNonNull(logic, "logic cannot be null");
        
        Entry entry = new Entry(matcher, logic);
        if (tail == null) {
            //assert head == null;
            head = tail = entry;
        } else {
            tail.next = entry;
            tail = entry;
        }
        return this;
    }
}
