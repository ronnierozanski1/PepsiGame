package pepse.world;

/**
 * Interface for listening to avatar jump events. Classes that implement this interface
 * can define behavior that occurs when the avatar enters jump mode.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 */
public interface AvatarJumpListener {
    /**
     * Called when the avatar enters jump mode.
     */
    void jumpMode();
}
