package ch.epfl.chacun;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class LauraMessageBoardTest {
    @Test
    void messageBoardThrows() {
        assertThrows(NullPointerException.class, () -> {
            new MessageBoard.Message(null, 0, Set.of(), Set.of());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new MessageBoard.Message("test", -1, Set.of(), Set.of());
        });
    }
    @Test
    void messageBoadIsImmutable() {
        Set<PlayerColor> color = new HashSet<>(Set.of(PlayerColor.PURPLE, PlayerColor.GREEN));
        Set<Integer> tileIds = new HashSet<>(Set.of(1, 2, 3));
        MessageBoard.Message message = new MessageBoard.Message("test", 0, color, tileIds);
        color.add(PlayerColor.RED);
        tileIds.add(4);
        assertNotEquals(color, message.scorers());
        assertNotEquals(tileIds, message.tileIds());
    }

    @Test
    void messageBoardIsImmutable2() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 0, Set.of(), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        messages.add(new MessageBoard.Message("hhhhushu", 0, Set.of(), Set.of()));
        assertNotEquals(messageBoard.messages(), messages);
    }

    @Test
    void pointsWorks() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        messages.add(new MessageBoard.Message("test2", 3, Set.of(PlayerColor.RED, PlayerColor.BLUE), Set.of()));
        messages.add(new MessageBoard.Message("test3", 1, Set.of(PlayerColor.YELLOW), Set.of()));
        Map<PlayerColor, Integer> map = new HashMap<>();
        map.put(PlayerColor.RED, 5);
        map.put(PlayerColor.PURPLE, 2);
        map.put(PlayerColor.BLUE, 3);
        map.put(PlayerColor.YELLOW, 1);
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        assertEquals(map, messageBoard.points());
        assertEquals(new HashMap<>(), new MessageBoard(null, new ArrayList<>()).points());
    }

    @Test
    void withScoredForestWorksWithOccupiedForest() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Forest> forest = new Area<>(Set.of((Zone.Forest)TileReader.readTileFromCSV(56).e().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(20).s().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(87).e().zones().getFirst()), List.of(PlayerColor.GREEN, PlayerColor.PURPLE), 0);
        messageBoard = messageBoard.withScoredForest(forest);
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredForest(Set.of(PlayerColor.PURPLE,PlayerColor.GREEN), 9, 1, 3),
                9, Set.of(PlayerColor.GREEN, PlayerColor.PURPLE), Set.of(56, 20, 87)
        ));
        assertEquals(messages, messageBoard.messages());
    }
    @Test
    void withScoredForestWorksWithOccupiedForest2() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Forest> forest = new Area<>(Set.of((Zone.Forest)TileReader.readTileFromCSV(56).e().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(20).s().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(87).e().zones().getFirst()), List.of(PlayerColor.GREEN, PlayerColor.PURPLE, PlayerColor.PURPLE), 0);
        messageBoard = messageBoard.withScoredForest(forest);
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredForest(Set.of(PlayerColor.PURPLE), 9, 1, 3),
                9, Set.of(PlayerColor.PURPLE), Set.of(56, 20, 87)
        ));
        assertEquals(messages, messageBoard.messages());
    }


    @Test
    void withScoredForestWorksWithUnoccupiedForest() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Forest> forest = new Area<>(Set.of((Zone.Forest)TileReader.readTileFromCSV(56).e().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(20).s().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(87).e().zones().getFirst()), List.of(), 0);
        messageBoard = messageBoard.withScoredForest(forest);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withClosedForestWithMenhir() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Forest> forest = new Area<>(Set.of((Zone.Forest)TileReader.readTileFromCSV(56).e().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(20).s().zones().getFirst(), (Zone.Forest)TileReader.readTileFromCSV(87).e().zones().getFirst()), List.of(), 0);
        messageBoard = messageBoard.withClosedForestWithMenhir(PlayerColor.RED, forest);
        messages.add(new MessageBoard.Message(
                textMaker.playerClosedForestWithMenhir(PlayerColor.RED),
                0, Set.of(), Set.of(56,20,87)
        ));
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredRiverWorksOccupied() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.River> river = new Area<>(Set.of((Zone.River)TileReader.readTileFromCSV(56).w().zones().get(1), (Zone.River)TileReader.readTileFromCSV(20).n().zones().get(1), (Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1)), List.of(PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE), 0);
        messageBoard = messageBoard.withScoredRiver(river);
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredRiver(Set.of(PlayerColor.GREEN), 8, 5, 3),
                8, Set.of(PlayerColor.GREEN), Set.of(56, 20, 87)
        ));
        assertEquals(messages, messageBoard.messages());
    }
    @Test
    void withScoredRiverWorksOccupied2() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.River> river = new Area<>(Set.of((Zone.River)TileReader.readTileFromCSV(56).w().zones().get(1), (Zone.River)TileReader.readTileFromCSV(20).n().zones().get(1), (Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1)), List.of(PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.RED), 0);
        messageBoard = messageBoard.withScoredRiver(river);
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredRiver(Set.of(PlayerColor.GREEN, PlayerColor.RED), 8, 5, 3),
                8, Set.of(PlayerColor.GREEN, PlayerColor.RED), Set.of(56, 20, 87)
        ));
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredRiverWorksUnoccupied() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.River> river = new Area<>(Set.of((Zone.River)TileReader.readTileFromCSV(56).w().zones().get(1), (Zone.River)TileReader.readTileFromCSV(20).n().zones().get(1), (Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1)), List.of(), 0);
        messageBoard = messageBoard.withScoredRiver(river);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredHuntingTrapWorksWithPoints() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of((Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2), (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(), (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst()), List.of(), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.AUROCHS, 2);
        messageBoard = messageBoard.withScoredHuntingTrap(PlayerColor.RED, meadow);
        messages.add(new MessageBoard.Message(
                textMaker.playerScoredHuntingTrap(PlayerColor.RED, 4, map),
                4, Set.of(PlayerColor.RED), Set.of(56,20,87)
        ));
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredHuntingTrapWorksWithoutPoints() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of((Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().getFirst(), (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().get(2), (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst()), List.of(), 0);
        messageBoard = messageBoard.withScoredHuntingTrap(PlayerColor.RED, meadow);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredHuntingTrapWorksWithPoints2() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of((Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()), List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.RED), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.AUROCHS, 2);
        map.put(Animal.Kind.DEER, 1);
        map.put(Animal.Kind.MAMMOTH, 1);
        map.put(Animal.Kind.TIGER, 1);
        messageBoard = messageBoard.withScoredHuntingTrap(PlayerColor.RED, meadow);
        messages.add(new MessageBoard.Message(
                textMaker.playerScoredHuntingTrap(PlayerColor.RED, 8, map),
                8, Set.of(PlayerColor.RED), Set.of(56,20,87, 81, 30)
        ));
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredLogboatWorks() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Water> waterArea = new Area<>(Set.of(
                (Zone.Water)TileReader.readTileFromCSV(56).w().zones().get(1),
                ((Zone.River)TileReader.readTileFromCSV(56).w().zones().get(1)).lake(),
                (Zone.River)TileReader.readTileFromCSV(20).n().zones().get(1),
                (Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1),
                ((Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1)).lake()), List.of(), 0);
        messageBoard = messageBoard.withScoredLogboat(PlayerColor.RED, waterArea);
        messages.add(new MessageBoard.Message(
                textMaker.playerScoredLogboat(PlayerColor.RED, 4, 2),
                4, Set.of(PlayerColor.RED), Set.of(56,20,87)
        ));
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredMeadowWorksWithPointsAndOccupants() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of((Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()), List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.YELLOW), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.AUROCHS, 2);
        map.put(Animal.Kind.MAMMOTH, 1);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(3000, Animal.Kind.DEER));
        cancelledAnimals.add(new Animal(8101, Animal.Kind.TIGER));
        messageBoard = messageBoard.withScoredMeadow(meadow, Set.copyOf(cancelledAnimals));
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredMeadow(Set.of(PlayerColor.GREEN, PlayerColor.RED), 7, map),
                7, Set.of(PlayerColor.RED, PlayerColor.GREEN), Set.of(56,20,87, 81, 30)
        ));
        assertEquals(messages, messageBoard.messages());
    }
    @Test
    void withScoredMeadowWorksWithPointsAndOccupants2() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of((Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()), List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.YELLOW, PlayerColor.YELLOW), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.AUROCHS, 2);
        map.put(Animal.Kind.DEER, 1);
        map.put(Animal.Kind.TIGER, 1);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(8100, Animal.Kind.MAMMOTH));
        messageBoard = messageBoard.withScoredMeadow(meadow, Set.copyOf(cancelledAnimals));
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredMeadow(Set.of(PlayerColor.GREEN, PlayerColor.RED, PlayerColor.YELLOW), 5, map),
                5, Set.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.YELLOW), Set.of(56,20,87, 81, 30)
        ));
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredMeadowWorkWithoutPoints() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of((Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()), List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.GREEN, PlayerColor.GREEN), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.TIGER, 1);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(8100, Animal.Kind.MAMMOTH));
        cancelledAnimals.add(new Animal(3000, Animal.Kind.DEER));
        cancelledAnimals.add(new Animal(2000, Animal.Kind.AUROCHS));
        cancelledAnimals.add(new Animal(5600, Animal.Kind.AUROCHS));
        messageBoard = messageBoard.withScoredMeadow(meadow, Set.copyOf(cancelledAnimals));
        assertEquals(messages, messageBoard.messages());
    }
    @Test
    void withScoredMeadowWorksWithoutOccupants() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of((Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()), List.of(), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.AUROCHS, 2);
        map.put(Animal.Kind.DEER, 1);
        map.put(Animal.Kind.TIGER, 1);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(8100, Animal.Kind.MAMMOTH));
        messageBoard = messageBoard.withScoredMeadow(meadow, Set.copyOf(cancelledAnimals));
        assertEquals(messages, messageBoard.messages());
    }
    @Test
    void withScoredMeadowWorksWithoutPointsWithoutOccupants() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of((Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()), List.of(), 0);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(8100, Animal.Kind.MAMMOTH));
        cancelledAnimals.add(new Animal(3000, Animal.Kind.DEER));
        cancelledAnimals.add(new Animal(2000, Animal.Kind.AUROCHS));
        cancelledAnimals.add(new Animal(5600, Animal.Kind.AUROCHS));
        messageBoard = messageBoard.withScoredMeadow(meadow, Set.copyOf(cancelledAnimals));
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredRiverSystemWorksWithPointsAndOccupants() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Water> waterArea = new Area<>(Set.of(
                (Zone.River)TileReader.readTileFromCSV(56).w().zones().get(1),
                ((Zone.River) TileReader.readTileFromCSV(56).w().zones().get(1)).lake(),
                (Zone.River)TileReader.readTileFromCSV(20).n().zones().get(1),
                (Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1),
        ((Zone.River) TileReader.readTileFromCSV(87).w().zones().get(1)).lake()), List.of(PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.BLUE), 0);
        messageBoard = messageBoard.withScoredRiverSystem(waterArea);
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredRiverSystem(Set.of(PlayerColor.GREEN, PlayerColor.BLUE), 5, 5),
                5, Set.of(PlayerColor.BLUE, PlayerColor.GREEN), Set.of(56, 20, 87)
        ));
        assertEquals(messages, messageBoard.messages());
    }
    @Test
    void withScoredRiverSystemWorksWithoutPoints() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Water> waterArea = new Area<>(Set.of(
                (Zone.Water)TileReader.readTileFromCSV(21).s().zones().get(1),
                (Zone.Water)TileReader.readTileFromCSV(22).n().zones().get(1)
        ), List.of(PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.BLUE), 0);
        messageBoard = messageBoard.withScoredRiverSystem(waterArea);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredRiverSystemWorksWithoutOccupants() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Water> waterArea = new Area<>(Set.of(
                (Zone.River)TileReader.readTileFromCSV(56).w().zones().get(1),
                ((Zone.River) TileReader.readTileFromCSV(56).w().zones().get(1)).lake(),
                (Zone.River)TileReader.readTileFromCSV(20).n().zones().get(1),
                (Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1),
                ((Zone.River) TileReader.readTileFromCSV(87).w().zones().get(1)).lake()), List.of(), 0);
        messageBoard = messageBoard.withScoredRiverSystem(waterArea);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredRiverSystemWorksWithoutOccupantsWithoutPoints() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of()));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Water> waterArea = new Area<>(Set.of(
                (Zone.Water)TileReader.readTileFromCSV(21).s().zones().get(1),
                (Zone.Water)TileReader.readTileFromCSV(22).n().zones().get(1)
        ), List.of(), 0);
        messageBoard = messageBoard.withScoredRiverSystem(waterArea);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredPitTapWorksWithPointsAndOccupants() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message("test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of(1, 2, 3)));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of(
                (Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()), List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.YELLOW), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.AUROCHS, 2);
        map.put(Animal.Kind.MAMMOTH, 1);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(3000, Animal.Kind.DEER));
        cancelledAnimals.add(new Animal(8101, Animal.Kind.TIGER));
        messageBoard = messageBoard.withScoredPitTrap(meadow, cancelledAnimals);
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredPitTrap(Set.of(PlayerColor.RED,PlayerColor.GREEN), 7, map),
                7, Set.of(PlayerColor.RED, PlayerColor.GREEN), Set.of(56,20,87, 81, 30)
        ));
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredPitTrapWorksWithoutOccupant() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message(
                "test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of(1, 2, 3)
        ));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of(
                (Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()
        ), List.of(), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.AUROCHS, 2);
        map.put(Animal.Kind.MAMMOTH, 1);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(3000, Animal.Kind.DEER));
        cancelledAnimals.add(new Animal(8101, Animal.Kind.TIGER));
        messageBoard = messageBoard.withScoredPitTrap(meadow, cancelledAnimals);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredPitTrapWorksWithoutPoints() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message(
                "test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of(1, 2, 3)
        ));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of(
                (Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()
        ), List.of(PlayerColor.RED, PlayerColor.RED, PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.YELLOW), 0);
        Map<Animal.Kind, Integer> map = new HashMap<>();
        map.put(Animal.Kind.TIGER, 1);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(8100, Animal.Kind.MAMMOTH));
        cancelledAnimals.add(new Animal(3000, Animal.Kind.DEER));
        cancelledAnimals.add(new Animal(2000, Animal.Kind.AUROCHS));
        cancelledAnimals.add(new Animal(5600, Animal.Kind.AUROCHS));
        messageBoard = messageBoard.withScoredPitTrap(meadow, cancelledAnimals);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredPitTrapWorksWithoutPointsWithoutOccupant() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message(
                "test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of(1, 2, 3)
        ));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Meadow> meadow = new Area<>(Set.of(
                (Zone.Meadow)TileReader.readTileFromCSV(56).w().zones().get(2),
                (Zone.Meadow)TileReader.readTileFromCSV(20).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(87).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(81).n().zones().getFirst(),
                (Zone.Meadow)TileReader.readTileFromCSV(30).n().zones().getFirst()
        ), List.of(), 0);
        Set<Animal> cancelledAnimals = new HashSet<>();
        cancelledAnimals.add(new Animal(8100, Animal.Kind.MAMMOTH));
        cancelledAnimals.add(new Animal(3000, Animal.Kind.DEER));
        cancelledAnimals.add(new Animal(2000, Animal.Kind.AUROCHS));
        cancelledAnimals.add(new Animal(5600, Animal.Kind.AUROCHS));
        messageBoard = messageBoard.withScoredPitTrap(meadow, cancelledAnimals);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withScoredRaftWorksWithOccupant() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message(
                "test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of(1, 2, 3)
        ));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Water> waterArea = new Area<>(Set.of(
                (Zone.Water)TileReader.readTileFromCSV(56).w().zones().get(1),
                ((Zone.River) TileReader.readTileFromCSV(56).w().zones().get(1)).lake(),
                (Zone.River)TileReader.readTileFromCSV(20).n().zones().get(1),
                (Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1),
                ((Zone.River) TileReader.readTileFromCSV(87).w().zones().get(1)).lake()), List.of(PlayerColor.GREEN, PlayerColor.GREEN, PlayerColor.RED, PlayerColor.BLUE, PlayerColor.BLUE), 0);
        messageBoard = messageBoard.withScoredRaft(waterArea);
        messages.add(new MessageBoard.Message(
                textMaker.playersScoredRaft(Set.of(PlayerColor.GREEN, PlayerColor.BLUE), 2, 2),
                2, Set.of(PlayerColor.GREEN, PlayerColor.BLUE), Set.of(56,20,87)
        ));
        assertEquals(messages, messageBoard.messages());
    }
    @Test
    void withScoredRaftWorksWithoutOccupant() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        messages.add(new MessageBoard.Message(
                "test", 2, Set.of(PlayerColor.RED, PlayerColor.PURPLE), Set.of(1, 2, 3)
        ));
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        Area<Zone.Water> waterArea = new Area<>(Set.of(
                (Zone.Water)TileReader.readTileFromCSV(56).w().zones().get(1),
                ((Zone.River) TileReader.readTileFromCSV(56).w().zones().get(1)).lake(),
                (Zone.River)TileReader.readTileFromCSV(20).n().zones().get(1),
                (Zone.River)TileReader.readTileFromCSV(87).w().zones().get(1),
                ((Zone.River) TileReader.readTileFromCSV(87).w().zones().get(1)).lake()), List.of(), 0);
        messageBoard = messageBoard.withScoredRaft(waterArea);
        assertEquals(messages, messageBoard.messages());
    }

    @Test
    void withWinnersWorksWithOneWinner() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        messageBoard = messageBoard.withWinners(Set.of(PlayerColor.RED), 36);
        messages.add(new MessageBoard.Message(
                textMaker.playersWon(Set.of(PlayerColor.RED), 36),
                0, Set.of(), Set.of()
        ));
        assertEquals(messages, messageBoard.messages());
    }
    @Test
    void withWinnersWorksWithMultipleWinner() {
        TextMaker textMaker = new LauraTextMakerClass();
        List<MessageBoard.Message> messages = new ArrayList<>();
        MessageBoard messageBoard = new MessageBoard(textMaker, messages);
        messageBoard = messageBoard.withWinners(Set.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.BLUE), 36);
        messages.add(new MessageBoard.Message(
                textMaker.playersWon(Set.of(PlayerColor.RED, PlayerColor.GREEN, PlayerColor.BLUE), 36),
                0, Set.of(), Set.of()
        ));
        assertEquals(messages, messageBoard.messages());
    }
}
