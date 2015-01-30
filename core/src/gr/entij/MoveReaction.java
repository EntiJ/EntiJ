package gr.entij;

import gr.entij.util.SingleLinkedMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Set of actions to be performed by an target as reaction to a
 {@linkplain Entity#move move}. <p>
 * These actions may include:
 * <ul>
 *   <li>Changing the target's state</li>
 *   <li>Changing the target's position</li>
 *   <li>Changing the target's properties</li>
 * </ul>
 * @see Logic
 * @see Entity#move
 */
public class MoveReaction {
    
    static class AndThen {
        Entity target;
        Stream<? extends Entity> targets;
        Object move;

        public AndThen(Entity target, Stream<? extends Entity> targets, Object move) {
            this.target = target;
            this.targets = targets;
            this.move = move;
        }
    }

    /**
     * The next position of the target or {@code null} if the position should
     * not change.
     */
    Long nextPosit;
    
    /**
     * The next state of the target or {@code null} if the state should
     * not change.
     */
    Long nextState;
    
    /**
     * The next position of the target or {@code null} if none of the properties
     * should change.
     */
     /* A {@code null} value on an entry indicates that the
     * property should be removed.
     */
    Map<String, Object> nextPropValues;
    
    /**
     * Moves to be performed after this move reaction is performed.
     */
    List<AndThen> andThenMoves;
    
    boolean consume = true;

    /**
     * Creates an new {@code MoveReaction} that does not perform any action.
     */
    public MoveReaction() {
    }
    
    /**
     * Sets the next position the target that performs the move should take.
     * @param posit the next position the target that performs the move should take
     * @return this {@code MoveReaction}
     */
    public MoveReaction posit(Long posit) {
        nextPosit = posit;
        return this;
    }
    
    /**
     * Sets the next state the target that performs the move should take.
     * @param state the next state the target that performs the move should take
     * @return this {@code MoveReaction}
     */
    public MoveReaction state(Long state) {
        nextState = state;
        return this;
    }
    
    /**
     * Appends the given move to the moves that are to be performed after this
     * reaction has been processed. These moves moves will be performed in the
     * order they are submitted.
     * @param target the Entity to perform the move
     * @param move the move to performed
     * @return this {@code MoveReaction}
     */
    public MoveReaction andThenMove(Entity target, Object move) {
        Objects.requireNonNull(target, "target can not be null");
        Objects.requireNonNull(move, "move can not be null");
        andThenImpl(target, null, move);
        return this;
    }
    
    /**
     * Appends the given move to the moves that are to be performed after this
     * reaction has been processed. These moves moves will be performed in the
     * order they are submitted.
     * @param targets the Entities to perform the move
     * @param move the move to performed
     * @return this {@code MoveReaction}
     */
    public MoveReaction andThenMove(Stream<? extends Entity> targets, Object move) {
        Objects.requireNonNull(targets, "targets can not be null");
        Objects.requireNonNull(move, "move can not be null");
        andThenImpl(null, targets, move);
        return this;
    }
    
    /**
     * Appends the given move to the moves that are to be performed after this
     * reaction has been processed. These moves moves will be performed in the
     * order they are submitted.
     * @param targets the Entities to perform the move
     * @param move the move to performed
     * @return this {@code MoveReaction}
     */
    public MoveReaction andThenMove(Collection<? extends Entity> targets, Object move) {
        Objects.requireNonNull(targets, "targets can not be null");
        Objects.requireNonNull(move, "move can not be null");
        andThenImpl(null, targets.stream(), move);
        return this;
    }
    
    private void andThenImpl(Entity target, Stream<? extends Entity> targets, Object move) {
        if (andThenMoves == null) {
            andThenMoves = new LinkedList<>();
        }
        andThenMoves.add(new AndThen(target, targets, move));
    }
    
    /**
     * If {@code true} (the default) the entity that performs the move will not
     * query the remaining logics for reactions.
     * @param consume whether to query other logics
     * @return  this {@code MoveReaction}
     */
    public MoveReaction consume(boolean consume) {
        this.consume = consume;
        return this;
    }
    
    /**
     * Sets the values that the given properties of the target that performs the
     * move should take.
     * @param props the values that the given properties of the target that
     * performs the move should take
     * @return this {@code MoveReaction}
     */
    public MoveReaction props(Map<String, Object> props) {
        nextPropValues = props;
        return this;
    }
    
    /**
     * Sets the values that the specified property of the target that performs the
     * move should take.
     * @param name the name of the property that should change value
     * @param val the value the specified property should take
     * @return this {@code MoveReaction}
     */
    public MoveReaction prop(String name, Object val) {
        if (nextPropValues == null) {
            nextPropValues = new SingleLinkedMap<>(name, val);
        } else {
            nextPropValues.put(name, val);
        }
        return this;
    }
}
