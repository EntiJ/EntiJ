package gr.entij.sound;
import gr.entij.event.PositEvent;;

@FunctionalInterface
public interface PositSoundDeterminer {
    String getSoundForPosit(PositEvent e);
}
