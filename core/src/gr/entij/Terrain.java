package gr.entij;

import gr.entij.event.*;
import java.util.*;
import java.util.function.Consumer;
import static gr.entij.event.EntityEvent.Type;
import java.util.function.Predicate;

/**
 * Container for storing and organizing entities. Automatically indexes the
 * entities by their name, state and position in real time. Also, it supports
 * monitoring additions and removals of entities by adding appropriate listeners.
 * <p> Note: when an entity is destroyed, it is removed from all {@code Terrains}
 * that it was added.
 */
public class Terrain extends Entity {
    private static final EntitySet EMPTY_SET = new EntitySet().readOnly();
    
    // TODO make indexing customizable. For now, entities are indexed by:
    // name, state and posit using hashing.
    // It would be good to support:
    //  - indexing by specific properties,
    //  - disabling an index to save resources
    //  - or using binary indices instead of hashing
    
    // TODO take care of possible concurrency issues
    
    private final EntityMultiMap<Long> entitiesByPosit = new EntityMultiMap<>();
    private final EntityMultiMap<Long> entitiesByState = new EntityMultiMap<>();
    private final EntityMultiMap<String> entitiesByName = new EntityMultiMap<>();
    
    private final List<Predicate<? super EntityEvent>> addRemoveListeners = new LinkedList<>();
    
    private final Consumer<MoveEvent> terrainMoveListener = (MoveEvent e) -> {
        if (e.previousPosit != e.nextPosit) {
            entitiesByPosit.removeFromKey(e.previousPosit, e.source);
        }
        entitiesByPosit.addToKey(e.nextPosit, e.source);
    };
    
    private final Consumer<StateEvent> terrainStateListener = (StateEvent e) -> {
        if (e.previousState != e.nextState) {
            entitiesByState.removeFromKey(e.previousState, e.source);
        }
        entitiesByState.addToKey(e.nextState, e.source);
    };
    
    private final Predicate<EntityEvent> entityListener = (EntityEvent e) -> {
        if (e.type == EntityEvent.Type.DESTROYED) {
            removeImpl(e.source, true);
            return false;
        }
        return true;
    };

    /**
     * Creates an empty Terrain.
     */
    public Terrain() {
    }

    /**
     * Creates an empty Terrain with the given name.
     * @param name the name of the new Terrain
     */
    public Terrain(String name) {
        super(name);
    }
    
    /**
     * Returns the number of Entities of this Terrain.
     * @return the number of Entities of this Terrain
     */
    public int getEntityCount() {
        return entitiesByName.size();
    }

    /**
     * Adds the given entity. Generates an {@link EntityEvent} of type
     * {@link Type#ADDED ADDED}.
     * @param toAdd the entity to be added
     * @see #addAddRemoveListener(java.util.function.Consumer) 
     */
    public void add(Entity toAdd) {
        toAdd.addMoveListener(terrainMoveListener);
        toAdd.addStateListener(terrainStateListener);
        toAdd.addEntityListenerRemovable(entityListener);
        entitiesByName.addToKey(toAdd.getName(), toAdd);
        entitiesByPosit.addToKey(toAdd.getPosit(), toAdd);
        entitiesByState.addToKey(toAdd.getState(), toAdd);
        onAddRemove(toAdd, EntityEvent.Type.ADDED);
    }
    
    /**
     * Removes the given entity. Generates an {@link EntityEvent} of type
     * {@link Type#REMOVED REMOVED}.
     * @param toRemove the entity to be removed
     * @see #addAddRemoveListener(java.util.function.Consumer) 
     */
    public void remove(Entity toRemove) {
        removeImpl(toRemove, false);
    }
    
    private void removeImpl(Entity toRemove, boolean ofDestroy) {
        toRemove.removeMoveListener(terrainMoveListener);
        toRemove.removeStateListener(terrainStateListener);
        entitiesByName.removeFromKey(toRemove.getName(), toRemove);
        entitiesByPosit.removeFromKey(toRemove.getPosit(), toRemove);
        entitiesByState.removeFromKey(toRemove.getState(), toRemove);
        onAddRemove(toRemove, ofDestroy ? EntityEvent.Type.DESTROY_REMOVED : EntityEvent.Type.REMOVED);
    }
    
