package gr.entij;

import gr.entij.util.SingleLinkedMap;
import java.util.Map;

/**
 * Set of actions to be performed by an entity as reaction to a
 * {@linkplain Entity#move move}. <p>
 * These actions may include:
 * <ul>
 *   <li>Changing the entity's state</li>
 *   <li>Changing the entity's position</li>
 *   <li>Changing the entity's properties</li>
 * </ul>
 * @see Logic
 * @see Entity#move
 */
public class MoveReaction {

    /**
     * The next position of the entity or {@code null} if the position should
     * not change.
     */
    public Long nextPosit;
    
    /**
     * The next state of the entity or {@code null} if the state should
     * not change.
     */
    public Long nextState;
    
    /**
     * The next position of the entity or {@code null} if none of the properties
     * should change.
     */
     /* A {@code null} value on an entry indicates that the
     * property should be removed.
     */
    public Map<String, Object> nextPropValues;
    
    public boolean consume = true;

    /**
     * Creates an new {@code MoveReaction} that does not perform any action.
     */
    public MoveReaction() {
    }
    
    /**
     * Sets the next position the entity that performs the move should take.
     * @param posit the next position the entity that performs the move should take
     * @return this {@code MoveReaction}
     */
    public MoveReaction posit(Long posit) {
        nextPosit = posit;
        return this;
    }
    
    /**
     * Sets the next state the entity that performs the move should take.
     * @param state the next state the entity that performs the move should take
     * @return this {@code MoveReaction}
     */
    public MoveReaction state(Long state) {
        nextState = state;
        return this;
    }
    
    public MoveReaction consume(boolean consume) {
        this.consume = consume;
        return this;
    }
    
    /**
     * Sets the values that the given properties of the entity that performs the
     * move should take.
     * @param props the values that the given properties of the entity that performs the
     * move should take
     * @return this {@code MoveReaction}
     */
    public MoveReaction props(Map<String, Object> props) {
        nextPropValues = props;
        return this;
    }
    
    /**
     * Sets the values that the specified property of the entity that performs the
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
