package gr.entij.sound;
import gr.entij.event.PositEvent;
import gr.entij.event.StateEvent;
import gr.entij.Entity;
import gr.entij.Component;
import gr.entij.event.EntityEvent;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SoundComp implements Component {
    
    private PositSoundDeterminer moveSoundDeterminer;
    private StateSoundDeterminer stateSoundDeterminer;
    
    private final Consumer<PositEvent> moveListener = (PositEvent e) -> {
        if (moveSoundDeterminer != null) {
            String soundFile = moveSoundDeterminer.getSoundForPosit(e);
            if (soundFile != null) {
                SoundUtil.playClip(soundFile);
            }
        }
    };
    
    private final Consumer<StateEvent> stateListener = (StateEvent e) -> {
        if (stateSoundDeterminer != null) {
            String soundFile = stateSoundDeterminer.getSoundForState(e);
            if (soundFile != null) {
                SoundUtil.playClip(soundFile);
            }
        }
    };
    
    private final Predicate<EntityEvent> destroyListener = (EntityEvent e) -> {
        if (e.type == EntityEvent.Type.DESTROYED) {
            e.source.removePositListener(moveListener);
            e.source.removeStateListener(stateListener);
            return false;
        }
        return true;
    };
    
    public PositSoundDeterminer getMoveSoundDeterminer() {
        return moveSoundDeterminer;
    }

    public void setMoveSoundDeterminer(PositSoundDeterminer moveSoundDeterminer) {
        this.moveSoundDeterminer = moveSoundDeterminer;
    }

    public StateSoundDeterminer getStateSoundDeterminer() {
        return stateSoundDeterminer;
    }

    public void setStateSoundDeterminer(StateSoundDeterminer stateSoundDeterminer) {
        this.stateSoundDeterminer = stateSoundDeterminer;
    }

    
    @Override
    public void attach(Entity target) {
        target.addPositListener(moveListener);
        target.addStateListener(stateListener);
        target.addEntityListenerRemovable(destroyListener);
    }

}
