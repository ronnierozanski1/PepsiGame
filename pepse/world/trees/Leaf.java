package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Random;

/**
 * Represents a single leaf in the game world. The leaf oscillates in size and angle,
 * simulating natural behavior, and interacts with its surroundings.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameObject
 * @see Block
 * @see ColorSupplier
 */
public class Leaf extends GameObject {

    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private static final float MAX_RANDOM_DELAY = 2f;
    private static final float ANGLE_MIN = -20f;
    private static final float ANGLE_MAX = 20f;
    private static final float TRANSITION_TIME = 2.0f;
    private static final float MAX_SIZE_FACTOR = 1.5f;
    private final Random random;

    /**
     * Constructs a new Leaf instance.
     * The leaf is assigned random behavior, such as delay before its oscillations.
     *
     * @param topLeftCorner The top-left corner position of the leaf in window coordinates.
     */
    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner, new Vector2(Block.SIZE, Block.SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        this.setTag(PepseGameManager.LEAF_TAG);
        this.random = new Random();
        float randomDelay = random.nextFloat(MAX_RANDOM_DELAY);
        new ScheduledTask(this, randomDelay, false, this::LeafTransitions);
    }

    /*
     * Initiates the oscillation transitions for the leaf.
     * This includes angle rotation and size adjustments.
     */
    private void LeafTransitions() {
        transitionByAngle();
        transitionByWidth();
    }

    /*
     * Creates a transition for the leaf's width and height, simulating a natural oscillation.
     */
    private void transitionByWidth() {
        Vector2 initialSize = new Vector2(Block.SIZE, Block.SIZE);
        new Transition<Vector2>(
                this,
                this::setDimensions,
                initialSize,
                initialSize.mult(MAX_SIZE_FACTOR),
                Transition.LINEAR_INTERPOLATOR_VECTOR,
                TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);

    }

    /*
     * Creates a transition for the leaf's angle, simulating a swaying motion.
     */
    private void transitionByAngle() {
        new Transition<Float>(
                this, //
                this.renderer()::setRenderableAngle,
                ANGLE_MIN,
                ANGLE_MAX,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }
}
