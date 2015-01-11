package gr.entij;

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

}
