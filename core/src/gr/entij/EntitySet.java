package gr.entij;

import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toCollection;
import java.util.stream.Stream;


/**
 * A {@code HashSet} of {@code Entities} with additional functionality.
 * @see Entity
 * @see EntityMultiMap
 */
public class EntitySet extends HashSet<Entity> {
    
    /** Creates an empty EntitySet.  */
    public EntitySet() {
    }

    /**
     * Creates a EntitySet that contains the given {@code Entities}.
     *
     * @param entities The Entities to be contained in the new set
     */
    public EntitySet(Entity... entities) {
        this.addAll(Arrays.asList(entities));
    }
    
    /**
     * Creates an {@code EntitySet} that is a copy of the given {@code Set}.
     * @param original the {@code Set} to copy
     */
    public EntitySet(Collection<? extends Entity> original) {
        super(original);
    }

    /**
     * Adds the specified Entity to this set.
     *
     * @param f The Entity to be added to this set
     * @return true if this set did not already contain the specified Entity
     * @throws NullPointerException if the Entity to be added is null
     */
    @Override
    public boolean add(Entity f) {
        if (f == null) {
            throw new NullPointerException("EntitySet Can Not"
                    + " Contain null");
        }
        return super.add(f);
    }
    
    /**
     * Returns {@code true} if this set is readonly.
     * If a set is readonly, no elements can be added or removed.
     * @return {@code true} if this set is readonly
     */
    public boolean isReadOnly() {
        return false;
    }
    
    /**
     * Returns a readonly live view of this set.
     * The returned set will always have the same contents as this set but it
     * will disallow add and remove operations.
     * @return a readonly live view of this set
     */
    public EntitySet readOnly() {
        return new ReadOnlySet();
    }

    /**
     * Returns a new {@code EntitySet} with the elements contained both in this
     * set and the given collection.
     * @param s the collection for which the intersection with this will be
     * returned
     * @return the intersection <em>this &cap; s</em>
     */
    public EntitySet and(Collection<? extends Entity> s) {
        EntitySet result;
        (result = (EntitySet) this.clone()).retainAll(s);
        return result;
    }

    /**
     * Returns a new {@code EntitySet} with all the elements contained in this
     * set or the given collection.
     * @param s the collection for which the union with this will be returned
     * @return the union <em>this &cup; s</em>
     */
    public EntitySet or(Collection<? extends Entity> s) {
        EntitySet result;
        (result = (EntitySet) this.clone()).addAll(s);
        return result;
    }
    
    /**
     * Returns a new {@code EntitySet} with the elements of this set that are
     * not present in the given collection.
     * @param s 
     * @return the subtraction <em>this - s</em>
     */
    public EntitySet not(Collection<? extends Entity> s) {
        EntitySet result;
        (result = (EntitySet) this.clone()).removeAll(s);
        return result;
    }
    
    /**
     * Returns an element of this set. <br> The choice is arbitrary.
     * @return an element of this set or null if this isEmpty
     */
    public Optional<Entity> any() {
        return stream().findAny();
    }
    
    /**
     * Returns a new {@code EntitySet} with the entities that match the given
     * predicate.
     * @param pred 
     * @return the subset <em>s = {e &isin; this | pred.test(e)=true}</em>
     * @see EFilter
     */
    public EntitySet filter(Predicate<? super Entity> pred) {
        return filterImpl(pred).collect(toCollection(EntitySet::new));
    }
    
    /**
     * Returns {@code true} if there exists an entity in this set that
     * matches the given predicate.
     * @param pred
     * @return {@code true} if <em>&exist; e &isin; this : pred.test(e)=true</em>
     * @see EFilter
     */
    public boolean hasAny(Predicate<? super Entity> pred) {
        return parallelStream().anyMatch(pred);
    }
    
    /**
     * Returns the  number of entities that match the given predicate.
     * @param pred
     * @return the  number of entities that match the given predicate
     * @see EFilter
     */
    public int count(Predicate<? super Entity> pred) {
        return (int) filterImpl(pred).count();
    }
    
    /**
     * Returns any entity that matches the given predicate.
     * @param pred 
     * @return returns any entity that matches the given predicate
     * @see EFilter
     */
    public Optional<Entity> any(Predicate<? super Entity> pred) {
        return filterImpl(pred).findAny();
    }
    
    private Stream<Entity> filterImpl(Predicate<? super Entity> pred) {
        return parallelStream().filter(pred);
    }
    
    private final class ReadOnlySet extends EntitySet {

        @Override public int size() {
            return EntitySet.this.size();
        }

        @Override public boolean isEmpty() {
            return EntitySet.this.isEmpty();
        }

        @Override public boolean contains(Object o) {
            return EntitySet.this.contains(o);
        }

        @Override public Object clone() {
            return EntitySet.this.clone();
        }

        @Override public boolean isReadOnly() {
            return true; 
        }
        
        @Override public EntitySet readOnly() {
            return this;
        }

        @Override public EntitySet and(Collection<? extends Entity> s) {
            return EntitySet.this.and(s);
        }

        @Override public EntitySet or(Collection<? extends Entity> s) {
            return EntitySet.this.or(s);
        }

        @Override public EntitySet not(Collection<? extends Entity> s) {
            return EntitySet.this.not(s);
        }

        @Override
        public boolean equals(Object obj) {
            return EntitySet.this.equals(obj);
        }

        @Override public int hashCode() {
            return EntitySet.this.hashCode();
        }

        @Override public Optional<Entity>  any() {
            return EntitySet.this.any();
        }

        @Override
        public EntitySet filter(Predicate<? super Entity> pred) {
            return EntitySet.this.filter(pred);
        }

        @Override
        public boolean hasAny(Predicate<? super Entity> pred) {
            return EntitySet.this.hasAny(pred);
        }

        @Override
        public int count(Predicate<? super Entity> pred) {
            return EntitySet.this.count(pred);
        }

        @Override
        public Optional<Entity> any(Predicate<? super Entity> pred) {
            return EntitySet.this.any(pred);
        }
        
        @Override public Stream<Entity> stream() {
            return EntitySet.this.stream();
        }

        @Override public Stream<Entity> parallelStream() {
            return EntitySet.this.parallelStream();
        }

        @Override public Spliterator<Entity> spliterator() {
            return EntitySet.this.spliterator();
        }
        
        @Override public boolean add(Entity f) {
            throw new IllegalStateException("This set is read only");
        }

        @Override public Iterator<Entity> iterator() {
            return Collections.unmodifiableSet(EntitySet.this).iterator();
        }

        @Override public boolean remove(Object o) {
            throw new IllegalStateException("This set is read only");
        }

        @Override public void clear() {
            throw new IllegalStateException("This set is read only");
        }
    }
    
}
