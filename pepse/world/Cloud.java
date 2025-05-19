package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.world.AvatarJumpListener;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a cloud structure that moves across the screen.
 * Can trigger rain animations when an avatar jumps.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see AvatarJumpListener
 */
public class Cloud implements AvatarJumpListener {

    private static final Color CLOUD_COLOR = new Color(255, 255, 255);
    private static final Color RAIN_DROP_COLOR = Color.blue;
    private static final int RAIN_DROP_SIZE = 15;
    private static final int TRANSITION_TIME = 20;
    private static final float Y_POSITION_FACTOR = 8f;
    private static final int X_POSITION_FACTOR = 5;
    private static final float RAIN_DROP_TRANS_START = 1f;
    private static final float RAIN_DROP_TRANS_END = 0f;
    private static final int CLOUD_BLOCK_YES = 1;
    private static final List<List<Integer>> CLOUD_PATTERN = List.of(
            List.of(0, 1, 1, 0, 0, 0),
            List.of(1, 1, 1, 0, 1, 0),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(1, 1, 1, 1, 1, 1),
            List.of(0, 1, 1, 1, 0, 0),
            List.of(0, 0, 0, 0, 0, 0)
    );
    private static final float GRAVITY = 700f;
    private static final float OPACITY_FADE_TIME = 1.0f;
    private static List<GameObject> cloudBlocks;
    private static BiConsumer<GameObject, Integer> addFunc;
    private static Consumer<AvatarJumpListener> addListenerFunc;


    /**
     * Creates and initializes a cloud structure.
     *
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The duration for a complete cloud movement cycle.
     * @param addFunc Function to add game objects to the game manager.
     * @param addListenerFunc Function to add listeners for avatar jumps.
     * @return A list of cloud blocks that make up the cloud structure.
     */
    public static List<GameObject> create(Vector2 windowDimensions, float cycleLength,
                                          BiConsumer<GameObject, Integer> addFunc,
                                          Consumer<AvatarJumpListener> addListenerFunc) {
        Cloud.addFunc = addFunc;
        Cloud.addListenerFunc = addListenerFunc;
        Vector2 startPosition = new Vector2(-Block.SIZE * X_POSITION_FACTOR,
                windowDimensions.y() / Y_POSITION_FACTOR);
        int cloudWidth = CLOUD_PATTERN.get(0).size() * Block.SIZE;
        int cloudHeight = CLOUD_PATTERN.size() * Block.SIZE;
        GameObject cloud = new GameObject(startPosition, new Vector2(cloudWidth, cloudHeight), null);
        cloud.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        cloud.setTag(PepseGameManager.CLOUD_TAG);

        Cloud.cloudBlocks = buildCloudStructure(startPosition);
        applyTransitionPerBlock(cloudBlocks, windowDimensions, cloudWidth);

        Cloud cloud1 = new Cloud();
        Cloud.addListenerFunc.accept(cloud1);
        return cloudBlocks;
    }

    /**
     * Implements the jumpMode action for triggering rain.
     *
     * @see AvatarJumpListener
     */
    @Override
    public void jumpMode() {
        createRain();
    }

    /*
     * Applies transition effects to each block in the cloud structure.
     *
     * Moves each block across the screen in a loop.
     */
    private static void applyTransitionPerBlock(List<GameObject> cloudParts,
                                                Vector2 windowDimensions, int cloudWidth) {
        for (GameObject block : cloudParts) {
            if(PepseGameManager.CLOUD_BLOCK_TAG.equals(block.getTag())) {
                new Transition<>(block,
                        block::setCenter,
                        block.getCenter(),
                        block.getCenter().add(new Vector2(windowDimensions.x() + cloudWidth, 0)),
                        Transition.LINEAR_INTERPOLATOR_VECTOR,
                        TRANSITION_TIME,
                        Transition.TransitionType.TRANSITION_LOOP,
                        null);
            }
        }
    }

    /*
     * Builds the structure of the cloud based on the defined pattern.
     *
     * Each block in the cloud is added to the cloud structure if it matches the pattern.
     */
    private static List<GameObject> buildCloudStructure(Vector2 startPosition) {
        List<GameObject> cloudBlocks = new ArrayList<>();
        for (int i = 0; i < CLOUD_PATTERN.size(); i++) {
            for (int j = 0; j < CLOUD_PATTERN.get(i).size(); j++) {
                if (CLOUD_PATTERN.get(i).get(j) == CLOUD_BLOCK_YES) {
                    Vector2 blockPosition = new Vector2(startPosition.x() + j * Block.SIZE,
                            startPosition.y() + i * Block.SIZE);
                    GameObject block = new Block(blockPosition,
                            new RectangleRenderable(ColorSupplier.approximateMonoColor(CLOUD_COLOR)));
                    block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                    block.setTag(PepseGameManager.CLOUD_BLOCK_TAG);
                    cloudBlocks.add(block);
                }
            }
        }
        return cloudBlocks;
    }

    /*
     * Creates a single raindrop with a specified position and velocity.
     *
     * Raindrops fall under the effect of gravity and fade out over time.
     */
    private GameObject createRainDrop(Vector2 position, Vector2 velocity) {
        GameObject rainDrop = new GameObject(position, new Vector2(RAIN_DROP_SIZE, RAIN_DROP_SIZE),
                new OvalRenderable(RAIN_DROP_COLOR));
        rainDrop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        rainDrop.setVelocity(velocity);
        rainDrop.transform().setAccelerationY(GRAVITY);
        rainDrop.setTag(PepseGameManager.RAIN_DROP_TAG);
        new Transition<>(
                rainDrop,
                rainDrop.renderer()::setOpaqueness,
                RAIN_DROP_TRANS_START, // Start fully opaque
                RAIN_DROP_TRANS_END, // End fully transparent
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                OPACITY_FADE_TIME,
                Transition.TransitionType.TRANSITION_ONCE,
                null);
        return rainDrop;
    }


    /*
     * Creates rain by randomly generating raindrops near the cloud.
     *
     * Adds raindrops to the game manager at the specified positions.
     */
    private void createRain() {
        Random random = new Random();
        for (GameObject block : Cloud.cloudBlocks) {
            if (random.nextBoolean()) {
                GameObject rainDrop = createRainDrop(block.getCenter(), block.getVelocity());
                Cloud.addFunc.accept(rainDrop, PepseGameManager.RAIN_DROP_LAYER);
            }
        }
    }
}