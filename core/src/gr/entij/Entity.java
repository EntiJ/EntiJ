package gr.entij;
import gr.entij.event.*;

import java.util.*;
import java.util.function.Consumer;
import static gr.entij.event.EntityEvent.Type;
import gr.entij.function_records.HashFunctionRecord;
import java.util.function.Predicate;

/**
 * Each entity has the following properties:
 * <ul>
 *   <li>Name,</li>
 *   <li>Position,</li>
 *   <li>State,</li>
 *   <li>A collection of arbitrary properties (name-value fields).</li>
 * </ul>
 * Entities, also, provide the ability to monitor changes to any of the above properties
 * (except for the name which does not change) by adding appropriate listeners. <p>
 * Finally, an entity can have its own {@linkplain Logic logic} that defines its
 * behavior upon accepting different inputs (see {@link Entity#move}).
 */
public class Entity {
    // TODO take care of possible concurrency issues
    // Proposal: implement async messaging system
    
    // TODO make logics removable
    
    static class RemovableListener<T> implements Predicate<T> {
        final Consumer<T> action;

        public RemovableListener(Consumer<T> action) {
            this.action = action;
        }
        
        @Override
        public boolean test(T t) {
            action.accept(t);
            return true;
        }
        
    }
    
    static class Node<T> {
        T data;
        Node<T> next;

        public Node(T data, Node<T> next) {
            this.data = data;
            this.next = next;
        }
        
        void destroy() {
            data = null;
            next = null;
        }
        
