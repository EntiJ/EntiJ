package gr.entij;

import gr.entij.util.SingleLinkedMap;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Set of actions to be performed by a target as reaction to a
 {@link Entity#move input}. <p>
 * These actions may include:
 * <ul>
 *   <li>Changing the target's state</li>
 *   <li>Changing the target's position</li>
 *   <li>Changing the target's properties</li>
 * </ul>
 * @see Logic
 * @see Entity#move
 */
public class Reaction {
    
    static class AndThen {
        Entity target;
        Stream<? extends Entity> targets;
        Object input;
        String funcName;
        Object[] args;

        public AndThen(Entity target, Stream<? extends Entity> targets, Object input) {
            this.target = target;
            this.targets = targets;
            this.input = input;
        }

        public AndThen(String funcName, Object[] params) {
            this.funcName = funcName;
            this.args = params;
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
     * Moves and calls to be performed after the input reaction is performed.
     */
    List<AndThen> andThen;
    
    boolean consume = true;

    /**
     * Creates an new {@code MoveReaction} that does not perform any action.
     */
    public Reaction() {
    }
    
    /**
     * Sets the next position the target that performs the input should take.
     * @param posit the next position the target that performs the input should take
     * @return this {@code Reaction}
     */
    public Reaction posit(Long posit) {
        nextPosit = posit;
        return this;
    }
    
    /**
     * Sets the next state the target that performs the input should take.
     * @param state the next state the target that performs the input should take
     * @return this {@code Reaction}
     */
    public Reaction state(Long state) {
        nextState = state;
        return this;
    }
    
    /**
     * Appends the given function call to the actions to be performed after
     * this reaction has been processed. These actions moves will be performed
     * in the order where are submitted.
     * @param funcName the name of the function to be called
     * @param params the parameters of the call
     * @return  this {@code Reaction}
     */
    public Reaction andThenCall(String funcName, Object... params) {
        if (andThen == null) {
            andThen = new LinkedList<>();
        }
        andThen.add(new AndThen(funcName, params));
        return this;
    }
    
    /**
     * Appends the given input to the actions that are to be performed after this
 reaction has been processed. These actions will be performed in the
     * order they where submitted.
     * @param target the Entity to perform the input
     * @param move the input to performed
     * @return this {@code Reaction}
     */
    public Reaction andThenMove(Entity target, Object move) {
        Objects.requireNonNull(target, "target can not be null");
        Objects.requireNonNull(move, "move can not be null");
        andThenImpl(target, null, move);
        return this;
    }
    
    /**
     * Appends the given input to the actions that are to be performed after this
 reaction has been processed. These actions will be performed in the
     * order they where submitted.
     * @param targets the Entities to perform the input
     * @param move the input to performed
     * @return this {@code Reaction}
     */
    public Reaction andThenMove(Stream<? extends Entity> targets, Object move) {
        Objects.requireNonNull(targets, "targets can not be null");
        Objects.requireNonNull(move, "move can not be null");
        andThenImpl(null, targets, move);
        return this;
    }
    
    /**
     * Appends the given input to the actions that are to be performed after this
 reaction has been processed. These actions moves will be performed in the
     * order they where submitted.
     * @param targets the Entities to perform the input
     * @param move the input to performed
     * @return this {@code Reaction}
     */
    public Reaction andThenMove(Collection<? extends Entity> targets, Object move) {
        Objects.requireNonNull(targets, "targets can not be null");
        Objects.requireNonNull(move, "move can not be null");
        andThenImpl(null, targets.stream(), move);
        return this;
    }
    
    private void andThenImpl(Entity target, Stream<? extends Entity> targets, Object move) {
        if (andThen == null) {
            andThen = new LinkedList<>();
        }
        andThen.add(new AndThen(target, targets, move));
    }
    
    /**
     * If {@code true} (the default) the entity that performs the input will not
 query the remaining logics for reactions.
     * @param consume whether to query other logics
     * @return  this {@code Reaction}
     */
    public Reaction consume(boolean consume) {
        this.consume = consume;
        return this;
    }
    
    /**
     * Sets the values that the given properties of the target that performs the
 input should take.
     * @param props the values that the given properties of the target that
 performs the input should take
     * @return this {@code Reaction}
     */
    public Reaction putAll(Map<String, Object> props) {
        nextPropValues = props;
        return this;
    }
    
    /**
     * Sets the values that the specified property of the target that performs the
 input should take.
     * @param name the name of the property that should change value
     * @param val the value the specified property should take
     * @return this {@code Reaction}
     */
    public Reaction set(String name, Object val) {
        if (nextPropValues == null) {
            nextPropValues = new SingleLinkedMap<>(name, val);
        } else {
            nextPropValues.put(name, val);
        }
        return this;
    }
}
