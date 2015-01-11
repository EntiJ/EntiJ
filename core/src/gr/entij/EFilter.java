package gr.entij;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Provides some common {@code Entity Predicates}.
 * @see java.util.function.Predicate
 */
public interface EFilter extends Predicate<Entity> {
    
    /**
     * Returns a predicate that matches only the entities in the given state.
     * @param state the state
     * @return a predicate that matches only the entities in the given state
     */
    public static EFilter inState(long state) {
        return e -> e.getState() == state;
    }
    
    /**
     * Returns a predicate that matches only the entities that are in the same
     * state as the given one. This is not equivalent to {@code inState(en.getState())}
     * because the entities that match will change if the state of the given 
     * entity changes.
     * @param en the entity 
     * @return a predicate that matches only the entities that are in the same
     * state as the given one
     */
    public static EFilter inStateOf(Entity en) {
        return e -> e.getState() == en.getState();
    }
    
    /**
     * Returns a predicate that matches only the entities in at given position.
     * @param posit the position
     * @return a predicate that matches only the entities in at given position
     */
    public static EFilter at(long posit) {
        return e -> e.getPosit() == posit;
    }
    
    /**
     * Returns a predicate that matches only the entities that are at the same
     * position as the given one. This is not equivalent to {@code at(en.getPosit())}
     * because the entities that match will change if the position of the given 
     * entity changes.
     * @param en the entity 
     * @return a predicate that matches only the entities that are at the same
     * position as the given one
     */
    public static EFilter atPositOf(Entity en) {
        return e -> e.getPosit() == en.getPosit();
    }
    
    /**
     * Returns a predicate that matches only the entities with the given name.
     * @param name the name
     * @return a predicate that matches only the entities with the given name
     */
    public static EFilter named(String name) {
        return e -> Objects.equals(e.getName(), name);
    }
    
}
