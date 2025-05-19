package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.Terrain;

import java.awt.*;

/**
 * Responsible for creating and animating the sun object.
 * The sun follows a circular path to simulate the natural day-night cycle.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 */
public class Sun {

    private static final Color SUN_COLOR = Color.YELLOW;
    private static final Vector2 SUN_SIZE = new Vector2(100, 100);
    private static final float START_ANGLE = 0;
    private static final float END_ANGLE = 360;
    private static final float POSITION_FACTOR1 = 2f;
    private static final float POSITION_FACTOR2 = 3f;


    /**
     * Creates a sun object that animates in a circular path representing the sun's daily movement.
     *
     * @param windowDimensions The dimensions of the game window. Used to determine the sun's path.
     * @param cycleLength      The length of the sun's movement cycle in seconds.
     * @return A GameObject representing the sun.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {

        Vector2 sunStartPosition = new Vector2(windowDimensions.x() / POSITION_FACTOR1,
                windowDimensions.y() / POSITION_FACTOR2);
        Vector2 sunCycleCenter = new Vector2(windowDimensions.x() / POSITION_FACTOR1 ,
                windowDimensions.y() * POSITION_FACTOR1 / POSITION_FACTOR2);

        GameObject sun = new GameObject(sunStartPosition, SUN_SIZE ,new OvalRenderable(SUN_COLOR));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(PepseGameManager.SUN_TAG);

        new Transition<Float>(
                sun,
                (Float angle) -> sun.setCenter
                        (sunStartPosition.subtract(sunCycleCenter).rotated(angle).add(sunCycleCenter)),
                START_ANGLE,
                END_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
    }
}
