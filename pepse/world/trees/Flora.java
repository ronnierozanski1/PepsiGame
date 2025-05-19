package pepse.world.trees;

import danogl.GameObject;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Responsible for generating trees within a given range.
 * Each tree's placement and characteristics are determined
 * pseudo-randomly and consistently for a given seed.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameObject
 * @see Tree
 * @see Block
 */
public class Flora {

    private static final float TREE_PROBABILITY = 0.05f;
    private final Function<Float, Float> groundHeightAtFunc;
    private final BiConsumer<GameObject, Integer> addFunc;
    private final BiConsumer<GameObject, Integer> removeFunc;
    private final Consumer<Float> addEnergyFunc;

    /**
     * Constructs a new instance of the Flora system.
     *
     * @param groundHeightAt A function to calculate the ground height at a given x-coordinate.
     * @param addFunc A consumer that adds a GameObject to a specific layer.
     * @param removeFunc A consumer that removes a GameObject from a specific layer.
     * @param addEnergyFunc A consumer that adds energy to the avatar when interacting with certain objects.
     */
    public Flora(Function<Float, Float> groundHeightAt, BiConsumer<GameObject, Integer> addFunc,
                 BiConsumer<GameObject, Integer> removeFunc, Consumer<Float> addEnergyFunc) {
        this.groundHeightAtFunc = groundHeightAt;
        this.addFunc = addFunc;
        this.removeFunc = removeFunc;
        this.addEnergyFunc = addEnergyFunc;

    }

    /**
     * Creates trees in the specified range, based on ground height and tree probability.
     * Each tree is placed at a consistent location and has consistent properties for a given seed.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of lists, where each inner list contains the GameObjects
     * representing a single tree.
     */
    public List<List<GameObject>> createInRange(int minX, int maxX) {
        List<List<GameObject>> trees = new ArrayList<>();
        int minXAdjusted = (int) (Math.floor((float) minX / Block.SIZE) * Block.SIZE);
        int maxXAdjusted = (int) (Math.floor((float) maxX / Block.SIZE) * Block.SIZE);
        for (int x = minXAdjusted; x <= maxXAdjusted; x += Block.SIZE) {
            Random treeRandom = new Random(Objects.hash(x, PepseGameManager.seed));
            if (treeRandom.nextFloat() < TREE_PROBABILITY) {
                float groundHeight = this.groundHeightAtFunc.apply((float) x);
                Tree tree = new Tree(new Vector2(x, groundHeight), this.addFunc,
                        this.removeFunc, this.addEnergyFunc);
                trees.add(tree.createTree());
            }
        }
        return trees;
    }

}
