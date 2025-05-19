package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Flora;
import pepse.world.Cloud;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Manages the Pepse game, including initialization of the game world,
 * dynamic elements like terrain, flora, clouds, and the avatar, and
 * ensuring an infinite scrolling world effect.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see GameManager
 */
public class PepseGameManager extends GameManager {

    /**
     * The seed for random generation in the game.
     */
    public static int seed;

    /**
     * Length of the day-night cycle in seconds.
     */
    public static final int DAY_CYCLE_LENGTH = 30;
    /**
     * Tag for the night game object.
     */
    public static final String NIGHT_TAG = "night";
    /**
     * Tag for the sun game object.
     */
    public static final String SUN_TAG = "sun";
    /**
     * Tag for the sun halo game object.
     */
    public static final String SUN_HALO_TAG = "sunHalo";
    /**
     * Tag for the fruit game object.
     */
    public static final String FRUIT_TAG = "fruit";
    /**
     * Tag for the avatar game object.
     */
    public static final String AVATAR_TAG = "avatar";
    /**
     * Tag for the leaf game object.
     */
    public static final String LEAF_TAG = "leaf";
    /**
     * Tag for the tree trunk game object.
     */
    public static final String TREE_TRUNK_TAG = "treeTrunk";
    /**
     * Tag for the ground (terrain) game object.
     */
    public static final String GROUND_TAG = "ground";
    /**
     * Tag for the block game object.
     */
    public static final String BLOCK_TAG = "block";
    /**
     * Tag for the cloud game object.
     */
    public static final String CLOUD_TAG = "cloud";
    /**
     * Tag for the cloud block game object.
     */
    public static final String CLOUD_BLOCK_TAG = "cloudBlock";
    /**
     * Tag for the rain drop game object.
     */
    public static final String RAIN_DROP_TAG = "rainDrop";
    /**
     * Tag for the sky game object.
     */
    public static final String SKY_TAG = "sky";
    /**
     * Tag for the text.
     */
    public static final String TEXT_TAG = "text";
    /**
     *  the rain drop layer.
     */
    public static final int RAIN_DROP_LAYER = Layer.BACKGROUND + 3;
    /**
     *  the fruit layer.
     */
    public static final int FRUIT_LAYER = Layer.DEFAULT;
    private static final int CLOUD_LAYER = Layer.BACKGROUND + 4;
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int DAYNIGHT_LAYER = Layer.FOREGROUND;
    private static final int SUN_LAYER = Layer.BACKGROUND + 2;
    private static final int SUN_HALO_LAYER = Layer.BACKGROUND + 1;
    private static final int LEAVES_LAYER = Layer.BACKGROUND + 5;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int ENERGY_LAYER = Layer.UI;
    private static final int AVATAR_HEIGHT = 80;
    private static final int AVATAR_INITIAL_X_DIVIDER = 2;
    private static final String ENERGY_TEXT_PREFIX = "ENERGY: ";
    private static final float THRESHOLD = 60;
    private static final Vector2 TEXT_TOP_LEFT = new Vector2(10, 10);
    private static final Vector2 TEXT_DIMENSIONS = new Vector2(100, 20);
    private static final float CAMERA_DIST_FACTOR = 0.5f;
    private static final int MAX_X_RANGE_FACTOR = 200;
    private static final int MIN_X_RANGE = -200;
    private Vector2 avatarLoc;
    private Avatar avatar;
    private Vector2 windowDimensions;
    private Terrain terrain;
    private Flora flora;
    private List<Block> groundBlocks;
    private List<List<GameObject>> trees;
    private int minXRange;
    private int maxXRange;


