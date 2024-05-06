package ch.epfl.chacun;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents the different decks of tiles
 *
 * @param startTiles  the start tiles deck (composed of only the start tile)
 * @param normalTiles the normal tiles deck
 * @param menhirTiles the menhir tiles deck
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {
    /**
     * Default Constructor for TileDeck, make copies of all the decks so that it cannot be modified otherwise
     *
     * @param startTiles  the start tiles deck (composed of only the start tile)
     * @param normalTiles the normal tiles deck
     * @param menhirTiles the menhir tiles deck
     */
    public TileDecks {
        startTiles = List.copyOf(startTiles);
        normalTiles = List.copyOf(normalTiles);
        menhirTiles = List.copyOf(menhirTiles);
    }

    /**
     * Returns the size of the deck of the given kind
     *
     * @param kind the kind of deck
     * @return the size of the deck of the given kind
     */
    public int deckSize(Tile.Kind kind) {
        return switch (kind) {
            case START -> startTiles.size();
            case NORMAL -> normalTiles.size();
            case MENHIR -> menhirTiles.size();
        };
    }

    /**
     * Returns the top tile of the deck of the given kind
     *
     * @param kind the kind of deck
     * @return the top tile of the deck of the given kind or null if the deck is empty
     */
    public Tile topTile(Tile.Kind kind) {
        List<Tile> tileDeckToSend = switch (kind) {
            case START -> startTiles;
            case NORMAL -> normalTiles;
            case MENHIR -> menhirTiles;
        };
        return tileDeckToSend.isEmpty() ? null : tileDeckToSend.getFirst();
    }

    /**
     * Returns a new TileDecks with the top tile of the given kind removed
     *
     * @param kind the kind of deck
     * @return a new TileDecks with the top tile of the given kind removed
     * @throws IllegalArgumentException if the deck of the given kind is empty
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        Preconditions.checkArgument(deckSize(kind) > 0);

        return switch (kind) {
            case START -> new TileDecks(startTiles.subList(1, startTiles.size()), normalTiles, menhirTiles);
            case NORMAL -> new TileDecks(startTiles, normalTiles.subList(1, normalTiles.size()), menhirTiles);
            case MENHIR -> new TileDecks(startTiles, normalTiles, menhirTiles.subList(1, menhirTiles.size()));
        };
    }

    /**
     * Returns a new TileDecks with the top tile of the given kind drawn until the given predicate is satisfied
     *
     * @param kind      the kind of deck
     * @param predicate the predicate to satisfy
     * @return a new TileDecks with the top tile of the given kind drawn until the given predicate is satisfied
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        TileDecks newTileDeck = this;

        while (topTileDoesntMatch(newTileDeck, kind, predicate)) {
            newTileDeck = newTileDeck.withTopTileDrawn(kind);
        }

        return newTileDeck;
    }

    /**
     * Returns whether the top tile of the deck of the given kind matches the given predicate
     *
     * @param td        the tile deck
     * @param kind      the kind of deck
     * @param predicate the predicate to satisfy
     * @return whether the top tile of the deck of the given kind matches the given predicate
     */
    private static boolean topTileDoesntMatch(TileDecks td, Tile.Kind kind, Predicate<Tile> predicate) {
        Tile topTile = td.topTile(kind);
        return topTile != null && !predicate.test(topTile);
    }
}
