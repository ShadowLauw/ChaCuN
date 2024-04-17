package ch.epfl.chacun.gui;

import javafx.scene.image.Image;

import static java.util.FormatProcessor.FMT;

/**
 * A utility class that loads images from the resources.
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class ImageLoader {
    /**
     * Tile large image pixel size
     */
    public static final int LARGE_TILE_PIXEL_SIZE = 512;
    /**
     * Tile large image display pixel size
     */
    public static final int LARGE_TILE_FIT_SIZE = 256;
    /**
     * Tile normal image pixel size
     */
    public static final int NORMAL_TILE_PIXEL_SIZE = 256;
    /**
     * Tile normal image display pixel size
     */
    public static final int NORMAL_TILE_FIT_SIZE = 128;
    /**
     * Marker image pixel size
     */
    public static final int MARKER_PIXEL_SIZE = 96;
    /**
     * Marker image display pixel size
     */
    public static final int MARKER_FIT_SIZE = 48;

    /**
     * Private constructor to prevent instantiation.
     */
    private ImageLoader() {
    }

    /**
     * Returns the normal size image for the given tile id.
     *
     * @param tileId the id of the tile wanted
     * @return the image for the given tile id in normal size
     */
    public static Image normalImageForTile(int tileId) {
        return new Image(FMT."/\{NORMAL_TILE_PIXEL_SIZE}/%02\{tileId}.jpg");
    }

    /**
     * Returns the large size image for the given tile id.
     *
     * @param tileId the id of the tile wanted
     * @return the image for the given tile id in large size
     */
    public static Image largeImageForTile(int tileId) {
        return new Image(FMT."/\{LARGE_TILE_PIXEL_SIZE}/%02\{tileId}.jpg");
    }
}
