package gr.entij.sound;
import gr.entij.event.StateEvent;

@FunctionalInterface
public interface StateSoundDeterminer {
    String getSoundForState(StateEvent e);
}
