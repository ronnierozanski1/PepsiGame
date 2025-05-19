package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;

/**
 * Represents a basic block in the game. Blocks are static objects
 * that can serve as the building blocks for terrain, structures, or other components.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameObject
 */
public class Block extends GameObject {

    /** The size of a block in pixels. */
    public static final int SIZE = 30;

    /**
     * Constructs a new Block instance.
     *
     * @param topLeftCorner The position of the block's top-left corner, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param renderable    The renderable representing the block. Can be null, in which case
     *                      the block will not be rendered.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        this.setTag(PepseGameManager.BLOCK_TAG);
    }

}
