package gr.entij.sound;
import gr.entij.event.MoveEvent;
import gr.entij.event.StateEvent;
import gr.entij.Entity;
import gr.entij.Component;
import gr.entij.event.EntityEvent;
import java.util.function.Consumer;

public class SoundComp implements Component {
    
    private MoveSoundDeterminer moveSoundDeterminer;
    private StateSoundDeterminer stateSoundDeterminer;
    
    private final Consumer<MoveEvent> moveListener = (MoveEvent e) -> {
        if (moveSoundDeterminer != null) {
            String soundFile = moveSoundDeterminer.getSoundForMove(e);
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
    
    private final Consumer<EntityEvent> destroyListener = (EntityEvent e) -> {
        if (e.type == EntityEvent.Type.DESTROYED) {
            e.source.removeMoveListener(moveListener);
            e.source.removeStateListener(stateListener);
            e.source.removeEntityListener(this.destroyListener);
        }
    };
    
    public MoveSoundDeterminer getMoveSoundDeterminer() {
        return moveSoundDeterminer;
    }

    public void setMoveSoundDeterminer(MoveSoundDeterminer moveSoundDeterminer) {
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
        target.addMoveListener(moveListener);
        target.addStateListener(stateListener);
        target.addEntityListener(destroyListener);
    }

}
