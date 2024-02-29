package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.List;

public class GenerateTileSide {
    public static TileSide.River generateTileSideRiverWithoutLake() {
        return new TileSide.River(generateZoneMeadow(), generateZoneRiverWithoutLake(), generateZoneMeadow());
    }

    public static TileSide.River generateTileSideRiverWithLake() {
        return new TileSide.River(generateZoneMeadow(), generateZoneRiverWithLake(), generateZoneMeadow());
    }

    public static TileSide.Forest generateTileSideForest() {
        return new TileSide.Forest(generateZoneForest());
    }

    public static TileSide.Meadow generateTileSideMeadow() {
        return new TileSide.Meadow(generateZoneMeadow());
    }

    public static Zone.Meadow generateZoneMeadow() {
        return new Zone.Meadow(0, List.of(new Animal(0, Animal.Kind.DEER)), null);
    }

    public static Zone.River generateZoneRiverWithoutLake() {
        return new Zone.River(0, 0, null);
    }

    public static Zone.River generateZoneRiverWithLake() {
        return new Zone.River(0, 0, generateZoneLake());
    }

    public static Zone.Forest generateZoneForest() {
        return new Zone.Forest(0, Zone.Forest.Kind.PLAIN);
    }

    public static Zone.Lake generateZoneLake() {
        return new Zone.Lake(0, 0, null);
    }

}
