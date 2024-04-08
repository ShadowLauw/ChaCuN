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
    final private Map<PlayerColor, String> playerNames;

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
     * Enum representing the different objects that can be counted
     */
    private enum MiscObjects {
        MUSHROOM,
        FISH,
        LAKE,
        TILE
    }

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
        SIMPLE,
        MEDIAN_POINT
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
        String scorersString = getScorersString(scorers, points, ScorersType.MAJORITY);
        String mushroomString = getMiscString(mushroomGroupCount, MiscObjects.MUSHROOM, " et de");
        String tileString = getMiscString(tileCount, MiscObjects.TILE, "composée de");

        return STR."\{scorersString} d'une forêt \{tileString}\{mushroomString}.";
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
        String scorersString = getScorersString(scorers, points, ScorersType.POINTS);
        String fishString = getMiscString(fishCount, MiscObjects.FISH, " et contenant");
        String tileString = getMiscString(tileCount, MiscObjects.TILE, "composée de");
        return STR."\{scorersString} d'une rivière \{tileString}\{fishString}.";
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
        String scorersString = getScorersString(Set.of(scorer), points, ScorersType.POINTS);
        String animalsString = getAnimalString(animals);
        return STR."\{scorersString} en plaçant la fosse à pieux dans un pré dans lequel elle est entourée de \{animalsString}.";
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
        String scorersString = getScorersString(Set.of(scorer), points, ScorersType.POINTS);
        String lakeString = getMiscString(lakeCount, MiscObjects.LAKE, "contenant");
        return STR."\{scorersString} en plaçant la pirogue dans un réseau hydrographique \{lakeString}.";
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
        String scorersString = getScorersString(scorers, points, ScorersType.MAJORITY);
        String animalsString = getAnimalString(animals);
        return STR."\{scorersString} d'un pré contenant \{animalsString}.";
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
        String scorersString = getScorersString(scorers, points, ScorersType.MAJORITY);
        String fishString = getMiscString(fishCount, MiscObjects.FISH, "contenant");
        return STR."\{scorersString} d'un réseau hydrographique \{fishString}.";
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
        String scorersString = getScorersString(scorers, points, ScorersType.MAJORITY);
        String animalsString = getAnimalString(animals);
        return STR."\{scorersString} d'un pré contenant la grande fosse à pieux entourée de \{animalsString}.";
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
        String scorersString = getScorersString(scorers, points, ScorersType.MAJORITY);
        String lakeString = getMiscString(lakeCount, MiscObjects.LAKE, "contenant");
        return STR."\{scorersString} d'un réseau hydrographique contenant le radeau et \{lakeString}.";
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
        String scorersString = getScorersString(winners, points, ScorersType.SIMPLE);
        return STR."\{scorersString} la partie avec \{points(points)} !";
    }

    /**
     * Return the string saying the player can click on the occupant they want to place, or on the text to not place
     * @return the string saying the player can click on the occupant they want to place, or on the text to not place
     */
    @Override
    public String clickToOccupy() {
        return "Cliquez sur le pion ou la hutte que vous désirez placer, ou ici pour ne pas en placer.";
    }

    /**
     * Return the string saying the player can click on the pawn they want to take back, or on the text to not take
     * @return the string saying the player can click on the pawn they want to take back, or on the text to not take
     */
    @Override
    public String clickToUnoccupy() {
        return "Cliquez sur le pion que vous désirez reprendre, ou ici pour ne pas en reprendre.";
    }

    /**
     * Return a string to pluralize a word with possibly a median point
     * @param number the number of an object
     * @param type the type of pluralization
     * @return the string to pluralize a word with possibly a median point
     */
    private String pluralize(int number, PluralizationType type) {
        return number > 1 ? (type == PluralizationType.MEDIAN_POINT ? "·s" : "s") : "";
    }

    /**
     * Return a string representation with correct comas and conjunctive words of an array of strings
     * @param array the array of strings
     * @return the string representation with correct comas and conjunctive words of an array of strings
     */
    private String arrayToString(String[] array) {
        StringBuilder string = new StringBuilder(array[0]);
        for (int i = 1; i < array.length; i++) {
            string.append(i + 1 == array.length ? " et " : ", ");
            string.append(array[i]);
        }

        return string.toString();
    }

    /**
     * Return a string representation of the players who scored a certain number of points
     * @param scorers the players who scored
     * @param points the number of points
     * @return the string representation of the players who scored a certain number of points
     */
    private String getScorersString(Set<PlayerColor> scorers, int points, ScorersType type) {
        String[] scorersNameArray = scorers.stream().sorted().map(playerNames::get).toArray(String[]::new);
        StringBuilder scorersString =  new StringBuilder(arrayToString(scorersNameArray));
        scorersString.append(scorers.size() > 1 ? " ont remporté" : " a remporté");
        if (type == ScorersType.POINTS || type == ScorersType.MAJORITY) {
            scorersString.append(STR." \{points(points)}");
            if (type == ScorersType.MAJORITY) {
                scorersString.append(STR." en tant qu'occupant·e\{pluralize(scorers.size(), PluralizationType.MEDIAN_POINT)}");
                scorersString.append(STR." majoritaire\{pluralize(scorers.size(), PluralizationType.SIMPLE)}");
            }
        }

        return scorersString.toString();
    }

    /**
     * Return a string representation of miscellaneous objects and their number, with possibly a string to prepend
     * @param objectNumber the number of objects
     * @param object the type of object
     * @param prepend the string to prepend
     * @return the string representation of miscellaneous objects and their number, with possibly a string to prepend
     */
    private String getMiscString(int objectNumber, MiscObjects object, String prepend) {
        String objectString = switch (object) {
            case MUSHROOM -> "groupe de champignons";
            case FISH -> "poisson";
            case LAKE -> "lac";
            case TILE -> "tuile";
        };
        return objectNumber > 0
                ? STR."\{prepend} \{objectNumber} \{objectString}\{pluralize(objectNumber, PluralizationType.SIMPLE)}"
                : "";
    }

    /**
     * Return a string representation of the animals and their number
     * @param animals the animals and their number
     * @return the string representation of the animals and their number
     */
    private String getAnimalString(Map<Animal.Kind, Integer> animals) {
        //animals is an EnumMap so the order is guaranteed
        String[] animalsArray = animals.entrySet().stream().map(entry -> {
            int count = entry.getValue();
            String animal = switch (entry.getKey()) {
                case MAMMOTH -> "mammouth";
                case AUROCHS -> "auroch";
                case DEER -> "cerf";
                case TIGER -> "tigre";
            };
            return STR."\{count} \{animal}\{pluralize(count, PluralizationType.SIMPLE)}";
        }).toArray(String[]::new);

        return arrayToString(animalsArray);
    }
}
