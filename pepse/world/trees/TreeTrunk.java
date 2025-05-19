package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
/**
 * Represents a single trunk block of a tree in the game world.
 * The trunk is a solid block with a brownish color.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see Block
 */
public class TreeTrunk extends Block {

    private static final Color TREE_TRUNK_COLOR = new Color(100, 50, 20);


    /**
     * Constructs a new TreeTrunk instance with a specific position and color.
     *
     * @param topLeftCorner Position of the tree trunk block, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     */
    public TreeTrunk(Vector2 topLeftCorner) {
        super(topLeftCorner, new RectangleRenderable(ColorSupplier.approximateColor(TREE_TRUNK_COLOR)));
        this.setTag(PepseGameManager.TREE_TRUNK_TAG);
    }
}
