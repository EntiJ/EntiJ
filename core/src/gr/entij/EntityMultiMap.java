package gr.entij;

import java.util.*;

/**
 * A data structure that maps arbitrary keys to multiple {@code Entities}.
 * To accomplish this, each key is associated to an {@link EntitySet}. So,
 * an entity may not be associated with the same key multiple times. <p>
 * An entity may be freely associated with multiple keys. <p>
 * When no entities are associated with a key, the corresponding {@code EntitySet}
 * is removed to save space. <p>
 * This implementation uses a {@link HashMap}.<br>
 * This implementation is not thread safe.
 * @param <K> the type of the keys
 * @see Entity
 * @see EntitySet
 */
public class EntityMultiMap<K>  {
//    TODO throw concurrent modification exceptions where apropriate
    
    private final HashMap<K, EntitySet> delegate;
    private int size = 0;
    private final Collection<Entity> all = new AllCollection();
    
    /**
     * Creates an empty map with the initial capacity
     * 16 and load factor 0.75.
     */
    public EntityMultiMap() {
        delegate = new HashMap<>();
    }

    /**
     * Creates an empty map with the specified initial
     * capacity and load factor.
     * @param initialCapacity the initial capacity
     * @param loadFactor the load factor
     */
    public EntityMultiMap(int initialCapacity, float loadFactor) {
        delegate = new HashMap<>(initialCapacity, loadFactor);
    }

    /**
     * Creates an empty map with the specified initial
     * capacity and load 0.75.
     * @param initialCapacity the initial capacity
     */
    public EntityMultiMap(int initialCapacity) {
        delegate = new HashMap<>(initialCapacity);
    }

    /**
     * Returns the entities associated with the given key as a readonly
     * {@code EntitySet} or {@code null} if there are no such entities. <p>
     * The returned set reflects any changes made on the actual mappings of the
     * given key, as long as it is not removed or replaced.
     * @param key the key to get its mappings
     * @return the entities of the the given key or {@code null}
     * @see EntitySet#isReadOnly()
     */
    public EntitySet get(K key) {
// TODO make this method return a real live view!
        EntitySet result = delegate.get(key);
        return result == null ? null : result.readOnly();
    }

    /**
     * Removes all the entities associated with the given key and returns them
     * as an {@code EntitySet}.
     * If no such entities are present, returns {@code null}.
     * @param key the key to clear its mappings
     * @return the entities previously associated with the given key or {@code null}
     */
    public EntitySet remove(K key) {
        final EntitySet removed = delegate.remove(key);
        if (removed != null) size -= removed.size();
        return removed;
    }
    
    /**
     * Adds the specified entities to mappings of the given key.
     * If an entity of those to added is associated with the given key, no action
     * is performed for that specific entity.
     * @param key the key to add the given entities
     * @param toAdd the entities to be added to key
     * @return the number of entities actually added
     */
    public int add(K key, Collection<? extends Entity> toAdd) {
        Objects.requireNonNull(toAdd, "Collection to add cannot be null");
        if (toAdd.isEmpty()) return 0;
        
        EntitySet set = delegate.get(key);
        if (set == null) {
            set = new EntitySet(toAdd);
            delegate.put(key, set);
            size += set.size();
            return set.size();
        }
        
        int addedCount = 0;
        for (Entity ent : toAdd) {
            if (set.add(ent)) {
                addedCount++;
                size++;
            }
        }
        return addedCount;
    }
    
    /**
     * Sets the mappings of the given key.
     * After this operation the given key will be associated with the given
     * entities and only them.
     * @param key the key to set its mappings
     * @param entities the new entities to be associated with key
     * @return the entities previously associated with the given key or
     * {@code null} if there are no such entities
     */
    public EntitySet set(K key, Collection<? extends Entity> entities) {
        Objects.requireNonNull(entities, "entities cannot be null");
        EntitySet old;
        if (entities.isEmpty()) {
            old = delegate.remove(key);
        } else {
            old = delegate.put(key, new EntitySet(entities));
        }
        size += entities.size() - (old != null ? old.size() : 0);
        return old;
    }
    
    /**
     * Removes the given entity from the mappings of given the key.
     * After this operation the entity will not be associated with key.
     * @param key the key
     * @param toRemove the entity to remove from key
     * @return {@code true} if the entity was previously contained in the key
     */
    public boolean removeFromKey(K key, Entity toRemove) {
        EntitySet set = delegate.get(key);
        if (set == null) {
            return false;
        } else {
            boolean removed = set.remove(toRemove);
            if (set.isEmpty()) {
                remove(key);
            }
            if (removed) {
                size--;
            }
            return removed;
        }
    }
    
    /**
     * Add the given entity to the mappings of given the key.
     * After this operation the entity will be associated with key.
     * @param key the key
     * @param toAdd the entity to be associated with key
     * @return {@code true} if the entity was not already contained in the key
     */
    public boolean addToKey(K key, Entity toAdd) {
        EntitySet set = delegate.get(key);
        if (set == null) {
            set = new EntitySet();
            delegate.put(key, set);
        }
        final boolean added = set.add(toAdd);
        if (added) size++;
        return added;
    }
    
    /**
     * Returns {@code true} if the given key is mapped to the given entity.
     * @param key the key
     * @param entity the entity
     * @return {@code true} if the given key is mapped to the given entity
     */
    public boolean containedInKey(K key, Entity entity) {
        EntitySet set = delegate.get(key);
        if (set == null) {
            return false;
        } else {
            return set.contains(entity);
        }
    }
    
    /**
     * Returns the number of entities associated with the given key.
     * @param key the key
     * @return the number of entities associated with the given key
     */
    public int countInKey(K key) {
        EntitySet set = delegate.get(key);
        return set == null ? 0 : set.size();
    }

    /**
     * Returns the number of mappings of this set. A mapping is an association
     * between a key and an entity. So, if an entity is associated with multiple
     * keys, it is counted multiple times.
     * @return the number of the mappings of this set
     */
    public int size() {
        return size;
    }
    
    /**
     * Returns a live view of all the entities associated with the keys of this
     * map. If an entity is associated with multiple keys, it is contained multiple
     * times in the returned collection.
     * @return a live view of all the entities of this map
     */
    public Collection<Entity> all() {
        return all;
    }
    
    final class AllCollection extends AbstractCollection<Entity> {

        final class Itr implements Iterator<Entity> {
            Iterator<EntitySet> outer = delegate.values().iterator();
            Iterator<Entity> current;
            
            @Override
            public boolean hasNext() {
                return outer.hasNext() || current != null && current.hasNext();
            }

            @Override
            public Entity next() {
                if (current == null || !current.hasNext())  {
                    current = outer.next().iterator();
                }
                return current.next();
            }
            
        }
        
        @Override
        public Iterator<Entity> iterator() {
            return new Itr();
        }

        @Override
        public int size() {
            return size;
        }
    
    }
    
}
