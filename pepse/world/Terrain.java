package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the terrain in the game world.
 * Responsible for generating ground blocks with a noise-based height function and terrain depth.
 *
 * @author Ronnie Rozanski and Gilly Sraya
 * @see Block
 * @see NoiseGenerator
 */
public class Terrain {

    private static final float X0_HEIGHT_ADJUSTER = 2.0f / 3.0f;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int TERRAIN_DEPTH = 20;
    private static final int NOISE_FACTOR = 5 * Block.SIZE;
    private final float baseHeight;
    private final NoiseGenerator noiseGenerator;

    /**
     * Constructs a new Terrain instance.
     *
     * @param windowDimensions The dimensions of the game window.
     */
    public Terrain(Vector2 windowDimensions) {
        this.baseHeight = windowDimensions.y() * X0_HEIGHT_ADJUSTER;
        this.noiseGenerator = new NoiseGenerator(PepseGameManager.seed, (int) this.baseHeight);
    }


    /**
     * Calculates the ground height at a specific x-coordinate using noise generation.
     *
     * @param x The x-coordinate for which the ground height is calculated.
     * @return The height of the ground at the given x-coordinate.
     */
    public float groundHeightAt(float x) {
        float noise = (float) this.noiseGenerator.noise(x, NOISE_FACTOR);
        return this.baseHeight + noise;
    }


    /**
     * Creates ground blocks in a specified range.
     * Generates blocks starting from the calculated ground height and extends
     * down to the terrain depth.
     *
     * @param minX The minimum x-coordinate of the range.
     * @param maxX The maximum x-coordinate of the range.
     * @return A list of generated ground blocks.
     */
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();

        int minXAdjusted = (int) (Math.floor((float) minX / Block.SIZE) * Block.SIZE);
        int maxXAdjusted = (int) (Math.floor((float) maxX / Block.SIZE) * Block.SIZE);

        for (int x = minXAdjusted; x <= maxXAdjusted; x += Block.SIZE) {
            float groundHeight = groundHeightAt(x);
            int topY = (int) (Math.floor(groundHeight / Block.SIZE) * Block.SIZE);
            for (int y = topY; y < topY + TERRAIN_DEPTH * Block.SIZE; y += Block.SIZE) {
                Vector2 position = new Vector2(x, y);
                RectangleRenderable blockRender = new RectangleRenderable
                        (ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                Block block = new Block(position, blockRender);
                block.setTag(PepseGameManager.GROUND_TAG);
                blocks.add(block);
            }
        }
        return blocks;
    }
}
