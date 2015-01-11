package gr.entij.event;

import gr.entij.Entity;

/**
 * A {@code StateEvent} is generated when an entity changes state
 * even,  if the new state equals the old state.
 * @see Entity#setState(long) 
 * @see Entity#addStateListener(java.util.function.Consumer) 
 */
public class StateEvent extends Event {

    /**
     * The previous state of the source.
     * May be equal to the next state.
     */
    public final long previousState;
    
    /**
     * The next (current) state of the source.
     * May be equal to the previous state.
     */
    public final long nextState;
    
    public StateEvent(Entity source, long previousState, long nextState) {
        super(source);
        this.previousState = previousState;
        this.nextState = nextState;
    }

}
