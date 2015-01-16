package gr.entij.event;

import java.util.Map;

import gr.entij.Entity;

/**
 * A {@code PropertyEvent} is generated when a property of an entity
 * changes state, even if the new value equals the old value.
 * A single event will be generated also in the the case where multiple
 * properties change by a single call to {@link Entity#setProps(java.util.Map) Entity.setProps} or
 * {@link Entity#putProps(java.util.Map) Entity.putProps}.
 * @see Entity#setProp(java.lang.String, java.lang.Object) 
 * @see Entity#setProps(java.util.Map)
 * @see Entity#putProps(java.util.Map)
 * @see Entity#removeProp(java.lang.String) 
 * @see Entity#addPropertyListener(java.util.function.Consumer) 
 */
public class PropertyEvent extends Event {

    /**
     * Contains the <strong>old</strong> values of the properties that
     * changed. If a property has just being added, then it is contained in this set
     * with a {@code null} value.
     */
    public final Map<String, Object> oldValues;
    
    public PropertyEvent(Entity source, Map<String, Object> oldValues) {
        super(source);
        this.oldValues = oldValues;
    }

    @Override
    public String toString() {
        return super.toString()+ ", oldValues: "+oldValues;
    }
}