    /**
     * Initializes the Pepse game by creating and configuring all game objects.
     *
     * @param imageReader     Utility for reading images.
     * @param soundReader     Utility for reading sounds.
     * @param inputListener   Handles user input.
     * @param windowController Manages the game window and its properties.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        this.windowDimensions = windowController.getWindowDimensions();
        this.minXRange = MIN_X_RANGE;
        this.maxXRange = (int) windowDimensions.x() + MAX_X_RANGE_FACTOR;
        this.seed = new Random().nextInt();
        //sky
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, SKY_LAYER);
        //terrain
        this.terrain = new Terrain(windowDimensions);
        createTerrain(this.minXRange, this.maxXRange);
        //night
        GameObject night = Night.create(windowDimensions, DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(night, DAYNIGHT_LAYER);
        //sun
        GameObject sun = Sun.create(windowDimensions, DAY_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, SUN_LAYER);
        //sun halo
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, SUN_HALO_LAYER);
        //avatar
        float initialXLocation = windowDimensions.x() / AVATAR_INITIAL_X_DIVIDER;
        Vector2 avatarInitialPosition = new Vector2(initialXLocation,
                terrain.groundHeightAt(initialXLocation) - AVATAR_HEIGHT);
        this.avatar = new Avatar(avatarInitialPosition, inputListener, imageReader);
        this.avatarLoc = avatar.getCenter();
        gameObjects().addGameObject(avatar, AVATAR_LAYER);
        //display energy
        displayEnergy();
        //flora
        this.flora = new Flora(terrain::groundHeightAt, gameObjects()::addGameObject,
                gameObjects()::removeGameObject,
            avatar::addEnergy);
        createFlora(this.minXRange, this.maxXRange);
        //clouds
        List<GameObject> cloudsBlocks = Cloud.create(windowDimensions, DAY_CYCLE_LENGTH,
                gameObjects()::addGameObject,
                avatar::addListener);
        for(GameObject cloudBlock: cloudsBlocks) {
        gameObjects().addGameObject(cloudBlock, CLOUD_LAYER);
        }
        Vector2 avatarDistFromCam =
                windowDimensions.mult(CAMERA_DIST_FACTOR).subtract(avatarInitialPosition);
        Camera camera = new Camera(avatar, avatarDistFromCam, windowDimensions, windowDimensions);
        setCamera(camera);
    }

    /**
     * Updates the terrain and flora as the avatar moves, ensuring the world remains infinite.
     *
     * @param deltaTime Time elapsed since the last update.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateMinAndMax();
    }

    /*
     * Displays the avatar's energy level on the screen.
     *
     * Updates the text and its position in the game UI.
     */
    private void displayEnergy() {
        TextRenderable energyText = new TextRenderable(ENERGY_TEXT_PREFIX + avatar.getEnergy());
        GameObject energyDisplay = new GameObject(TEXT_TOP_LEFT,
               TEXT_DIMENSIONS, energyText);
        energyDisplay.setCoordinateSpace(danogl.components.CoordinateSpace.CAMERA_COORDINATES);
        energyDisplay.addComponent((deltaTime) -> {
            energyText.setString(ENERGY_TEXT_PREFIX + avatar.getEnergy());
        });
        energyDisplay.setTag(TEXT_TAG);
        gameObjects().addGameObject(energyDisplay, ENERGY_LAYER);
    }

    /*
     * Creates terrain within the specified range.
     *
     * Generates and adds blocks to form the ground, based on the given x-coordinate range.
     */
    private void createTerrain(int minXRange, int maxXRange) {
        this.groundBlocks = this.terrain.createInRange(minXRange,maxXRange);
        for (GameObject block : groundBlocks) {
            gameObjects().addGameObject(block, GROUND_LAYER);
        }
    }

    /*
     * Creates flora (trees and fruits) within the specified range.
     *
     * Spawns trees and their associated components (leaves, fruits) in the
     * specified x-coordinate range.
     */
    private void createFlora(int minXRange, int maxXRange) {
        this.trees = flora.createInRange(minXRange, maxXRange);
        for (List<GameObject> tree : trees) {
            for (GameObject treePart : tree) {
                String tag = treePart.getTag();
                if (TREE_TRUNK_TAG.equals(tag)) {
                    gameObjects().addGameObject(treePart, GROUND_LAYER);
                } else if (LEAF_TAG.equals(tag)) {
                    gameObjects().addGameObject(treePart, LEAVES_LAYER);
                } else if(FRUIT_TAG.equals(tag)) {
                    gameObjects().addGameObject(treePart, FRUIT_LAYER);
                }
            }
        }
    }