    /**
     * Returns the entities with the given name. <p>
     * This method runs in O(1) time and does not make any memory allocations. <p>
     * The contents of the returned set may change as if the contents of this Terrain
     * change but, currently, there is no guaranty that a set returned by this 
     * method will always have the same contents with a set returned by this method
     * at a later time.
     * @param name the name of the entities to return
     * @return the entities with the given name
     */
    public EntitySet getByName(String name) {
        EntitySet result = entitiesByName.get(name);
        return result == null ? EMPTY_SET : result;
    }
    
    /**
     * Returns the entities at the given position. <p>
     * This method runs in O(1) time and does not make any memory allocations. <p>
     * The contents of the returned set may change as if the contents of this Terrain
     * change but, currently, there is no guaranty that a set returned by this 
     * method will always have the same contents with a set returned by this method
     * at a later time.
     * @param posit the position of the entities to return
     * @return the entities at the given position
     */
    public EntitySet getByPosit(long posit) {
        EntitySet result = entitiesByPosit.get(posit);
        return result == null ? EMPTY_SET : result;
    }
    
    /**
     * Returns the entities in the given state. <p>
     * This method runs in O(1) time and does not make any memory allocations. <p>
     * The contents of the returned set may change as if the contents of this Terrain
     * change but, currently, there is no guaranty that a set returned by this 
     * method will always have the same contents with a set returned by this method
     * at a later time.
     * @param state the state of the entities to return
     * @return the entities in the given state
     */
    public EntitySet getByState(long state) {
        EntitySet result = entitiesByState.get(state);
        return result == null ? EMPTY_SET : result;
    }
    
    /**
     * Returns all the entities; the contents of the returned collection will
     * automatically change, reflecting additions and removals of entities on
     * this terrain.
     * @return a live view of all the entities
     */
    public Collection<Entity> getAll() {
        return entitiesByName.all();
    }
    
    /**
     * Adds the given listener for entity additions and removals. <br>
     * NOTE: the source (see {@link Event#source}) of the events that will be
     * handled by these listeners will be the entity that actually got added or
     * removed and not this terrain. <p>
     * NOTE: if an entity is removed due to destruction (see {@link Entity#destroy})
     * an {@code EntityEvent} of type {@code DESTROY_REMOVED} is generated while
     * if it is removed by a regular call to {@link #remove} the event's type
     * will be {@code REMOVED}.
     * @param toAdd
     * @see #add(gr.entij.Entity)
     * @see #remove(gr.entij.Entity) 
     * @see EntityEvent
     */
    public void addAddRemoveListener(Consumer<? super EntityEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
        addRemoveListeners.add(new RemovableListener<>(toAdd));
    }
    
    /**
     * Same as {@link #addAddRemoveListener(java.util.function.Consumer)} but the
     * added listener will be retained as long as it returns {@code true}.
     * The listener will be removed the first time it returns {@code false}
     * and the remove operation will take O(1) time.
     * <p> NOTE: the listener will not be removed it throw an exception.
     * @param toAdd
     * @see #add(gr.entij.Entity)
     * @see #remove(gr.entij.Entity) 
     * @see EntityEvent
     */
    public void addAddRemoveListenerRemovable(Predicate<? super EntityEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
        addRemoveListeners.add(toAdd);
    }
    
    /**
     * Removes the specified listener for entity additions and removals.
     * @param toRemove the listener to remove
     */
    public void removeAddRemoveListener(Consumer<? super EntityEvent> toRemove) {
        removeListener(addRemoveListeners, toRemove);
    }
    
    private void onAddRemove(Entity ent, EntityEvent.Type type) {
        fireEvent(addRemoveListeners, new EntityEvent(ent, type));
    }
}
