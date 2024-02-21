package ch.epfl.chacun;

import java.util.List;
import java.util.function.Predicate;

public record TileDecks(List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {
    public TileDecks (List<Tile> startTiles, List<Tile> normalTiles, List<Tile> menhirTiles) {
        this.startTiles = List.copyOf(startTiles);
        this.normalTiles = List.copyOf(normalTiles);
        this.menhirTiles = List.copyOf(menhirTiles);
    }
    public int deckSize(Tile.Kind kind) {
        switch (kind) {
            case START -> {
                return startTiles.size();
            }
            case NORMAL -> {
                return normalTiles.size();
            }
            case MENHIR -> {
                return menhirTiles.size();
            }
        }
        throw new IllegalArgumentException();
    }
    //attention, potentiellement il veut qu'on l'implémente en inversé, donc faire gaffe au changement d'énnoncé jusqu'à la dernière minute
    public Tile topTile (Tile.Kind kind) {
        switch (kind) {
            case START -> {
                return startTiles.get(0);
            }
            case NORMAL -> {
                return normalTiles.get(0);
            }
            case MENHIR -> {
                return menhirTiles.get(0);
            }
        }
        throw new IllegalArgumentException();
    }
    //ici c'est bancal vu que j'utilise la méthode remove des arrayList je crois, et pas des lists
    public TileDecks withTopTileDrawn(Tile.Kind kind) {
        Preconditions.checkArgument(deckSize(kind) > 0);
        switch (kind) {
            case START -> {
                return new TileDecks(removeFirstElementDeck(this.startTiles), this.normalTiles, this.menhirTiles);
            }
            case NORMAL -> {
                return new TileDecks(this.startTiles, removeFirstElementDeck(this.normalTiles), this.menhirTiles);
            }
            case MENHIR -> {
                return new TileDecks(this.startTiles, this.normalTiles, removeFirstElementDeck(this.menhirTiles));
            }
            default -> {
                throw new IllegalArgumentException();
            }
        }
    }
    private List<Tile> removeFirstElementDeck(List<Tile> initialDeck) {
        Preconditions.checkArgument(!initialDeck.isEmpty());
        List<Tile> newDeck = List.copyOf(initialDeck);
        newDeck.remove(0);
        return newDeck;
    }
    public TileDecks withTopTileDrawnUntil(Tile.Kind kind, Predicate<Tile> predicate) {
        TileDecks newTileDeck = new TileDecks(this.startTiles, this.normalTiles, this.menhirTiles);
        while (!predicate.test(newTileDeck.topTile(kind))){
            newTileDeck = withTopTileDrawn(kind);
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