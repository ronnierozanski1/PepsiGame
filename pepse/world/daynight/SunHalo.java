package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;

import java.awt.*;

/**
 * Represents the halo effect around the sun, enhancing the visual aesthetics
 * of the day-night cycle in the game. The halo dynamically follows the sun's
 * position in the sky.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameObject
 */
public class SunHalo {

    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final Vector2 SUN_HALO_SIZE = new Vector2(120, 120);
    private static final float HALO_SIZE_MULTIPLIER = 1.5f;

    /**
     * Creates the sun halo and sets its position and behavior to follow the sun.
     *
     * @param sun The sun GameObject around which the halo is positioned.
     * @return The created GameObject representing the sun halo.
     */
    public static GameObject create(GameObject sun) {

        Vector2 haloSize = sun.getDimensions().mult(HALO_SIZE_MULTIPLIER);
        GameObject sunHalo = new GameObject(Vector2.ZERO, haloSize ,new OvalRenderable(SUN_HALO_COLOR));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(PepseGameManager.SUN_HALO_TAG);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        return sunHalo;
    }
}