    /*
     * Updates the terrain and flora boundaries as the avatar moves.
     *
     * Dynamically adjusts the world to ensure continuous generation of terrain and flora
     * while removing objects that move out of the visible frame.
     */
    private void updateMinAndMax() {
        boolean removeFromLeft;
        float CurrentAvatarX = this.avatar.getCenter().x();
        float PreviousAvatarX = this.avatarLoc.x();
        float avaterMovement = CurrentAvatarX - PreviousAvatarX; //not abs?
        if(avaterMovement > THRESHOLD) {
            removeFromLeft = true;
            int newMax = (int) (avaterMovement + this.maxXRange);
            createTerrain(this.maxXRange, newMax);
            createFlora(this.maxXRange, newMax);
            cleanFrame(this.minXRange + avaterMovement, removeFromLeft);
            this.maxXRange = newMax;
            this.minXRange += (int) avaterMovement;
            this.avatarLoc = this.avatar.getCenter();
        }
        else if(avaterMovement < -THRESHOLD) {
            removeFromLeft = false;
            int newMin = (int) (this.minXRange + avaterMovement);
            createTerrain(newMin, this.minXRange);
            createFlora(newMin, this.minXRange);
            cleanFrame(this.maxXRange + avaterMovement, removeFromLeft);
            this.minXRange = newMin;
            this.maxXRange += (int) avaterMovement;
            this.avatarLoc = this.avatar.getCenter();
        }

    }


    /*
     * Removes game objects that are outside the visible frame.
     *
     * Identifies and deletes ground blocks and flora outside the given boundary
     * based on the avatar's position.
     */
    private void cleanFrame(float boundary, boolean removeFromLeft) {
        ArrayList<Block> groundToRemove = new ArrayList<>();
        for (Block block : this.groundBlocks) {
            if ((removeFromLeft && block.getTopLeftCorner().x() <= boundary) ||
                    (!removeFromLeft && block.getTopLeftCorner().x() >= boundary)) {
                groundToRemove.add(block);
                gameObjects().removeGameObject(block, GROUND_LAYER);
            }
        }
        this.groundBlocks.removeAll(groundToRemove);

        for (List<GameObject> tree : trees) {
            for (GameObject treePart : tree) {
                if (removeFromLeft) {
                    if (TREE_TRUNK_TAG.equals(treePart.getTag())) {
                        if (treePart.getCenter().x() <= boundary) {
                            removeTree(tree);
                            break;
                        }
                    }
                } else if (!removeFromLeft) {
                    if (TREE_TRUNK_TAG.equals(treePart.getTag())) {
                        if (treePart.getCenter().x() >= boundary) {
                            removeTree(tree);
                            break;
                        }
                    }
                }
            }
        }
    }

//        ArrayList<GameObject> floraToRemove = new ArrayList<>();
//        for (List<GameObject> tree : trees) {
//            for (GameObject treePart : tree) {
//                if (removeFromLeft) {
//                    if (treePart.getCenter().x() <= boundary) {
//                        floraToRemove.add(treePart);
//                        String tag = treePart.getTag();
//                        removeTreePart(treePart, tag);
//                    }
//                }
//                else {
//                    if (treePart.getCenter().x() >= boundary) {
//                        floraToRemove.add(treePart);
//                        String tag = treePart.getTag();
//                        removeTreePart(treePart, tag);
//                    }
//                }
//            }
//            this.trees.removeAll(floraToRemove);
//        }
//    }


    /*
     * Removes a tree and all its parts from the game.
     *
     * Deletes the entire tree (trunk, leaves, and fruits) from the game objects
     * and internal list of trees.
     */
    private void removeTree(List<GameObject> tree) {
        for (GameObject treePart : tree) {
            String tag = treePart.getTag();
            removeTreePart(treePart, tag);
        }
        this.trees.remove(tree);
    }

    /*
     * Removes a specific tree part based on its tag.
     *
     * Deletes a tree component (trunk, leaf, or fruit) from the game,
     * depending on its tag and associated layer.
     */
    private void removeTreePart(GameObject treePart, String tag) {
        if (TREE_TRUNK_TAG.equals(tag)) {
            gameObjects().removeGameObject(treePart, GROUND_LAYER);
        } else if (LEAF_TAG.equals(tag)) {
            gameObjects().removeGameObject(treePart, LEAVES_LAYER);
        } else if (FRUIT_TAG.equals(tag)) {
            gameObjects().removeGameObject(treePart, FRUIT_LAYER);
        }
    }


    /**
     * Main entry point to run the Pepse game.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}