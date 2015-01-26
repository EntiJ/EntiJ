package gr.entij.sound;

import java.util.*;

import gr.entij.event.PositEvent;
import gr.entij.event.StateEvent;

// XXX It needs to support sounds for moves
public class BaseSoundDeterminer
implements StateSoundDeterminer, PositSoundDeterminer {

    private final Map<Long, String> positSounds = new HashMap<>();
    private final Map<Long, String> stateSounds = new HashMap<>();
    
    public void setSoundForPosit(long posit, String soundFile) {
        positSounds.put(posit, soundFile);
    }
    
    public void setSoundForState(long state, String soundFile) {
        stateSounds.put(state, soundFile);
    }
    
    @Override
    public String getSoundForPosit(PositEvent e) {
        return positSounds.get(e.nextPosit);
    }

    @Override
    public String getSoundForState(StateEvent e) {
        return e.nextState != e.previousState
                ? stateSounds.get(e.nextState)
                : null;
    }

}
