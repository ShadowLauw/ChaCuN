package ch.epfl.chacun;

import java.util.List;
import java.util.function.Predicate;

/**
 * Represents the different decks of tiles
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 *
 * @param startTiles (List<Tile>) the start tiles deck (composed of only the start tile)
 * @param normalTiles (List<Tile>) the normal tiles deck
 * @param menhirTiles (List<Tile>) the menhir tiles deck
 */
public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {
    /**
     * Default Constructor for TileDeck, make copies of all the decks so that it cannot be modified otherwise
     * @param startTiles (List<Tile>) the start tiles deck (composed of only the start tile)
     * @param normalTiles (List<Tile>) the normal tiles deck
     * @param menhirTiles (List<Tile>) the menhir tiles deck
     */
    public TileDecks {
        startTiles = List.copyOf(startTiles);
        normalTiles = List.copyOf(normalTiles);
        menhirTiles = List.copyOf(menhirTiles);
    }
    /**
     * Returns the size of the deck of the given kind
     * @param kind (Tile.Kind) the kind of deck
     * @return (int) the size of the deck of the given kind
     */
    public int deckSize(Tile.Kind kind) {
        return switch(kind) {
            case START -> startTiles.size();
            case NORMAL -> normalTiles.size();
            case MENHIR -> menhirTiles.size();
        };
    }
    /**
     * Returns the top tile of the deck of the given kind
     * @param kind (Tile.Kind) the kind of deck
     * @return (Tile) the top tile of the deck of the given kind
     */
    public Tile topTile (Tile.Kind kind) {
        return switch(kind) {
            case START -> startTiles.isEmpty() ? startTiles.getFirst() : null;
            case NORMAL -> normalTiles.isEmpty() ? normalTiles.getFirst() : null;
            case MENHIR -> menhirTiles.isEmpty() ? menhirTiles.getFirst() : null;
        };
    }
    /**
     * Returns a new TileDecks with the top tile of the given kind removed
     * @param kind (Tile.Kind) the kind of deck
     * @return (TileDecks) a new TileDecks with the top tile of the given kind removed
     * @throws IllegalArgumentException if the deck of the given kind is empty
     */
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        Preconditions.checkArgument(deckSize(kind) > 0);
        return switch (kind) {
            case START -> new TileDecks(removeFirstElementDeck(this.normalTiles), normalTiles, menhirTiles);
            case NORMAL -> new TileDecks(this.startTiles, removeFirstElementDeck(this.normalTiles), this.menhirTiles);
            case MENHIR -> new TileDecks(this.startTiles, this.normalTiles, removeFirstElementDeck(this.menhirTiles));
        };
    }
    /**
     * Returns a List of Tile with the first element removed
     * @param initialDeck (List<Tile>) the initial deck
     * @return (List<Tile>) a List of Tile with the first element removed
     */
    private List<Tile> removeFirstElementDeck(List<Tile> initialDeck) {
        List<Tile> newDeck = List.copyOf(initialDeck);
        newDeck.removeFirst();
        return newDeck;
    }

    /**
     * Returns a new TileDecks with the top tile of the given kind drawn until the given predicate is satisfied
     * @param kind (Tile.Kind) the kind of deck
     * @param predicate (Predicate<Tile>) the predicate to satisfy
     * @return (TileDecks) a new TileDecks with the top tile of the given kind drawn until the given predicate is satisfied
     */
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        TileDecks newTileDeck = new TileDecks(this.startTiles, this.normalTiles, this.menhirTiles);
        Tile topTile = newTileDeck.topTile(kind);
        while (topTile != null && !predicate.test(topTile)){
            newTileDeck = withTopTileDrawn(kind);
            topTile = newTileDeck.topTile(kind);
        }
        return newTileDeck;
    }
}


//ici c'est la class de test c'est pour ça qu'elle n'a pas son fichier a part
//public final class TileHasOneZone implements Predicate<Tile> {
//    @Override
//    public boolean test(Tile tile) {
//        return tile.zones().size() == 1;
//    }
//}
