package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LauraTextMakerFrTest {

    final static Map<PlayerColor, String> map = Map.of(
            PlayerColor.RED, "Dalia",
            PlayerColor.BLUE, "Claude",
            PlayerColor.GREEN, "Bachir",
            PlayerColor.YELLOW, "Alice"
    );

    final static TextMakerFr textMaker = new TextMakerFr(map);

    @Test
    void pointsTest() {
        Map<PlayerColor, String> map = Map.of();
        TextMaker textMaker = new TextMakerFr(map);
        assertEquals("1 point", textMaker.points(1));
        assertEquals("2 points", textMaker.points(2));
        assertEquals("3 points", textMaker.points(3));
        assertEquals("4 points", textMaker.points(4));
        assertEquals("5 points", textMaker.points(5));
        assertEquals("6 points", textMaker.points(6));
        assertEquals("7 points", textMaker.points(7));
        assertEquals("0 point", textMaker.points(0));
    }

    @Test
    void playerClosedForestWithMenhirTest() {
        assertEquals("Dalia a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.", textMaker.playerClosedForestWithMenhir(PlayerColor.RED));
        assertEquals("Claude a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.", textMaker.playerClosedForestWithMenhir(PlayerColor.BLUE));
        assertEquals("Bachir a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.", textMaker.playerClosedForestWithMenhir(PlayerColor.GREEN));
        assertEquals("Alice a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.", textMaker.playerClosedForestWithMenhir(PlayerColor.YELLOW));
    }

    @Test
    void playersScoredForestTest() {
        assertEquals("Claude a remporté 6 points en tant qu'occupant·e majoritaire d'une forêt composée de 3 tuiles.", textMaker.playersScoredForest(Set.of(PlayerColor.BLUE), 6, 0, 3));
        assertEquals("Dalia et Alice ont remporté 9 points en tant qu'occupant·e·s majoritaires d'une forêt composée de 3 tuiles et de 1 groupe de champignons.", textMaker.playersScoredForest(Set.of(PlayerColor.YELLOW, PlayerColor.RED), 9, 1, 3));
        assertEquals("Claude, Bachir et Alice ont remporté 12 points en tant qu'occupant·e·s majoritaires d'une forêt composée de 3 tuiles et de 2 groupes de champignons.", textMaker.playersScoredForest(Set.of(PlayerColor.YELLOW, PlayerColor.GREEN, PlayerColor.BLUE), 12, 2, 3));
    }

    @Test
    void playerScoredRiverTest() {
        assertEquals("Dalia a remporté 6 points en tant qu'occupant·e majoritaire d'une rivière composée de 3 tuiles.", textMaker.playersScoredRiver(Set.of(PlayerColor.RED), 6, 0, 3));
        assertEquals("Claude et Alice ont remporté 9 points en tant qu'occupant·e·s majoritaires d'une rivière composée de 3 tuiles et contenant 1 poisson.", textMaker.playersScoredRiver(Set.of(PlayerColor.YELLOW, PlayerColor.BLUE), 9, 1, 3));
        assertEquals("Dalia, Bachir et Alice ont remporté 12 points en tant qu'occupant·e·s majoritaires d'une rivière composée de 3 tuiles et contenant 2 poissons.", textMaker.playersScoredRiver(Set.of(PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.RED), 12, 2, 3));
    }

    @Test
    void playerScoredHuntingTrapTest() {
        EnumMap<Animal.Kind, Integer> mapAnimal = new EnumMap<>(Animal.Kind.class);
        mapAnimal.put(Animal.Kind.DEER, 3);
        mapAnimal.put(Animal.Kind.MAMMOTH, 1);
        mapAnimal.put(Animal.Kind.AUROCHS, 2);
        mapAnimal.put(Animal.Kind.TIGER, 6);
        assertEquals("Bachir a remporté 10 points en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de 1 mammouth, 2 aurochs et 3 cerfs.", textMaker.playerScoredHuntingTrap(PlayerColor.GREEN, 10, mapAnimal));
    }

    @Test
    void playerScoredLogboatTest() {
        assertEquals("Alice a remporté 8 points en plaçant la pirogue dans un réseau hydrographique contenant 4 lacs.", textMaker.playerScoredLogboat(PlayerColor.YELLOW, 8, 4));
    }

    @Test
    void playersScoredMeadowTest() {
        EnumMap<Animal.Kind, Integer> mapAnimal = new EnumMap<>(Animal.Kind.class);
        mapAnimal.put(Animal.Kind.DEER, 1);
        mapAnimal.put(Animal.Kind.TIGER, 18);

        assertEquals("Dalia a remporté 1 point en tant qu'occupant·e majoritaire d'un pré contenant 1 cerf.", textMaker.playersScoredMeadow(Set.of(PlayerColor.RED), 1, mapAnimal));

        mapAnimal.put(Animal.Kind.DEER, 2);
        mapAnimal.put(Animal.Kind.MAMMOTH, 1);
        assertEquals("Claude et Bachir ont remporté 5 points en tant qu'occupant·e·s majoritaires d'un pré contenant 1 mammouth et 2 cerfs.", textMaker.playersScoredMeadow(Set.of(PlayerColor.GREEN, PlayerColor.BLUE), 5, mapAnimal));

        mapAnimal.remove(Animal.Kind.DEER);
        mapAnimal.put(Animal.Kind.AUROCHS, 3);
        assertEquals("Dalia, Claude et Alice ont remporté 10 points en tant qu'occupant·e·s majoritaires d'un pré contenant 1 mammouth et 3 aurochs.", textMaker.playersScoredMeadow(Set.of(PlayerColor.YELLOW, PlayerColor.RED, PlayerColor.BLUE), 10, mapAnimal));
    }

    @Test
    void playersScoredRiverSystemTest() {
        assertEquals("Alice a remporté 9 points en tant qu'occupant·e majoritaire d'un réseau hydrographique contenant 9 poissons.", textMaker.playersScoredRiverSystem(Set.of(PlayerColor.YELLOW), 9, 9));
        assertEquals("Dalia, Claude et Bachir ont remporté 1 point en tant qu'occupant·e·s majoritaires d'un réseau hydrographique contenant 1 poisson.", textMaker.playersScoredRiverSystem(Set.of(PlayerColor.BLUE, PlayerColor.GREEN, PlayerColor.RED), 1, 1));
    }

    @Test
    void playersScoredPitTrapTest() {
        EnumMap<Animal.Kind, Integer> mapAnimal = new EnumMap<>(Animal.Kind.class);
        mapAnimal.put(Animal.Kind.MAMMOTH, 2);
        mapAnimal.put(Animal.Kind.DEER, 2);
        mapAnimal.put(Animal.Kind.TIGER, 6);
        mapAnimal.put(Animal.Kind.AUROCHS, 2);

        assertEquals("Bachir et Alice ont remporté 12 points en tant qu'occupant·e·s majoritaires d'un pré contenant la grande fosse à pieux entourée de 2 mammouths, 2 aurochs et 2 cerfs.", textMaker.playersScoredPitTrap(Set.of(PlayerColor.YELLOW, PlayerColor.GREEN), 12, mapAnimal));
        mapAnimal.remove(Animal.Kind.MAMMOTH);
        mapAnimal.remove(Animal.Kind.DEER);
        mapAnimal.put(Animal.Kind.AUROCHS, 1);
        assertEquals("Dalia a remporté 2 points en tant qu'occupant·e majoritaire d'un pré contenant la grande fosse à pieux entourée de 1 auroch.", textMaker.playersScoredPitTrap(Set.of(PlayerColor.RED), 2, mapAnimal));
    }

    @Test
    void playersScoredRaftTest() {
        assertEquals("Dalia et Claude ont remporté 10 points en tant qu'occupant·e·s majoritaires d'un réseau hydrographique contenant le radeau et 10 lacs.", textMaker.playersScoredRaft(Set.of(PlayerColor.BLUE, PlayerColor.RED), 10, 10));
        assertEquals("Alice a remporté 1 point en tant qu'occupant·e majoritaire d'un réseau hydrographique contenant le radeau et 1 lac.", textMaker.playersScoredRaft(Set.of(PlayerColor.YELLOW), 1, 1));
    }

    @Test
    void playersWonTest() {
        assertEquals("Bachir a remporté la partie avec 111 points !", textMaker.playersWon(Set.of(PlayerColor.GREEN), 111));
        assertEquals("Dalia et Alice ont remporté la partie avec 123 points !", textMaker.playersWon(Set.of(PlayerColor.YELLOW, PlayerColor.RED), 123));
    }

    @Test
    void clickToOccupyTest() {
        assertEquals("Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.", textMaker.clickToOccupy());
    }

    @Test
    void clickToUnoccupyTest() {
        assertEquals("Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.", textMaker.clickToUnoccupy());
    }
}
