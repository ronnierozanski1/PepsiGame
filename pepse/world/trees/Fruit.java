package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.Block;

import java.awt.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents a fruit in the game world. Fruits provide energy to the avatar upon collision
 * and regenerate after a specific time period.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameObject
 * @see Block
 */
public class Fruit extends GameObject {

    /**
     * the fruits size
     */
    public static final float FRUIT_SIZE = Block.SIZE * 0.8f;
    private static final Color FRUIT_COLOR = Color.BLUE;
    private static final float FRUIT_ENERGY = 10f;
    private static final int VISIBLE_VALUE = 1000;
    private final BiConsumer<GameObject, Integer> addFunc;
    private final BiConsumer<GameObject, Integer> removeFunc;
    private final Consumer<Float> addEnergyFunc;

    /**
     * Constructs a new Fruit instance.
     *
     * @param topLeftCorner The top-left corner of the fruit's position, in window coordinates.
     * @param addFunc A function to add the fruit to a specified layer.
     * @param removeFunc A function to remove the fruit from a specified layer.
     * @param addEnergyFunc A function to add energy to the avatar upon collision with the fruit.
     *
     */
    public Fruit(Vector2 topLeftCorner, BiConsumer<GameObject, Integer> addFunc,
                 BiConsumer<GameObject, Integer> removeFunc, Consumer<Float> addEnergyFunc) {
        super(topLeftCorner, new Vector2(FRUIT_SIZE, FRUIT_SIZE), new OvalRenderable(FRUIT_COLOR));
        this.addFunc = addFunc;
        this.removeFunc = removeFunc;
        this.addEnergyFunc = addEnergyFunc;
        this.setTag(PepseGameManager.FRUIT_TAG);
    }

    /**
     * Handles the collision logic for the fruit. If the fruit collides with the avatar:
     * - The fruit is removed and made invisible.
     * - Energy is added to the avatar.
     * - The fruit is scheduled to regenerate after a fixed time.
     *
     * @param other The object with which the fruit has collided.
     * @param collision The collision information.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if(other.getTag().equals(PepseGameManager.AVATAR_TAG)) {
            super.onCollisionEnter(other, collision);
            //remove fruit - make invisible
            this.removeFunc.accept(this, PepseGameManager.FRUIT_LAYER);
            this.addFunc.accept(this, Layer.BACKGROUND);
            this.renderer().setOpaqueness(0);
            //add energy
            addEnergyFunc.accept(FRUIT_ENERGY);
            //add fruit back
            new ScheduledTask(this, PepseGameManager.DAY_CYCLE_LENGTH, false,
                    this::addFruitBack);
        }
    }

    /*
     * Regenerates the fruit. The fruit is re-added to its original layer and made visible again.
     */
    private void addFruitBack() {
        this.removeFunc.accept(this, Layer.BACKGROUND);
        this.addFunc.accept(this, PepseGameManager.FRUIT_LAYER);
        this.renderer().setOpaqueness(VISIBLE_VALUE);
    }
}
