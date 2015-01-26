package gr.entij.util;

import java.util.*;

/**
 * Single linked based implementation of {@link Map} that does not allow
 * {@code null} keys.
 * This implementation is aimed for saving memory used by a large
 * number of very small maps. <p>
 * NOTE! The key and value of the entries of {@code SingleLinkedMaps} become
 * {@code null} where they are removed. (e.g. when the key is removed)
 * @param <K> the type of the keys
 * @param <V> the type of the values
 */
public class SingleLinkedMap<K, V> extends AbstractMap<K, V> {
    
    static final class SLEntry<K, V> implements Entry<K, V> {
        K key;
        V val;
        SLEntry <K, V> next;

        public SLEntry(K key, V val, SLEntry<K, V> next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
        
        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return val;
        }

        @Override
        public V setValue(V value) {
            V old = val;
            val = value;
            return old;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?,?> other = (Map.Entry<?,?>)obj;
            return Objects.equals(key, other.getKey())
                    && Objects.equals(val, other.getValue());
        }

        @Override
        public int hashCode() {
            return (key == null ? 0 : key.hashCode()) ^
                   (val == null ? 0 : val.hashCode());
        }
        
        void detach() {
            key = null;
            val = null;
            next = null;
        }

        void ensureNotDetached() throws ConcurrentModificationException {
            if (key == null) {
                throw new ConcurrentModificationException();
            }
        }
    }
    
    private SLEntry<K, V> head;
    private int size;

    public SingleLinkedMap() {}

    public SingleLinkedMap(K key, V val) {
        head = new SLEntry<>(key, val, null);
        size = 1;
    }
    
    @Override
    public V get(Object key) {
        checkKeyNonNull(key);
        SLEntry<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                return current.val;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        checkKeyNonNull(key);
        SLEntry<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        SLEntry<K, V> current = head;
        while (current != null) {
            if (Objects.equals(current.val, value)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    @Override
    public V put(K key, V value) {
        checkKeyNonNull(key);
//        checkValueNonNull(value);
        SLEntry<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                return current.setValue(value);
            }
            current = current.next;
        }
        head = new SLEntry<>(key, value, head);
        size++;
        return null;
    }

    @Override
    public V remove(Object key) {
        checkKeyNonNull(key);
        SLEntry<K, V> current = head;
        SLEntry<K, V> prev = null;
        while (current != null) {
            if (current.key.equals(key)) {
                V old = current.val;
                removeImpl(prev);
                return old;
            }
            prev = current;
            current = current.next;
        }
        return null;
    }

    @Override
    public void clear() {
        SLEntry<K, V> current = head;
        head = null;
        size = 0;
        while (current != null) {
            SLEntry<K, V> prev = current;
            current = current.next;
            prev.detach();
        }
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    SLEntry<K, V> current = null;
                    SLEntry<K, V> prev = null;
                    
                    @Override
                    public boolean hasNext() {
                        if (current != null) {
                            current.ensureNotDetached();
                            return current.next != null;
                        } else {
                            return head != null;
                        }
                    }

                    @Override
                    public Entry<K, V> next() {
                        SLEntry<K, V> next;
                        if (current != null) {
                            current.ensureNotDetached();
                            next = current.next;
                        } else if (head != null) {
                            next = head;
                        } else {
                            throw new NoSuchElementException("Map has no more elements");
                        }
                        next.ensureNotDetached();
                        prev = current;
                        current = next;
                        return next;
                    }

                    @Override
                    public void remove() {
                        if (current == null) {
                            throw new IllegalStateException("call to remove"
                                    + " requires a previous call to next");
                        }
                        current.ensureNotDetached();
                        if (current == prev) {
                            throw new IllegalStateException("remove already called");
                        }
                        if (prev != null) prev.ensureNotDetached();
                        removeImpl(prev);
                        current = prev;
                    }
                    
                };
            }

            @Override
            public int size() {
                return size;
            }

            @Override
            public boolean remove(Object o) {
                if (o instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry) o;
                    return SingleLinkedMap.this.remove(entry.getKey(), entry.getValue());
                }
                return false;
            }

            @Override
            public void clear() {
                SingleLinkedMap.this.clear();
            }
        };
    }
    
    void removeImpl(SLEntry<K, V> previous) {
        SLEntry<K, V> toRemove;
        if (previous == null) {
            //assert head != null: "head is null";
            toRemove = head;
            head = head.next;
        } else {
            //assert previous.next != null: "to be removed is null";
            toRemove = previous.next;
            previous.next = toRemove.next;
        }
        toRemove.detach();
        size--;
    }
    
    static void checkKeyNonNull(Object k) {
        if (k == null) {
            throw new NullPointerException("This map does not allow null keys");
        }
    }
    
//    static void checkValueNonNull(Object value) {
//        if (value == null) {
//            throw new NullPointerException("This map does not allow null values");
//        }
//    }
    
}
