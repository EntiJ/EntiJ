package gr.entij;

/**
 * Judges moves supplied to an entity for their validity and, if they are
 * valid, decides a reaction. <p>
 * A {@code Logic} object is assigned to an entity via {@link Entity#setLogic(Logic)}
 * and used by that entity every time {@link Entity#move} is called. <p>
 * The same {@code Logic} object can be assigned to multiple entities.
 * @see Entity#move
 * @see Entity#setLogic(Logic)
 */
public interface Logic {

    /**
     * Determines whether the given move is valid and, if it is valid, returns
     * a reaction for that move. Otherwise, returns {@code null}. <p>
     * This method may also perform additional actions, not covered by the
     * returned {@linkplain MoveReaction reaction object},
     * as side effects of the move.
     * @param e the entity that is about to perform the given move
     * @param move the move to be performed, if found valid
     * @return a reaction for move or {@code null} if the move is
     * invalid
     */
    MoveReaction reaction(Entity e, Object move);
}