package ch.epfl.chacun;

import java.util.*;

/**
 * Represents the french TextMaker for the game
 *
 * @author Laura Paraboschi (364161)
 * @author Emmanuel Omont (372632)
 */
public final class TextMakerFr implements TextMaker {

    /**
     * Map of the association between the player's color and their name
     */
    private final Map<PlayerColor, String> playerNames;

    /**
     * String for the occupy deck
     */
    private static final String OCCUPY_STRING =
            "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
    /**
     * String for the unoccupy deck
     */
    private static final String UNOCCUPY_STRING =
            "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";

    /**
     * Map of the association between the animal kind and its name
     */
    private static final Map<Animal.Kind, String> animalNames = Map.of(
            Animal.Kind.MAMMOTH, "mammouth",
            Animal.Kind.AUROCHS, "auroch",
            Animal.Kind.DEER, "cerf"
    );

    /**
     * Enum representing the different types of message for the scorers
     */
    private enum ScorersType {
        SIMPLE,
        POINTS,
        MAJORITY
    }

    /**
     * Enum representing the different types of pluralization
     */
    private enum PluralizationType {
        SIMPLE("s"),
        MEDIAN_POINT("·s");

        /**
         * The pluralization
         */
        private final String pluralization;

        /**
         * Return the pluralization
         *
         * @return the pluralization
         */
        public String pluralization() {
            return pluralization;
        }

        /**
         * Creates a new pluralization type with the given pluralization
         *
         * @param pluralization the pluralization
         */
        PluralizationType(String pluralization) {
            this.pluralization = pluralization;
        }
    }

    /**
     * Enum representing the different objects that can be counted
     */
    private enum MiscObjects {
        MUSHROOM("groupe", " de champignons"),
        FISH("poisson", ""),
        LAKE("lac", ""),
        TILE("tuile", "");

        /**
         * The name of the object
         */
        private final String name;
        /**
         * The appendix of the object
         */
        private final String appendix;

        /**
         * Return the name of the object
         *
         * @return the name of the object
         */
        public String getName() {
            return name;
        }

        /**
         * Return the appendix of the object
         *
         * @return the appendix of the object
         */
        public String getAppendix() {
            return appendix;
        }

        /**
         * Creates a new object with the given name and appendix
         *
         * @param name     the name of the object
         * @param appendix the appendix of the object
         */
        MiscObjects(String name, String appendix) {
            this.name = name;
            this.appendix = appendix;
        }
    }

    /**
     * Creates a new french TextMaker with the given player names and colors as players
     *
     * @param playerNames the map of the association between the player's color and their name
     */
    public TextMakerFr(Map<PlayerColor, String> playerNames) {
        this.playerNames = Map.copyOf(playerNames);
    }

    /**
     * Returns the name of the player with the given color
     *
     * @param playerColor the color of the player
     * @return the name of the player with the given color
     */
    @Override
    public String playerName(PlayerColor playerColor) {
        return playerNames.get(playerColor);
    }

    /**
     * Return the string representation of the given points
     *
     * @param points the number of points
     * @return the string representation of the given points
     */
    @Override
    public String points(int points) {
        return STR."\{points} point\{pluralize(points, PluralizationType.SIMPLE)}";
    }

