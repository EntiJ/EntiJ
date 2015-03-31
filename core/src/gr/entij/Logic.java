package gr.entij;

/**
 * Judges moves supplied to an entity for their validity and, if they are
 * valid, decides a reaction. <p>
 * A {@code Logic} object is assigned to an entity via {@link Entity#addLogic(Logic)}
 * and used by that entity every time {@link Entity#react} is called. <p>
 * The same {@code Logic} object can be assigned to multiple entities.
 * @see Entity#react
 * @see Entity#setLogic(Logic)
 */
public interface Logic extends Component {

    /**
     * Determines whether the given react is valid and, if it is valid, returns
 a reaction for that react. Otherwise, returns {@code null}. <p>
     * This method may also perform additional actions, not covered by the
     * returned {@link Reaction reaction object},
 as side effects of the react.
     * @param e the entity that is about to perform the given react
     * @param move the react to be performed, if found valid
     * @return a reaction for react or {@code null} if the react is
 invalid
     */
    Reaction reaction(Entity e, Object move);

    /**
     * Adds this logic to the given entity.
     * @param target the entity to be added to
     */
    @Override default void attach(Entity target) {
        target.addLogic(this);
    }
}