        void ensureNotDestoyed() throws ConcurrentModificationException {
            if (data == null) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    private final String name;
    private long posit;
    private long state;
    
    private Node<Logic> logics;
    private Node<Predicate<? super PositEvent>> positListeners; 
    private Node<Predicate<? super StateEvent>> stateListeners ;
    private Node<Predicate<? super PropertyEvent>> propertyListeners;
    private Node<Predicate<? super EntityEvent>> entityListeners;
    
    /**
     * List used to store the child-entities.
     * The meaning of this list is up to the application.
     */
    public List<Entity> children = null;
    private Map<String, Object> properties;
    private FunctionRecord functionRecord = new HashFunctionRecord();
    
    /**
     * Creates an {@code Entity} with {@code null} as name and 0 as posit
     * and state.
     */
    public Entity() {
        name = null;
    }
    
    /**
     * Creates an {@code Entity} with the given name and 0 as posit
     * and state.
     * @param name the name of the new entity
     */
    public Entity(String name) {
        this.name = name;
    }

    /**
     * Creates an {@code Entity} with the given name, posit and state.
     * @param name the name of the new entity
     * @param posit the initial posit of the new entity
     * @param state the initial state of the new entity
     */
    public Entity(String name, long posit, long state) {
        this.name = name;
        this.posit = posit;
        this.state = state;
    }

    /**
     * Destroys this entity; notifies all {@link EntityEvent} listeners with a
     * {@link Type#DESTROYED DESTROYED} event.
     * @see Type#DESTROYED
     * @see #addEntityListener
     */
    public void destroy() {
        EntityEvent destroyEvent = new EntityEvent(this, EntityEvent.Type.DESTROYED);
        entityListeners = fireEvent(entityListeners, destroyEvent);
    }
    
    /**
     * Returns the name of this entity.
     * The name cannot change after construction.
     * @return the name of this entity
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current position of this entity.
     * @return the current position of this entity
     */
    public long getPosit() {
        return posit;
    }

    /**
     * Sets the position of this entity.
     * Notifies all {@link PositEvent} listeners that the position has changed
     * (by a {@code null} move).
     * @param posit the new position
     * @see PositEvent
     * @see #addPositListener
     */
    public void setPosit(long posit) {
        setPositImpl(posit, null);
    }
    
    private void setPositImpl(long posit, Object move) {
        PositEvent e = new PositEvent(this, move, this.posit, posit);
        this.posit = posit;
        positListeners = fireEvent(positListeners, e);
    }

    /**
     * Returns the current state of this entity.
     * @return the current state of this entity
     */
    public long getState() {
        return state;
    }

    /**
     * Sets the state of this entity.
     * Notifies all {@link StateEvent} listeners.
     * @param state the new state
     * @see StateEvent
     * @see #addStateListener
     */
    public void setState(long state) {
        setStateImpl(state, null);
    }
    
    private void setStateImpl(long state, Object move) {
        StateEvent e = new StateEvent(this, move, this.state, state);
        this.state = state;
        stateListeners = fireEvent(stateListeners, e);
    }

    /**
     * Adds this {@link Logic} component to this entity at the front.
     * @param logic the new {@link Logic} component for this entity
     * @throws NullPointerException if {@code logic} is {@code nul}
     * @see Logic
     * @see #move
     */
    public void addLogic(Logic logic) throws NullPointerException {
        Objects.requireNonNull(logic, "logic cannot be null");
        this.logics = new Node<>(logic, this.logics);
    }

//    /**
//     * Returns the list used to store the child-entities.
//     * The meaning of the returned list is up to the application.
//     * @return the actual list used to store th child-entities
//     */
//    public List<Entity> children() {
//        return children;
//    }
  
    /**
     * Performs the given move. <br>
     * Questions all {@linkplain Logic logics} of this entity for a
     * {@linkplain MoveReaction reaction} to the given move and performs the
     * steps of any non-null reaction. <br>
     * If a logic consumes the move (see {@link MoveReaction#consume}) the
     * remaining logics are not questioned. <p>
     * If there is no <em>logic</em> in this entity, nothing happens.
     * @param move the move to perform; cannot be null
     * @return the reaction that consumed the {@code move}
     * or {@code null} if the move was no such reaction
     * @throws NullPointerException if {@code move} is {@code null}
     * @see #addLogic(gr.entij.Logic)
     * @see MoveReaction
     */
    public MoveReaction move(Object move) throws NullPointerException {
        Objects.requireNonNull(move, "move cannot be null");
        for (Node<Logic> logic = logics; logic != null; logic = logic.next) {
            MoveReaction reaction = logic.data.reaction(this, move);
            if (reaction != null) {
                applyMoveReaction(reaction, move);
                if (reaction.consume) {
                    return reaction;
                }
            }
        }
        
        return null;
    }
    
    private void applyMoveReaction(MoveReaction reaction, Object move) {
        if (reaction.nextPosit != null) {
            setPositImpl(reaction.nextPosit, move);
        }
        if (reaction.nextState != null) {
            setStateImpl(reaction.nextState, move);
        }
        if (reaction.nextPropValues != null) {
            putPropsImpl(reaction.nextPropValues, move);
        }
        if (reaction.andThenMoves != null) {
            for (MoveReaction.AndThen andThen : reaction.andThenMoves) {
                Object andThenMove = andThen.move;
                if (andThen.target != null) {
                    andThen.target.move(andThenMove);
                } else {
                    //assert andThen.targets != null;
                    andThen.targets.forEach((Entity ent) -> ent.move(andThenMove));
                }
            }
        }
    }
    
// Listener Management
    
    /**
     * Adds the given {@link PositEvent} listener. <br>
     * The added listener will be notified each time the position changes,
     * even if the new position equals the old position. <p>
     * Some methods that may generate move events are: {@link #setPosit} and
     * {@link #move}.
     * @param toAdd the listener to be added
     * @see PositEvent
     */
    public void addPositListener(Consumer<? super PositEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
        positListeners = new Node<>(new RemovableListener<>(toAdd), positListeners);
    }
        
    /**
     * Same as {@link #addPositListener(java.util.function.Consumer)} but the
     * added listener will be retained as long as it returns {@code true}.
     * The listener will be removed the first time it returns {@code false}
     * and the remove operation will take O(1) time.
     * <p> NOTE: the listener will not be removed it throw an exception.
     * @param toAdd the listener to be added
     * @see PositEvent
     */
    public void addPositListenerRemovable(Predicate<? super PositEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
//        moveListeners.add(toAdd);
        positListeners = new Node<>(toAdd, positListeners);
    }
    
    /**
     * Removes the specified {@link PositEvent} listener.
     * @param toRemove the listener to be removed
     */
    public void removePositListener(Consumer<? super PositEvent> toRemove) {
        positListeners = removeListener(positListeners, toRemove);
    }
    
    /**
     * Adds the given {@link StateEvent} listener. <br>
     * The added listener will be notified each time the state changes,
     * even if the new state equals the old state. <p>
     * Some methods that may generate state events are: {@link #setState}.
     * @param toAdd the listener to be added
     * @see StateEvent
     */
    public void addStateListener(Consumer<? super StateEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
        stateListeners = new Node<>(new RemovableListener<>(toAdd), stateListeners);
    }
      
    /**
     * Same as {@link #addStateListener(java.util.function.Consumer)} but the
     * added listener will be retained as long as it returns {@code true}.
     * The listener will be removed the first time it returns {@code false}
     * and the remove operation will take O(1) time.
     * <p> NOTE: the listener will not be removed it throw an exception.
     * @param toAdd the listener to be added
     * @see StateEvent
     */
    public void addStateListenerRemovable(Predicate<? super StateEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
//        stateListeners.add(toAdd);
        stateListeners = new Node<>(toAdd, stateListeners);
    }
    
    /**
     * Removes the specified {@link StateEvent} listener.
     * @param toRemove the listener to be removed
     */
    public void removeStateListener(Consumer<StateEvent> toRemove) {
        stateListeners = removeListener(stateListeners, toRemove);
    }
    
    /**
     * Adds the given {@link PropertyEvent} listener. <br>
     * The added listener will be notified each time one or multiple
     * properties change. A property is considered changed even if the
     * new value equals the old value. <p>
     * Some methods that may generate property events are:
     * <ul>
     *   <li>with a single property changed: {@link #setProp}</li>
     *   <li>with a multiple properties changed: {@link #setProps setProps(java.util.Map)}
     *       and {@link #putProps putProps(java.util.Map)}.</li>
     * </ul>
     * @param toAdd the listener to be added
     * @see PropertyEvent
     */
    public void addPropertyListener(Consumer<? super PropertyEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
        propertyListeners = new Node<>(new RemovableListener<>(toAdd), propertyListeners);
    }
    
    /**
     * Same as {@link #addPropertyListener(java.util.function.Consumer)} but the
     * added listener will be retained as long as it returns {@code true}.
     * The listener will be removed the first time it returns {@code false}
     * and the remove operation will take O(1) time.
     * <p> NOTE: the listener will not be removed it throw an exception.
     * @param toAdd the listener to be added
     * @see PropertyEvent
     */
    public void addPropertyListenerRemovable(Predicate<? super PropertyEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
//        propertyListeners.add(toAdd);
        propertyListeners = new Node<>(toAdd, propertyListeners);
    }
    
    /**
     * Removes the specified {@link PropertyEvent} listener.
     * @param toRemove the listener to be removed
     */
    public void removePropertyListener(Consumer<? super PropertyEvent> toRemove) {
        propertyListeners = removeListener(propertyListeners, toRemove);
    }
       
    /**
     * Adds the given {@link EntityEvent} listener. <br>
     * The added listener will be notified for events regarding the lifecycle
     * of the entity. <p>
     * Some methods that may generate entity events are: {@link #destroy}.
     * @param toAdd the listener to be added
     * @see EntityEvent
     */ 
    public void addEntityListener(Consumer<? super EntityEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
        entityListeners = new Node<>(new RemovableListener<>(toAdd), entityListeners);
    }
             
    /**
     * Same as {@link #addEntityListener(java.util.function.Consumer)} but the
     * added listener will be retained as long as it returns {@code true}.
     * The listener will be removed the first time it returns {@code false}
     * and the remove operation will take O(1) time.
     * <p> NOTE: the listener will not be removed it throw an exception.
     * @param toAdd the listener to be added
     * @see EntityEvent
     */ 
    public void addEntityListenerRemovable(Predicate<? super EntityEvent> toAdd) {
        Objects.requireNonNull(toAdd, "listener cannot be null");
//        entityListeners.add(toAdd);
        entityListeners = new Node<>(toAdd, entityListeners);
    }
    
    /**
     * Removes the specified {@link EntityEvent} listener.
     * @param toRemove the listener to be removed
     */
    public void removeEntityListener(Consumer<? super EntityEvent> toRemove) {
        entityListeners = removeListener(entityListeners, toRemove);
    }
    
    /**
     * NOTE! always replace the reference to the given listenerList with the
     * returned value!
     * E.g. always call this method like that: {@code 
     * listenerList = removeListener(listenerList, listenerToRemove);
     * }
     * @param <T> the of events these listeners handle
     * @param listenerList the head of the listener list
     * @param toRemove the listener to be removed
     * @return the new head of the listener list
     */
    static  <T> Node<Predicate<? super T>> removeListener(Node<Predicate<? super T>> listenerList, Consumer<? super T> toRemove) {
        if (listenerList == null) return null;
        Node<Predicate<? super T>> current = listenerList;
        Node<Predicate<? super T>> previous = null;
        
        while (current != null) {
            if (current.data instanceof RemovableListener && ((RemovableListener) current.data).action == toRemove) {
                if (previous != null) {
                    previous.next = current.next;
                } else {
                    listenerList = current.next;
                }
                current.destroy();
                return listenerList;
            }
            previous = current;
            current = current.next;
        }
        
        return listenerList;
    }
    
    /**
     * NOTE! always replace the reference to the given listenerList with the
     * returned value!
     * E.g. always call this method like that: {@code 
     * listenerList = fireEvent(listenerList, event);
     * }
     * @param <T> the type of event to be fired
     * @param listenerList the head of the listeners to be notified
     * @param event the event to be fired
     * @return the new head of the notified listeners; 
     */
    static <T> Node<Predicate<? super T>> fireEvent(Node<Predicate<? super T>> listenerList, T event) {
        if (listenerList == null) return null;
        Node<Predicate<? super T>> current = listenerList;
        Node<Predicate<? super T>> previous = null;
        
        while (current != null) {
            boolean removed = false;
            try {
                current.ensureNotDestoyed();
                if (!current.data.test(event)) {
                    if (previous != null) {
                        previous.ensureNotDestoyed();
                        previous.next = current.next;
                        current.destroy();
                        current = previous.next;
                        removed = true;
                    } else {
                        listenerList = current.next;
                        current.destroy();
                        current = listenerList;
                        removed = true;
                    }
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
            if (!removed) {
                previous = current;
                current = current.next;
            }
        }
        return listenerList;
    }
    
// Property Management
    
    /**
     * Returns the value of the specified property or {@code null} if the
     * property is not present. <p>
     * To check if the property is present use {@link #hasProp}.
     * @param <T> the expected type of the value of the property
     * @param propertyName the name of the property
     * @return the value of the specified property or {@code null} if the
     * property is not present
     * @throws ClassCastException if the value of the specified property
     * is not of type assignable to {@code T}
     */
    @SuppressWarnings("unchecked")
    public <T> T getProp(String propertyName) throws ClassCastException {
        ensurePropertiesInitialized();
        return (T) properties.get(propertyName);
    }
    
    /**
     * Sets the value of the specified property. <br>
     * Notifies all {@link PropertyEvent} listeners for the property value
     * change, even if the new value equals the old value.
     * @param propertyName the name of the property to set
     * @param propertyValue the value to be set for the property
     * @see #addPropertyListener
     */
    public void setProp(String propertyName, Object propertyValue) {
        ensurePropertiesInitialized();
        Map<String, Object> oldValues = Collections.singletonMap(
                propertyName, properties.put(propertyName, propertyValue));
        dispatchPropertyEvent(oldValues, null);
    }
    
    /**
     * Removes the specified property.
     * @param propNameToRemove the name of the property to remove
     */
    public void removeProp(String propNameToRemove) {
        if (properties != null && properties.containsKey(propNameToRemove)) {
            Map<String, Object> oldValues = Collections.singletonMap(
                propNameToRemove, properties.remove(propNameToRemove));
            dispatchPropertyEvent(oldValues, null);
        }
        
    }
    
    /**
     * Returns {@code true} if the specified property is present. <p>
     * Note that the value of the property may be {@code null}.
     * @param propertyName the name of the property
     * @return {@code true} if the specified property is present
     */
    public boolean hasProp(String propertyName) {
        return properties != null && properties.containsKey(propertyName);
    }
    
    /**
     * Sets all the key-value property pairs.
     * Any key not contained in the given map will be discarded. <p>
     * Generates a singe {@link PropertyEvent}.
     * @param props the new properties
     * @see #addPropertyListener
     */
    public void setProps(Map<String, ? extends Object> props) {
        if (propertyListeners == null) {
            properties = new HashMap<>(props); return;
        }
        
        Map<String, Object> oldValues = properties == null ? Collections.EMPTY_MAP : properties;
        properties = new HashMap<>(props);
        if (oldValues.isEmpty()) {
            oldValues.putAll(props);
            oldValues.entrySet().stream().forEach(ent -> ent.setValue(null));
        } else {
            properties.entrySet().stream()
                    .filter(ent -> !oldValues.containsKey(ent.getKey()))
                    .forEach(ent -> oldValues.put(ent.getKey(), null));
        }
        dispatchPropertyEvent(oldValues, null);
    }
    
    /**
     * Add the given key-value property pairs.
     * Is a key already exists, its value will be replaced. <p>
     * Generates a singe {@link PropertyEvent}.
     * @param props the key-value pairs to be added
     * @see #addPropertyListener
     */
    public void putProps(Map<String, ? extends Object> props) {
        putPropsImpl(props, null);
    }
    
    private void putPropsImpl(Map<String, ? extends Object> props, Object move) {
        ensurePropertiesInitialized();
        if (propertyListeners == null) {
            properties.putAll(props); return; 
        }

        Map<String, Object> oldValues = new HashMap<>(props);
        oldValues.entrySet().stream().forEach(ent -> {
            ent.setValue(properties.get(ent.getKey()));
        });
        properties.putAll(props);
        dispatchPropertyEvent(oldValues, move);
    }
    
    private void ensurePropertiesInitialized() {
        if (properties == null) {
            properties = new HashMap<>(1);
        }
    }
    
    private void dispatchPropertyEvent(Map<String, Object> oldValues, Object move) {
        propertyListeners = fireEvent(propertyListeners,
                new PropertyEvent(this, move, oldValues));
    }

// function management

    public FunctionRecord getFunctionRecord() {
        return functionRecord;
    }

    public void setFunctionRecord(FunctionRecord functionRecord) {
        this.functionRecord = functionRecord.child();
    }
    
    public <T> T func(String func, Object... args)
            throws NoSuchElementException, ClassCastException {
        return functionRecord.apply(this, func, args);
    }
    
    
    @Override
    public String toString() {
        return super.toString()+ " name: "+(name==null?"null":"\""+name+"\"")
                +", posit: "+posit+", state: "+state;
    }
}
