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
     * Type of scoring for Meadows
     */
    private enum MeadowType {
        PIT_TRAP,
        HUNTING_TRAP,
        MEADOW
    }

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
        if (!forest.isOccupied())
            return this;

        int mushroomGroups = Area.mushroomGroupCount(forest);
        Set<Integer> tileIds = forest.tileIds();
        int points = Points.forClosedForest(tileIds.size(), mushroomGroups);
        Set<PlayerColor> majorityOccupants = forest.majorityOccupants();
        String text = textMaker.playersScoredForest(majorityOccupants, points, mushroomGroups, tileIds.size());

        return addMessage(text, points, majorityOccupants, tileIds);
    }

    /**
     * Return the message board with a new message for the closed forest with menhir
     *
     * @param player the player who closed the forest with menhir
     * @param forest the closed forest with menhir
     * @return the message board with a new message for the given closed forest with menhir
     */
    public MessageBoard withClosedForestWithMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        return addMessage(textMaker.playerClosedForestWithMenhir(player), 0, Set.of(player), forest.tileIds());
    }

    /**
     * Return the message board with possibly the message when the player scored a closed river
     *
     * @param river the closed river
     * @return the message board with possibly the message when the player scored a closed river
     */
    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if (!river.isOccupied())
            return this;

        int fishCount = Area.riverFishCount(river);
        Set<Integer> tileIds = river.tileIds();
        int points = Points.forClosedRiver(tileIds.size(), fishCount);
        Set<PlayerColor> majorityOccupants = river.majorityOccupants();
        String text = textMaker.playersScoredRiver(majorityOccupants, points, fishCount, tileIds.size());

        return addMessage(text, points, majorityOccupants, tileIds);
    }

    /**
     * Return the message board with possibly the message when the player scored a hunting trap
     *
     * @param scorer         the player who scored the hunting trap
     * @param adjacentMeadow the meadow where the hunting trap is placed
     * @return the message board with possibly the message when the player scored a hunting trap
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer,
                                              Area<Zone.Meadow> adjacentMeadow,
                                              Set<Animal> cancelledAnimals
    ) {
        return getMessageBoardMeadow(adjacentMeadow, cancelledAnimals, MeadowType.HUNTING_TRAP, scorer);
    }

    /**
     * Return the message board with the message when the player scored a logboat
     *
     * @param scorer      the player who scored the logboat
     * @param riverSystem the river system where the logboat is placed
     * @return the message board with the message when the player scored a logboat
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        int lakeCount = Area.lakeCount(riverSystem);
        int points = Points.forLogboat(lakeCount);
        String text = textMaker.playerScoredLogboat(scorer, points, lakeCount);

        return addMessage(text, points, Set.of(scorer), riverSystem.tileIds());
    }

    /**
     * Return a message board with possibly the message when a player closed a meadow
     *
     * @param meadow           the closed meadow
     * @param cancelledAnimals the animals that are not counted in the meadow
     * @return the message board with possibly the message when a player closed a meadow
     */
    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        return getMessageBoardMeadow(meadow, cancelledAnimals, MeadowType.MEADOW, null);
    }

    /**
     * Return a message board with possibly the message when a player closed a river system
     *
     * @param riverSystem the closed river system
     * @return the message board with possibly the message when a player closed a river system
     */
    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        int fishCount = Area.riverSystemFishCount(riverSystem);
        int points = Points.forRiverSystem(fishCount);

        if (!(riverSystem.isOccupied() && points > 0))
            return this;

        Set<PlayerColor> majorityOccupants = riverSystem.majorityOccupants();
        String text = textMaker.playersScoredRiverSystem(majorityOccupants, points, fishCount);

        return addMessage(text, points, majorityOccupants, riverSystem.tileIds());
    }

    /**
     * Return a message board with possibly the message when a player scored a pit trap
     *
     * @param adjacentMeadow   the meadow where the pit trap is placed
     * @param cancelledAnimals the animals that are not counted in the meadow
     * @return the message board with possibly the message when a player scored a pit trap
     */
    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        return getMessageBoardMeadow(adjacentMeadow, cancelledAnimals, MeadowType.PIT_TRAP, null);
    }

    /**
     * Return a message board with possibly the message when a player scored a raft
     *
     * @param riverSystem the river system where the raft is placed
     * @return the message board with possibly the message when a player scored a raft
     */
    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        int lakeCount = Area.lakeCount(riverSystem);
        int points = Points.forRaft(lakeCount);

        if (!(riverSystem.isOccupied() && points > 0))
            return this;

        Set<PlayerColor> majorityOccupants = riverSystem.majorityOccupants();
        String text = textMaker.playersScoredRaft(majorityOccupants, points, lakeCount);

        return addMessage(text, points, majorityOccupants, riverSystem.tileIds());
    }

    /**
     * Return a message board with the message when it is the end of the game
     *
     * @param winners the winners of the game
     * @param points  the points of the winners
     * @return the message board with the message when it is the end of the game
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        return addMessage(textMaker.playersWon(winners, points), 0, Set.of(), Set.of());
    }

    /**
     * Return a message board that depend on meadows (pit trap, hunting trap, meadow)
     *
     * @param adjacentMeadow   the meadow
     * @param cancelledAnimals the animals that are not counted in the meadow
     * @param meadowType       the type of the scoring (can be "pitTrap", "huntingTrap" or "meadow")
     * @param scorer           the player who scored the hunting trap
     * @return the message board that depends on meadows (pit trap, hunting trap, meadow)
     */
    private MessageBoard getMessageBoardMeadow(
            Area<Zone.Meadow> adjacentMeadow,
            Set<Animal> cancelledAnimals,
            MeadowType meadowType,
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
                animalsCount.getOrDefault(Animal.Kind.DEER, 0)
        );

        if (!(points > 0))
            return this;

        return switch (meadowType) {
            case MEADOW, PIT_TRAP -> {
                if (adjacentMeadow.isOccupied()) {
                    Set<PlayerColor> scorers = adjacentMeadow.majorityOccupants();
                    String textToAdd = meadowType == MeadowType.MEADOW
                            ? textMaker.playersScoredMeadow(scorers, points, animalsCount)
                            : textMaker.playersScoredPitTrap(scorers, points, animalsCount);
                    yield addMessage(textToAdd, points, scorers, adjacentMeadow.tileIds());
                }
                yield this;
            }
            case HUNTING_TRAP -> {
                String textToAdd = textMaker.playerScoredHuntingTrap(scorer, points, animalsCount);
                yield addMessage(textToAdd, points, Set.of(scorer), adjacentMeadow.tileIds());
            }
        };
    }

    private MessageBoard addMessage(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        List<Message> newMessages = new ArrayList<>(messages);
        newMessages.add(new Message(text, points, scorers, tileIds));
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
