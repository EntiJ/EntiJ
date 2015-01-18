package gr.entij;

/**
 * Judges moves supplied to an entity for their validity and, if they are
 * valid, decides the next position of the entity. <p>
 * A {@code Logic} object is assigned to an entity via {@link Entity#setLogic(Logic)}
 * and used by that entity every time {@link Entity#move} is called. <p>
 * The same {@code Logic} object can be assigned to multiple entities.
 * @see Entity#move
 * @see Entity#setLogic(Logic)
 */
public interface Logic {

    /**
     * Determines whether the given move is valid and, if it is valid, returns
     * the next position of the entity. Otherwise, returns {@code null}. <p>
     * This method may also perform additional actions, such as changing
     * the entity's state or properties, as side effects of the move.
     * @param e the entity that is about to perform the given move
     * @param move the move to be performed, if found valid
     * @return the next position of the entity or {@code null} if the move is
     * invalid
     */
    Long nextPosit(Entity e, Object move);
}