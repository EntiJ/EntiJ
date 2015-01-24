package gr.entij.util;

import java.util.Objects;

public class Pair<T1, T2> {
    public final T1 val1;
    public final T2 val2;

    public Pair(T1 val1, T2 val2) {
        this.val1 = val1;
        this.val2 = val2;
    }

    public static <T1, T2> Pair<T1, T2> pair(T1 val1, T2 val2) {
        return new Pair<>(val1, val2);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.val1);
        hash = 83 * hash + Objects.hashCode(this.val2);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair<?, ?> other = (Pair<?, ?>) obj;
        if (!Objects.equals(this.val1, other.val1)) {
            return false;
        }
        return Objects.equals(this.val2, other.val2);
    }
    
    
}