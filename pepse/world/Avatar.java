package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the controllable avatar in the game world. The avatar can run, jump, and consume energy
 * during these actions. It also interacts with other game objects like the ground.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameObject
 */
public class Avatar extends GameObject {

    private static final Vector2 AVATAR_SIZE = new Vector2(50, 80); //with, height
    private static final float HORIZONTAL_SPEED = 300;
    private static final float JUMP_SPEED = -700;
    private static final float GRAVITY = 700;
    private static final float MAX_ENERGY = 100;
    private static final float IDLE_ENERGY = 1.0f;
    private static final float RUN_ENERGY = 0.5f;
    private static final float JUMP_ENERGY = 10f;
    private static final String[] IDLE_MOOD_IMAGES_PATHS = {"assets/idle_0.png", "assets/idle_1.png",
            "assets/idle_2.png", "assets/idle_3.png"};
    private static final String[] JUMP_MOOD_IMAGES_PATHS = {"assets/jump_0.png", "assets/jump_1.png",
            "assets/jump_2.png", "assets/jump_3.png"};
    private static final String[] RUN_MOOD_IMAGES_PATHS = {"assets/run_0.png", "assets/run_1.png",
            "assets/run_2.png", "assets/run_3.png", "assets/run_4.png", "assets/run_5.png"};
    private static final float TIME_BETWEEN_IMAGES = 0.5f;
    private float energy = MAX_ENERGY;
    private final UserInputListener inputListener;
    private AnimationRenderable idleMoodAnimation;
    private AnimationRenderable jumpMoodAnimation;
    private AnimationRenderable runMoodAnimation;
    private final List<AvatarJumpListener> listeners = new ArrayList<>();
    private boolean jumpMode;



    /**
     * Constructs a new Avatar instance.
     *
     * @param topLeftCorner The initial position of the avatar in the game world,
     *                      in window coordinates (pixels).
     * @param inputListener The object responsible for listening to user input for controlling the avatar.
     * @param imageReader   An image reader for loading animations and textures for the avatar.
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, AVATAR_SIZE, imageReader.readImage(IDLE_MOOD_IMAGES_PATHS[0],
                true));
        this.inputListener = inputListener;
        this.transform().setAccelerationY(GRAVITY);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        idleMoodAnimation = new AnimationRenderable(IDLE_MOOD_IMAGES_PATHS, imageReader,
                true, TIME_BETWEEN_IMAGES);
        jumpMoodAnimation = new AnimationRenderable(JUMP_MOOD_IMAGES_PATHS, imageReader,
                true, TIME_BETWEEN_IMAGES);
        runMoodAnimation = new AnimationRenderable(RUN_MOOD_IMAGES_PATHS, imageReader,
                true, TIME_BETWEEN_IMAGES);
        this.setTag(PepseGameManager.AVATAR_TAG);
        this.jumpMode = false;
    }

    /**
     * Adds a listener that responds to the avatar's jump events.
     *
     * @param listener The listener to be added.
     */
    public void addListener(AvatarJumpListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes a listener that responds to the avatar's jump events.
     *
     * @param listener The listener to be removed.
     */
    public void removeListener(AvatarJumpListener listener) {
        this.listeners.remove(listener);
    }

    /**
     * Updates the avatar's position, animations, and energy based on user input and game state.
     *
     * @param deltaTime The time interval since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float velocityX = 0;
        if(this.inputListener.isKeyPressed(KeyEvent.VK_LEFT) &&
                !this.inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            if(this.energy >= RUN_ENERGY) {
                velocityX -= HORIZONTAL_SPEED;
                renderer().setRenderable(runMoodAnimation);
                renderer().setIsFlippedHorizontally(true);
                if (!jumpMode) {
                    this.energy -= RUN_ENERGY;
                }
            }
        }
        if(this.inputListener.isKeyPressed(KeyEvent.VK_RIGHT) &&
                !this.inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            if(this.energy >= RUN_ENERGY) {
                velocityX += HORIZONTAL_SPEED;
                renderer().setRenderable(runMoodAnimation);
                renderer().setIsFlippedHorizontally(false);
                if (!jumpMode) {
                    this.energy -= RUN_ENERGY;
                }
            }
        }
        this.transform().setVelocityX(velocityX);

        if(this.inputListener.isKeyPressed(KeyEvent.VK_SPACE) && this.getVelocity().y() == 0) {
            if(this.energy >= JUMP_ENERGY) {
                this.jumpMode = true;
                this.transform().setVelocityY(JUMP_SPEED);
                this.energy -= JUMP_ENERGY;
                renderer().setRenderable(jumpMoodAnimation);
                notifyJump();

            }
        }

        if(velocityX == 0 && this.getVelocity().y() == 0) {
            this.energy += IDLE_ENERGY;
            renderer().setRenderable(idleMoodAnimation);

        }

        this.energy = Math.max(0, Math.min(this.energy, MAX_ENERGY));
    }

    /**
     * Returns the current energy level of the avatar, rounded down to the nearest integer.
     *
     * @return The avatar's energy level.
     */
    public float getEnergy() {
        return (int) Math.floor(this.energy);
    }

    /**
     * Increases the avatar's energy by the specified amount.
     *
     * @param energyBoost The amount of energy to add to the avatar.
     */
    public void addEnergy(float energyBoost) {
        this.energy += energyBoost;
    }

    /**
     * Handles collision events between the avatar and other game objects.
     *
     * @param other     The other game object involved in the collision.
     * @param collision Details about the collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals(PepseGameManager.GROUND_TAG)){
            this.transform().setVelocityY(0);
            this.jumpMode = false;
        }
    }


    /*
     * Notifies all registered listeners that the avatar has jumped.
     */
    private void notifyJump() {
        for (AvatarJumpListener listener : this.listeners) {
            listener.jumpMode();
        }
    }
}