package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the message board of the game
 *
 * @param textMaker the text maker of the game
 * @param messages  the messages of the game
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    /**
     * Constructs a message board with the given text maker and messages
     *
     * @param textMaker the text maker of the game
     * @param messages  the messages of the game
     */
    public MessageBoard {
        messages = List.copyOf(messages);
    }

    /**
     * Return a table with all the scorers and their points
     *
     * @return the table with all the scorers and their points
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
     *
     * @param forest the placed tile
     * @return the message board with possibly a new message for the closed forest
     */
    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if (forest.isOccupied()) {
            List<Message> newMessages = new ArrayList<>(messages);
            int mushroomGroups = Area.mushroomGroupCount(forest);
            int points = Points.forClosedForest(forest.tileIds().size(), mushroomGroups);
            newMessages.add(new Message(
                    textMaker.playersScoredForest(
                            forest.majorityOccupants(), points, mushroomGroups, forest.zones().size()
                    ),
                    points, forest.majorityOccupants(), forest.tileIds()
            ));
            return new MessageBoard(textMaker, newMessages);
        }
        return this;
    }

    /**
     * Return the message board with a new message for the closed forest with menhir
     *
     * @param player the player who closed the forest with menhir
     * @param forest the closed forest with menhir
     * @return the message board with a new message for the given closed forest with menhir
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        List<Message> newMessages = new ArrayList<>(messages);
        newMessages.add(new Message(
                textMaker.playerClosedForestWithMenhir(player),
                0, Set.of(), forest.tileIds()
        ));
        return new MessageBoard(this.textMaker, newMessages);
    }

    /**
     * Return the message board with possibly the message when the player scored a closed river
     *
     * @param river the closed river
     * @return the message board with possibly the message when the player scored a closed river
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (river.isOccupied()) {
            List<Message> newMessages = new ArrayList<>(messages);
            int fishCount = Area.riverFishCount(river);
            int points = Points.forClosedRiver(river.tileIds().size(), fishCount);
            newMessages.add(new Message(
                    textMaker.playersScoredRiver(river.majorityOccupants(), points, fishCount, river.tileIds().size()),
                    points, river.majorityOccupants(), river.tileIds()
            ));
            return new MessageBoard(textMaker, newMessages);
        }
        return this;
    }

    /**
     * Return the message board with possibly the message when the player scored a hunting trap
     *
     * @param scorer         the player who scored the hunting trap
     * @param adjacentMeadow the meadow where the hunting trap is placed
     * @return the message board with possibly the message when the player scored a hunting trap
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow) {
        return getMessageBoardMeadow(adjacentMeadow, Set.of(), "huntingTrap", scorer);
    }

    /**
     * Return the message board with the message when the player scored a logboat
     *
     * @param scorer      the player who scored the logboat
     * @param riverSystem the river system where the logboat is placed
     * @return the message board with the message when the player scored a logboat
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
     *
     * @param meadow           the closed meadow
     * @param cancelledAnimals the animals that are not counted in the meadow
     * @return the message board with possibly the message when a player closed a meadow
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        return getMessageBoardMeadow(meadow, cancelledAnimals, "meadow", null);
    }

    /**
     * Return a message board with possibly the message when a player closed a river system
     *
     * @param riverSystem the closed river system
     * @return the message board with possibly the message when a player closed a river system
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        int points = Points.forRiverSystem(Area.riverSystemFishCount(riverSystem));
        if (riverSystem.isOccupied() && points > 0) {
            List<Message> newMessages = new ArrayList<>(messages);
            newMessages.add(new Message(
                    textMaker.playersScoredRiverSystem(
                            riverSystem.majorityOccupants(),
                            points,
                            Area.riverSystemFishCount(riverSystem)
                    ),
                    points, riverSystem.majorityOccupants(), riverSystem.tileIds()
            ));
            return new MessageBoard(textMaker, newMessages);
        }
        return this;
    }

    /**
     * Return a message board with possibly the message when a player scored a pit trap
     *
     * @param adjacentMeadow   the meadow where the pit trap is placed
     * @param cancelledAnimals the animals that are not counted in the meadow
     * @return the message board with possibly the message when a player scored a pit trap
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        return getMessageBoardMeadow(adjacentMeadow, cancelledAnimals, "pitTrap", null);
    }

    /**
     * Return a message board that depend on meadows (pit trap, hunting trap, meadow)
     *
     * @param adjacentMeadow   the meadow
     * @param cancelledAnimals the animals that are not counted in the meadow
     * @param messageType      the type of the message (can be "pitTrap", "huntingTrap" or "meadow")
     * @param scorer           the player who scored the hunting trap
     * @return the message board that depends on meadows (pit trap, hunting trap, meadow)
     */
    private MessageBoard getMessageBoardMeadow(
            Area<Zone.Meadow> adjacentMeadow,
            Set<Animal> cancelledAnimals,
            String messageType,
            PlayerColor scorer
    ) {
        Set<Animal> animals = Area.animals(adjacentMeadow, cancelledAnimals);
        Map<Animal.Kind, Integer> animalsCount = new HashMap<>();
        for (Animal animal : animals) {
            animalsCount.merge(animal.kind(), 1, Integer::sum);
        }
        int points = Points.forMeadow(
                animalsCount.getOrDefault(Animal.Kind.MAMMOTH, 0),
                animalsCount.getOrDefault(Animal.Kind.AUROCHS, 0),
                animalsCount.getOrDefault(Animal.Kind.DEER, 0));
        if ((scorer != null || adjacentMeadow.isOccupied()) && points > 0) {
            List<Message> newMessages = new ArrayList<>(messages);
            switch (messageType) {
                case "meadow" -> newMessages.add(new Message(
                        textMaker.playersScoredMeadow(adjacentMeadow.majorityOccupants(), points, animalsCount),
                        points, adjacentMeadow.majorityOccupants(), adjacentMeadow.tileIds()
                ));
                case "pitTrap" -> newMessages.add(new Message(
                        textMaker.playersScoredPitTrap(adjacentMeadow.majorityOccupants(), points, animalsCount),
                        points, adjacentMeadow.majorityOccupants(), adjacentMeadow.tileIds()
                ));
                case "huntingTrap" -> {
                    //Even though intellij says that the scorer could be null, in the case of hunting trap, it can't
                    newMessages.add(new Message(
                            textMaker.playerScoredHuntingTrap(scorer, points, animalsCount),
                            points, Set.of(scorer), adjacentMeadow.tileIds()
                    ));
                }
            }
            return new MessageBoard(textMaker, newMessages);
        }
        return this;
    }

    /**
     * Return a message board with possibly the message when a player scored a raft
     *
     * @param riverSystem the river system where the raft is placed
     * @return the message board with possibly the message when a player scored a raft
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        int points = Points.forRaft(Area.lakeCount(riverSystem));
        if (riverSystem.isOccupied() && points > 0) {
            List<Message> newMessages = new ArrayList<>(messages);
            newMessages.add(new Message(
                    textMaker.playersScoredRaft(riverSystem.majorityOccupants(), points, Area.lakeCount(riverSystem)),
                    points, riverSystem.majorityOccupants(), riverSystem.tileIds()
            ));
            return new MessageBoard(textMaker, newMessages);
        }
        return this;
    }

    /**
     * Return a message board with the message when it is the end of the game
     *
     * @param winners the winners of the game
     * @param points  the points of the winners
     * @return the message board with the message when it is the end of the game
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        List<Message> newMessages = new ArrayList<>(messages);
        newMessages.add(new Message(textMaker.playersWon(winners, points), 0, Set.of(), Set.of()));
        return new MessageBoard(textMaker, newMessages);
    }

    /**
     * Represents a message of the message board
     *
     * @param text    the text of the message
     * @param points  the points of the message
     * @param scorers the scorers of the message
     * @param tileIds the tile ids of the message
     */
    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        /**
         * Constructs a message with the given text, points, scorers and tile ids
         *
         * @param text    the text of the message
         * @param points  the points of the message
         * @param scorers the scorers of the message
         * @param tileIds the tile ids of the message
         */
        public Message {
            Objects.requireNonNull(text);
            Preconditions.checkArgument(points >= 0);
            scorers = Set.copyOf(scorers);
            tileIds = Set.copyOf(tileIds);
        }
    }
}
