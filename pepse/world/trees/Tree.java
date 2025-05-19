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

/**
 * Represents a tree in the game world. A tree consists of a trunk, leaves, and optionally fruits.
 * Trees are procedurally generated with consistent randomness based on their location.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameObject
 * @see Leaf
 * @see Fruit
 * @see TreeTrunk
 */
public class Tree {

    private static final int MIN_TRUNK_HEIGHT = 3;
    private static final int MAX_TRUNK_HEIGHT = 7;
    private static final int LEAF_GRID_SIZE = 7;
    private static final int START_LEAF_FACTOR = 3;
    private static final float LEAF_PROBABILITY = 0.8f;
    private static final int TRUNK_HEIGHT_FACTOR = 1;
    private static final int FRUIT_FACTOR = 1;
    private final Vector2 treeLoc;
    private final float groundHeight;
    private final List<GameObject> treeParts;
    private int trunkHeight;
    private final BiConsumer<GameObject, Integer> addFunc;
    private final BiConsumer<GameObject, Integer> removeFunc;
    private final Consumer<Float> addEnergyFunc;
    private Random random;

    /**
     * Constructs a new Tree instance at the specified location.
     *
     * @param treeLoc The location of the tree's base.
     * @param addFunc A function to add game objects to the game world.
     * @param removeFunc A function to remove game objects from the game world.
     * @param addEnergyFunc A function to increase the energy of the avatar.
     */
    public Tree(Vector2 treeLoc, BiConsumer<GameObject, Integer> addFunc,
                BiConsumer<GameObject, Integer> removeFunc, Consumer<Float> addEnergyFunc) {
        this.treeLoc = treeLoc;
        this.groundHeight = treeLoc.y();
        this.addFunc = addFunc;
        this.removeFunc = removeFunc;
        this.addEnergyFunc = addEnergyFunc;
        this.treeParts = new ArrayList<>();
        this.random = new Random(Objects.hash(this.treeLoc.x(), PepseGameManager.seed));
    }

    /**
     * Generates the tree, including its trunk, leaves, and optional fruits.
     *
     * @return A list of all game objects that compose the tree.
     */
    public List<GameObject> createTree() {
        createTreeTrunk();
        createLeaves();
        return this.treeParts;
    }

    /*
     * Creates the leaves for the tree in a grid pattern.
     * Some leaves may contain fruits based on random probability.
     */
    private void createLeaves() {
        Vector2 leafStart = new Vector2(treeLoc.x() - (Block.SIZE * START_LEAF_FACTOR),
                groundHeight - (this.trunkHeight * Block.SIZE));
        for (int x = 0; x < LEAF_GRID_SIZE; x++) {
            for (int y = 0; y < LEAF_GRID_SIZE; y++) {
                if (this.random.nextFloat() < LEAF_PROBABILITY) {
                    Vector2 leafPosition = new Vector2(leafStart.x() + (x * Block.SIZE),
                            leafStart.y() - (y * Block.SIZE));
                    treeParts.add(new Leaf(leafPosition));
                    // Randomly decide to add a fruit on/near the leaf
                    if (this.random.nextFloat() < LEAF_PROBABILITY / FRUIT_FACTOR) {
                        Vector2 fruitPosition = leafPosition.add(new Vector2(
                                (Block.SIZE - Fruit.FRUIT_SIZE) / FRUIT_FACTOR, -Fruit.FRUIT_SIZE));
                        Fruit fruit = new Fruit(fruitPosition, this.addFunc,
                                this.removeFunc, this.addEnergyFunc);
                        treeParts.add(fruit);
                    }
                }
            }
        }
    }

    /*
     * Creates the trunk of the tree with a random height.
     * Each trunk block is stacked vertically.
     */
    private void createTreeTrunk() {
        this.trunkHeight = MIN_TRUNK_HEIGHT + random.nextInt(MAX_TRUNK_HEIGHT - MIN_TRUNK_HEIGHT
                + TRUNK_HEIGHT_FACTOR);
        for (int i = 0; i < trunkHeight; i++) {
            Vector2 trunkPosition = new Vector2(treeLoc.x(), groundHeight - (i * Block.SIZE));
            treeParts.add(new TreeTrunk(trunkPosition));
        }
    }

}