    /**
     * Return the string of the player saying they can play a second turn given they closed a menhir forest
     *
     * @param player the player who closed the menhir forest
     * @return the string of the player saying they can play a second turn given they closed a menhir forest
     */
    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return STR."\{playerName(player)} a fermé une forêt contenant un menhir et peut donc placer une tuile menhir.";
    }

    /**
     * Return the string saying a player closed a forest with a certain number of mushroom groups and tiles,
     * therefore scoring a certain number of points
     *
     * @param scorers            the majority occupants of the forest
     * @param points             the points scored
     * @param mushroomGroupCount the number of mushroom groups in the forest
     * @param tileCount          the number of tiles in the forest
     * @return the string saying a player scored a forest with a certain number of mushroom groups and tiles,
     * therefore scoring a certain number of points
     */
    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        String scorersInfo = getScorersString(scorers, points, ScorersType.MAJORITY);
        String mushroomInfo = getMiscString(mushroomGroupCount, MiscObjects.MUSHROOM, " et de ");
        String tileInfo = getMiscString(tileCount, MiscObjects.TILE, "composée de ");

        return STR."\{scorersInfo} d'une forêt \{tileInfo}\{mushroomInfo}.";
    }

    /**
     * Return the string saying players closed a river with a certain number of fish and tiles, therefore scoring
     * a certain number of points
     *
     * @param scorers   the majority occupants of the river
     * @param points    the points scored
     * @param fishCount the number of fish in the river
     * @param tileCount the number of tiles in the river
     * @return the string saying players scored a river with a certain number of fish and tiles, therefore scoring
     * a certain number of points
     */
    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        String scorersInfo = getScorersString(scorers, points, ScorersType.MAJORITY);
        String fishInfo = getMiscString(fishCount, MiscObjects.FISH, " et contenant ");
        String tileInfo = getMiscString(tileCount, MiscObjects.TILE, "composée de ");
        return STR."\{scorersInfo} d'une rivière \{tileInfo}\{fishInfo}.";
    }

    /**
     * Return the string saying a player placed a hunting trap in a meadow containing a certain number of animals,
     * therefore scoring a certain number of points
     *
     * @param scorer  the player who placed the hunting trap
     * @param points  the points scored
     * @param animals the animals in the meadow
     * @return the string saying a player placed a hunting trap in a meadow containing a certain number of animals,
     * therefore scoring a certain number of points
     */
    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        String scorersInfo = getScorersString(Set.of(scorer), points, ScorersType.POINTS);
        String animalsInfo = getAnimalString(animals);
        return STR."\{scorersInfo} en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de \{animalsInfo}.";
    }

    /**
     * Return the string saying a player placed a logboat in a river system containing a certain number of lakes,
     * therefore scoring a certain number of points
     *
     * @param scorer    the player who placed the logboat
     * @param points    the points scored
     * @param lakeCount the number of lakes in the river system
     * @return the string saying a player placed a logboat in a river system containing a certain number of lakes,
     * therefore scoring a certain number of points
     */
    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        String scorersInfo = getScorersString(Set.of(scorer), points, ScorersType.POINTS);
        String lakeInfo = getMiscString(lakeCount, MiscObjects.LAKE, "contenant ");
        return STR."\{scorersInfo} en plaçant la pirogue dans un réseau hydrographique \{lakeInfo}.";
    }

    /**
     * Return the string saying the majority occupants of a meadow containing certain animals scored a certain number
     * of points
     *
     * @param scorers the majority occupants of the meadow
     * @param points  the points scored
     * @param animals the animals in the meadow
     * @return the string saying the majority occupants of a meadow containing certain animals scored a certain number
     * of points
     */
    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        String scorersInfo = getScorersString(scorers, points, ScorersType.MAJORITY);
        String animalsInfo = getAnimalString(animals);
        return STR."\{scorersInfo} d'un pré contenant \{animalsInfo}.";
    }

    /**
     * Return the string saying the majority occupants of a river system containing a certain number of fish scored a
     * certain number of points
     *
     * @param scorers   the majority occupants of the river system
     * @param points    the points scored
     * @param fishCount the number of fish in the river system
     * @return the string saying the majority occupants of a river system containing a certain number of fish scored a
     * certain number of points
     */
    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        String scorersInfo = getScorersString(scorers, points, ScorersType.MAJORITY);
        String fishInfo = getMiscString(fishCount, MiscObjects.FISH, "contenant ");
        return STR."\{scorersInfo} d'un réseau hydrographique \{fishInfo}.";
    }

    /**
     * Return the string saying the majority occupants of a meadow containing a pit trap and certain animals scored a
     * certain number of points
     *
     * @param scorers the majority occupants of the meadow
     * @param points  the points scored
     * @param animals the animals in the meadow
     * @return the string saying the majority occupants of a meadow containing a pit trap and certain animals scored a
     * certain number of points
     */
    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        String scorersInfo = getScorersString(scorers, points, ScorersType.MAJORITY);
        String animalsInfo = getAnimalString(animals);
        return STR."\{scorersInfo} d'un pré contenant la grande fosse à pieux entourée de \{animalsInfo}.";
    }

    /**
     * Return the string saying the majority occupants of a river system containing a raft scored a certain number of
     * points
     *
     * @param scorers   the majority occupants of the river system
     * @param points    the points scored
     * @param lakeCount the number of lakes accessible to the raft
     * @return the string saying the majority occupants of a river system containing a raft scored a certain number of
     * points
     */
    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        String scorersInfo = getScorersString(scorers, points, ScorersType.MAJORITY);
        String lakeInfo = getMiscString(lakeCount, MiscObjects.LAKE, "");
        return STR."\{scorersInfo} d'un réseau hydrographique contenant le radeau et \{lakeInfo}.";
    }

    /**
     * Return the string saying the players won the game with a certain number of points
     *
     * @param winners the players who won the game
     * @param points  the number of points
     * @return the string saying the players won the game with a certain number of points
     */
    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        String scorersInfo = getScorersString(winners, points, ScorersType.SIMPLE);
        return STR."\{scorersInfo} la partie avec \{points(points)} !";
    }

    /**
     * Return the string saying the player can click on the occupant they want to place, or on the text to not place
     *
     * @return the string saying the player can click on the occupant they want to place, or on the text to not place
     */
    @Override
    public String clickToOccupy() {
        return OCCUPY_STRING;
    }

    /**
     * Return the string saying the player can click on the pawn they want to take back, or on the text to not take
     *
     * @return the string saying the player can click on the pawn they want to take back, or on the text to not take
     */
    @Override
    public String clickToUnoccupy() {
        return UNOCCUPY_STRING;
    }

    /**
     * Return a string to pluralize a word with possibly a median point
     *
     * @param number the number of an object
     * @param type   the type of pluralization
     * @return the string to pluralize a word with possibly a median point
     */
    private static String pluralize(int number, PluralizationType type) {
        return number > 1 ? type.pluralization() : "";
    }

    /**
     * Return a string representation with correct commas and conjunctive words of an array of strings
     *
     * @param array the array of strings
     * @return the string representation with correct commas and conjunctive words of an array of strings
     */
    private static String arrayToString(String[] array) {
        StringBuilder string = new StringBuilder(array[0]);
        for (int i = 1; i < array.length; i++) {
            string.append(i + 1 == array.length ? " et " : ", ");
            string.append(array[i]);
        }

        return string.toString();
    }

    /**
     * Return a string representation of miscellaneous objects and their number, with possibly a string to prepend
     *
     * @param objectNumber the number of objects
     * @param misc       the type of object
     * @param prepend      the string to prepend
     * @return the string representation of miscellaneous objects and their number, with possibly a string to prepend
     */
    private static String getMiscString(int objectNumber, MiscObjects misc, String prepend) {
        if (!(objectNumber > 0)) {
            return "";
        }
        String objectInfo =
                STR."\{misc.getName()}\{pluralize(objectNumber, PluralizationType.SIMPLE)}\{misc.getAppendix()}";

        return STR."\{prepend}\{objectNumber} \{objectInfo}";
    }

    /**
     * Return a string representation of the animals and their number
     *
     * @param animals the animals and their number
     * @return the string representation of the animals and their number
     */
    private static String getAnimalString(Map<Animal.Kind, Integer> animals) {
        //EnumMap will sort the animals by their order in the enum
        Map<Animal.Kind, Integer> sortedAnimals = new EnumMap<>(animals);
        String[] animalsArray = sortedAnimals.entrySet().stream().filter(entry -> entry.getKey() != Animal.Kind.TIGER)
                .map(entry -> {
                    int count = entry.getValue();

                    return STR."\{count} \{animalNames.get(entry.getKey())}\{pluralize(count, PluralizationType.SIMPLE)}";
                }).toArray(String[]::new);

        return arrayToString(animalsArray);
    }

    /**
     * Return a string representation of the players who scored a certain number of points
     *
     * @param scorers the players who scored
     * @param points  the number of points
     * @return the string representation of the players who scored a certain number of points
     */
    private String getScorersString(Set<PlayerColor> scorers, int points, ScorersType type) {
        String[] scorersNameArray = scorers.stream().sorted().map(playerNames::get).toArray(String[]::new);
        StringBuilder scorersInfo = new StringBuilder(arrayToString(scorersNameArray));
        scorersInfo.append(scorers.size() > 1 ? " ont remporté" : " a remporté");
        if (type != ScorersType.SIMPLE) {
            scorersInfo.append(STR." \{points(points)}");
            if (type == ScorersType.MAJORITY) {
                scorersInfo.append(STR." en tant qu'occupant·e\{pluralize(scorers.size(), PluralizationType.MEDIAN_POINT)}");
                scorersInfo.append(STR." majoritaire\{pluralize(scorers.size(), PluralizationType.SIMPLE)}");
            }
        }

        return scorersInfo.toString();
    }
}
