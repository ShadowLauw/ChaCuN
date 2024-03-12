package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the message board of the game
 *
 * @param textMaker (TextMaker) the text maker of the game
 * @param messages (List<Message>) the messages of the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    /**
     * Constructs a message board with the given text maker and messages
     *
     * @param textMaker (TextMaker) the text maker of the game
     * @param messages (List<Message>) the messages of the game
     */
    public MessageBoard {
        messages = List.copyOf(messages);
    }

    /**
     * Return a table with all the scorers and their points
     * @return (Map<PlayerColor, Integer>) the table with all the scorers and their points
     */
    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> points = new HashMap<>();
        for (Message message : messages) {
            for (PlayerColor player : message.scorers) {
                points.merge(player, message.points(), Integer::sum);
            }
        }
        return points;
    }

    /**
     * Return the message board with possibly a new message for the closed forest
     * @param forest (Area<Zone.Forest>) the placed tile
     * @return (MessageBoard) the message board with possibly a new message for the closed forest
     */
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        List<Message> newMessages = new ArrayList<>(messages);
        if (forest.isOccupied()) {
            int mushroomGroups = Area.mushroomGroupCount(forest);
            int points = Points.forClosedForest(forest.tileIds().size(), mushroomGroups);
            newMessages.add(new Message(
                    textMaker.playersScoredForest(
                            forest.majorityOccupants(), points, mushroomGroups, forest.zones().size()
                    ),
                    points, forest.majorityOccupants(), forest.tileIds()
            ));
        }
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Return the message board with a new message for the closed forest with menhir
     * @param player (PlayerColor) the player who closed the forest with menhir
     * @param forest (Area<Zone.Forest>) the closed forest with menhir
     * @return (MessageBoard) the message board with a new message for the given closed forest with menhir
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        List<Message> newMessages = new ArrayList<>(messages);
        int mushroomGroups = Area.mushroomGroupCount(forest);
        newMessages.add(new Message(textMaker.playerClosedForestWithMenhir(player),
                0, Set.of(), forest.tileIds()
        ));
        return new MessageBoard(this.textMaker, newMessages);
    }

    /**
     * Return the message board with possibly the message when the player scored a closed river
     * @param river (Area<Zone.River>) the closed river
     * @return (MessageBoard) the message board with possibly the message when the player scored a closed river
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        List<Message> newMessages = new ArrayList<>(messages);
        if (river.isOccupied()) {
            int fishCount = Area.riverFishCount(river);
            int points = Points.forClosedRiver(river.tileIds().size(), fishCount);
            newMessages.add(new Message(
                    textMaker.playersScoredRiver(river.majorityOccupants(), points, fishCount, river.tileIds().size()),
                    points, river.majorityOccupants(), river.tileIds()
            ));
        }
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Return the message board with possibly the message when the player scored a hunting trap
     * @param scorer (PlayerColor) the player who scored the hunting trap
     * @param adjacentMeadow (Area<Zone.Meadow>) the meadow where the hunting trap is placed
     * @return (MessageBoard) the message board with possibly the message when the player scored a hunting trap
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow) {
        List<Message> newMessages = new ArrayList<>(messages);
        Set<Animal> animals = Area.animals(adjacentMeadow, Set.of());
        Map<Animal.Kind, Integer> animalsCount = new HashMap<>();
        for (Animal animal : animals) {
            animalsCount.merge(animal.kind(), 1, Integer::sum);
        }
        int points = Points.forMeadow(
                animalsCount.get(Animal.Kind.MAMMOTH),
                animalsCount.get(Animal.Kind.AUROCHS),
                animalsCount.get(Animal.Kind.DEER)
        );
        if (points > 0) {
            newMessages.add(new Message(
                    textMaker.playerScoredHuntingTrap(scorer, points, animalsCount),
                    points, Set.of(scorer), adjacentMeadow.tileIds()
            ));
        }
        return new MessageBoard(textMaker, newMessages);
    }
    /**
     * Return the message board with the message when the player scored a logboat
     * @param scorer (PlayerColor) the player who scored the logboat
     * @param riverSystem (Area<Zone.Water>) the river system where the logboat is placed
     * @return (MessageBoard) the message board with the message when the player scored a logboat
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        List<Message> newMessages = new ArrayList<>(messages);
        int points = Points.forLogboat(Area.lakeCount(riverSystem));
        newMessages.add(new Message(
                textMaker.playerScoredLogboat(scorer, points, Area.lakeCount(riverSystem)),
                points, Set.of(scorer), riverSystem.tileIds()
        ));
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Return a message board with possibly the message when a player closed a meadow
     * @param meadow (Area<Zone.Meadow>) the closed meadow
     * @param cancelledAnimals (Set<Animal>) the animals that are not counted in the meadow
     * @return (MessageBoard) the message board with possibly the message when a player closed a meadow
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        List<Message> newMessages = new ArrayList<>(messages);
        Set<Animal> animals = Area.animals(meadow, cancelledAnimals);
        Map<Animal.Kind, Integer> animalsCount = new HashMap<>();
        for (Animal animal : animals) {
            animalsCount.merge(animal.kind(), 1, Integer::sum);
        }
        int points = Points.forMeadow(
                animalsCount.get(Animal.Kind.MAMMOTH),
                animalsCount.get(Animal.Kind.AUROCHS),
                animalsCount.get(Animal.Kind.DEER));
        if (meadow.isOccupied() && points > 0) {
            newMessages.add(new Message(
                    textMaker.playersScoredMeadow(meadow.majorityOccupants(), points, animalsCount),
                    points, meadow.majorityOccupants(), meadow.tileIds()
            ));
        }
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Return a message board with possibly the message when a player closed a river system
     * @param riverSystem (Area<Zone.Water>) the closed river system
     * @return (MessageBoard) the message board with possibly the message when a player closed a river system
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        int points = Points.forRiverSystem(Area.riverSystemFishCount(riverSystem));
        List<Message> newMessages = new ArrayList<>(messages);
        if (riverSystem.isOccupied() && points > 0) {
            newMessages.add(new Message(
                    textMaker.playersScoredRiverSystem(
                            riverSystem.majorityOccupants(),
                            points,
                            Area.riverSystemFishCount(riverSystem)
                    ),
                    points, riverSystem.majorityOccupants(), riverSystem.tileIds()
            ));
        }
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Return a message board with possibly the message when a player scored a pit trap
     * @param adjacentMeadow (Area<Zone.Meadow>) the meadow where the pit trap is placed
     * @param cancelledAnimals (Set<Animal>) the animals that are not counted in the meadow
     * @return (MessageBoard) the message board with possibly the message when a player scored a pit trap
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        List<Message> newMessages = new ArrayList<>(messages);
        Set<Animal> animals = Area.animals(adjacentMeadow, cancelledAnimals);
        Map<Animal.Kind, Integer> animalsCount = new HashMap<>();
        for (Animal animal : animals) {
            animalsCount.merge(animal.kind(), 1, Integer::sum);
        }
        int points = Points.forMeadow(
                animalsCount.get(Animal.Kind.MAMMOTH),
                animalsCount.get(Animal.Kind.AUROCHS),
                animalsCount.get(Animal.Kind.DEER));
        if (adjacentMeadow.isOccupied() && points > 0) {
            newMessages.add(new Message(
                    textMaker.playersScoredPitTrap(adjacentMeadow.majorityOccupants(), points, animalsCount),
                    points, adjacentMeadow.majorityOccupants(), adjacentMeadow.tileIds()
            ));
        }
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Return a message board with possibly the message when a player scored a raft
     * @param riverSystem (Area<Zone.Water>) the river system where the raft is placed
     * @return (MessageBoard) the message board with possibly the message when a player scored a raft
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        List<Message> newMessages = new ArrayList<>(messages);
        int points = Points.forRaft(Area.lakeCount(riverSystem));
        if (riverSystem.isOccupied() && points > 0) {
            newMessages.add(new Message(
                    textMaker.playersScoredRaft(riverSystem.majorityOccupants(), points, Area.lakeCount(riverSystem)),
                    points, riverSystem.majorityOccupants(), riverSystem.tileIds()
            ));
        }
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Return a message board with the message when it is the end of the game
     * @param winners (Set<PlayerColor>) the winners of the game
     * @param points (int) the points of the winners
     * @return (MessageBoard) the message board with the message when it is the end of the game
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        List<Message> newMessages = new ArrayList<>(messages);
        newMessages.add(new Message(textMaker.playersWon(winners, points), 0, Set.of(), Set.of()));
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Represents a message of the message board
     *
     * @param text (String) the text of the message
     * @param points (int) the points of the message
     * @param scorers (Set<PlayerColor>) the scorers of the message
     * @param tileIds (Set<Integer>) the tile ids of the message
     */
    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        /**
         * Constructs a message with the given text, points, scorers and tile ids
         *
         * @param text (String) the text of the message
         * @param points (int) the points of the message
         * @param scorers (Set<PlayerColor>) the scorers of the message
         * @param tileIds (Set<Integer>) the tile ids of the message
         */
        public Message {
            Objects.requireNonNull(text);
            Preconditions.checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }
    }
}
