package gr.entij.event;

import gr.entij.*;

/**
 * An {@code EntityEvent} is generated when an event regarding the lifecycle
 * of the entity occurs. The {@linkplain EntityEvent.Type type} of the
 * {@code EntityEvent} is shows the reason that event was generated.
 */
public class EntityEvent extends Event {
    
    /**
     * The type of an {@code EntityEvent} shows the reason that event was
     * generated.
     */
    public static enum Type {

        /**
         * Generated when the destroy method of an entity is called.
         * @see Entity#destroy
         * @see Entity#addEntityListener(java.util.function.Consumer) 
         */
        DESTROYED,

        /**
         * Generated when an entity is added to a {@code Terrain}.
         * To capture this kind of events you must add a listener to that
         * {@code Terrain} via {@link Terrain#addAddRemoveListener Terrain.addAddRemoveListener}
         * @see Terrain#add(gr.entij.Entity)
         * @see Terrain#addAddRemoveListener(java.util.function.Consumer) 
         */
        ADDED,

        /**
         * Generated when an entity is removed from a {@code Terrain}.
         * To capture this kind of events you must add a listener to that
         * {@code Terrain} via {@link Terrain#addAddRemoveListener Terrain.addAddRemoveListener}
         * @see Terrain#remove(gr.entij.Entity)
         * @see Terrain#addAddRemoveListener(java.util.function.Consumer) 
         */
        REMOVED,

        /**
         * Generated when an entity is removed from a {@code Terrain} because 
         * it was destroyed.
         * To capture this kind of events you must add a listener to that
         * {@code Terrain} via {@link Terrain#addAddRemoveListener Terrain.addAddRemoveListener}
         * @see Terrain#addAddRemoveListener(java.util.function.Consumer) 
         */
        DESTROY_REMOVED
    }
    
    /**
     * The type of this {@code EntityEvent} shows the reason this event was
     * generated.
     */
    public final Type type;

    public EntityEvent(Entity source, Type type) {
        super(source);
        this.type = type;
    }
}
