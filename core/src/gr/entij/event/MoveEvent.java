package gr.entij.event;

import gr.entij.Entity;

/**
 * A {@code MoveEvent} is generated when  an entity changes position, 
 * even if the new position equals the old position.
 * @see Entity#addMoveListener(java.util.function.Consumer) 
 */
public class MoveEvent extends Event {

    /**
     * The previous position of the source.
     * May be equal to the next position.
     */
    public final long previousPosit;

    /**
     * The next (current) position of the source.
     * May be equal to the previous position.
     */
    public final long nextPosit;
    
    public MoveEvent(Entity source, Object move,
            long previousPosit, long nextPosit) {
        super(source, move);
//        this.move = move;
        this.previousPosit = previousPosit;
        this.nextPosit = nextPosit;
    }

    public MoveEvent(Entity source, long previousPosit, long nextPosit) {
        super(source);
        this.previousPosit = previousPosit;
        this.nextPosit = nextPosit;
    }

    @Override
    public String toString() {
        return super.toString()+", move: \""+move+"\", previousPosit: "
                +previousPosit+", nextPosit"+nextPosit;
    }
}
