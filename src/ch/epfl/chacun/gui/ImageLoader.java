package ch.epfl.chacun.gui;

import javafx.scene.image.Image;


public final class ImageLoader {

    public static final int LARGE_TILE_PIXEL_SIZE = 512;
    public static final int LARGE_TILE_FIT_SIZE = 256;
    public static final int NORMAL_TILE_PIXEL_SIZE = 256;
    public static final int NORMAL_TILE_FIT_SIZE = 128;
    public static final int MARKER_PIXEL_SIZE = 96;
    public static final int MARKER_FIT_SIZE = 48;
    public static final Image NORMAL_NORMAL_TILE_BACK = new Image("/256/NORMAL.jpg");
    public static final Image NORMAL_MENHIR_TILE_BACK = new Image("/256/MENHIR.jpg");

    private ImageLoader() {}

    public static Image normalImageForTile(int tileId) {
        return new Image(STR."/\{NORMAL_TILE_PIXEL_SIZE}/\{tileId}.jpg");
    }

    public static Image largeImageForTile(int tileId) {
        return new Image(STR."/\{LARGE_TILE_PIXEL_SIZE}/\{tileId}.jpg");
    }
}
