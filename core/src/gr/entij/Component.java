package gr.entij;

import java.util.Objects;

/**
 * Component that can be attached to an {@link Entity} to provide additional
 * characteristics. For example, a graphic component may provide a graphical
 * representation for an {@code Entity} (see {@link gr.entij.graphics2d.GEntity}).
 * Other examples include components for sound or for physics. <p>
 * Usually, a component listens for moves or changes in the state or the
 * properties of the {@code Entity} (or {@code Entities}) that it is attached to
 * and responds appropriately. <p>
 * Any object that performs the type of functionality described above is
 * proposed to implement this interface.
 * @see Entity
 */
public interface Component {

    /**
     * Attaches this {@code Component} the given {@code Entity}.
     * @param target the {@code Entity} to become attached to
     */
    void attach(Entity target);

    /**
     * Returns a new component that is the combination of this and the given one.
     * The {@code attach} method of the returned component will first attach
     * this component and then the given one.
     * @param toBeCombined the component to be combined with this
     * @return the combination of this component and the given one
     * @throws NullPointerException if toBeCombined is null
     */
    default Component combine(Component toBeCombined) {
        Objects.requireNonNull(toBeCombined, "toBeCombined can not be null");
        return e -> {Component.this.attach(e); toBeCombined.attach(e);};
    }
}
