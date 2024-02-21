package ch.epfl.chacun;

import java.util.List;

//ATTENTION
//il est marqué dans le sujet que l'interface est "scellée", je n'ai aucune foutre idée de ce que ça peut dire mais apparemment c'est comme pour les records, on verra plus tard
//ducoup faut modifier cette interface absolument
/// effacer le commentaire si et seulement si c'est résolu
public sealed interface TileSide {
    List<Zone> zones();
    boolean isSameKindAs(TileSide that);

    record Forest(Zone.Forest forest) implements TileSide {
        @Override
        public List<Zone> zones() {
            return List.of(forest);
        }
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Forest;
        }
    };
    record Meadow(Zone.Meadow meadow) implements TileSide {
        @Override
        public List<Zone> zones() {
            return List.of(meadow);
        }
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof Meadow;
        }
    };
    record River(Zone.Meadow meadow1, Zone.River river, Zone.Meadow meadow2) implements TileSide {
        @Override
        public List<Zone> zones() {
            return List.of(meadow1, river, meadow2);
        }
        @Override
        public boolean isSameKindAs(TileSide that) {
            return that instanceof River;
        }
    };
}
