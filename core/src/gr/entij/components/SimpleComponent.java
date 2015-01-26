/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.entij.components;

import gr.entij.*;
import gr.entij.event.*;
import java.util.function.Consumer;


public class SimpleComponent implements Component {
    
    private Consumer<? super StateEvent>[] stateListen;
    private Consumer<? super PositEvent>[] positListen;
    private Consumer<? super PropertyEvent>[] propertyListen;
    private Consumer<? super EntityEvent>[] entityListen;
    
    public SimpleComponent stateListen(Consumer<? super StateEvent>... stateListen) {
        this.stateListen = stateListen;
        return this;
    }
    
    public SimpleComponent positListen(Consumer<? super PositEvent>... positListen) {
        this.positListen = positListen;
        return this;
    }
    
    public SimpleComponent propertyListen(Consumer<? super PropertyEvent>... propertyListen) {
        this.propertyListen = propertyListen;
        return this;
    }
    
    public SimpleComponent entityListen(Consumer<? super EntityEvent>... entityListen) {
        this.entityListen = entityListen;
        return this;
    }
    
    @Override
    public void attach(Entity target) {
        if (stateListen != null) {
            for (Consumer<? super StateEvent> listener : stateListen) {
                target.addStateListener(listener);
            }
        }
        if (positListen != null) {
            for (Consumer<? super PositEvent> listener : positListen) {
                target.addPositListener(listener);
            }
        }
        if (propertyListen != null) {
            for (Consumer<? super PropertyEvent> listener : propertyListen) {
                target.addPropertyListener(listener);
            }
        }
        if (entityListen != null) {
            for (Consumer<? super EntityEvent> listener : entityListen) {
                target.addEntityListener(listener);
            }
        }
    }
    
}
