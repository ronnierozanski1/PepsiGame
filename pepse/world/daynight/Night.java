package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;

import java.awt.*;

/**
 * Represents the transition to night time in the game world.
 * The Night class manages a visual effect that simulates the
 * transition from day to night.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameObject
 */
public class Night {

    private static final Color BLACK_COLOR = Color.BLACK;
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final float INITIALIZE_TRANSITION_VALUE = 0f;
    private static final float HALF_CYCLE = 2;


    /**
     * Creates a GameObject that represents the night effect.
     * The night effect is visualized as a black transparent overlay
     * that oscillates in opacity to simulate the transition between
     * day and night.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The total duration of a full day-night cycle.
     * @return A GameObject representing the night effect.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BLACK_COLOR));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(PepseGameManager.NIGHT_TAG);
        new Transition<Float>(
                night, // the game object being changed
                night.renderer()::setOpaqueness, // the method to call
                INITIALIZE_TRANSITION_VALUE, // initial transition value
                MIDNIGHT_OPACITY, // final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT,// use a cubic interpolator
                cycleLength/HALF_CYCLE, // transition fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, // Choose appropriate ENUM value
                null);// nothing further to execute upon reaching final value
        return night;

    }
}
