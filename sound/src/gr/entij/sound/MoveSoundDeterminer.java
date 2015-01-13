package gr.entij.sound;
import gr.entij.event.MoveEvent;;

@FunctionalInterface
public interface MoveSoundDeterminer {
    String getSoundForMove(MoveEvent e);
}
