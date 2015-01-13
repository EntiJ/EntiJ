package gr.entij.sound;

import java.util.*;

import gr.entij.event.MoveEvent;
import gr.entij.event.StateEvent;

public class BaseSoundDeterminer
implements StateSoundDeterminer, MoveSoundDeterminer {

    private final Map<Long, String> moveSounds = new HashMap<>();
    private final Map<Long, String> stateSounds = new HashMap<>();
    
    public void setSoundForMove(Long move, String soundFile) {
        moveSounds.put(move, soundFile);
    }
    
    public void setSoundForState(long state, String soundFile) {
        stateSounds.put(state, soundFile);
    }
    
    @Override
    public String getSoundForMove(MoveEvent e) {
        return moveSounds.get(e.move);
    }

    @Override
    public String getSoundForState(StateEvent e) {
        return e.nextState != e.previousState
                ? stateSounds.get(e.nextState)
                : null;
    }

}
