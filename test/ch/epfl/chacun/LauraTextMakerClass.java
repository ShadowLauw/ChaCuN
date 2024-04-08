package ch.epfl.chacun;

import java.util.*;

public class LauraTextMakerClass implements TextMaker{
    @Override
    public String playerName(PlayerColor playerColor) {
        return new StringJoiner(" ")
                .add(playerColor.toString())
                .toString();
    }

    @Override
    public String points(int points) {
        return new StringJoiner(" ")
                .add(String.valueOf(points))
                .toString();
    }

    @Override
    public String playerClosedForestWithMenhir(PlayerColor player) {
        return new StringJoiner(" ")
                .add(player.toString())
                .toString();
    }

    @Override
    public String playersScoredForest(Set<PlayerColor> scorers, int points, int mushroomGroupCount, int tileCount) {
        Set<PlayerColor> scorerrsTreeSet = new TreeSet<>(scorers);
        return new StringJoiner(" ")
                .add(scorerrsTreeSet.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(mushroomGroupCount))
                .add(String.valueOf(tileCount))
                .toString();
    }

    @Override
    public String playersScoredRiver(Set<PlayerColor> scorers, int points, int fishCount, int tileCount) {
        Set<PlayerColor> scorerrsTreeSet = new TreeSet<>(scorers);
        return new StringJoiner(" ")
                .add(scorerrsTreeSet.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(fishCount))
                .add(String.valueOf(tileCount))
                .toString();
    }

    @Override
    public String playerScoredHuntingTrap(PlayerColor scorer, int points, Map<Animal.Kind, Integer> animals) {
        Map<Animal.Kind, Integer> animalsTreeMap = new TreeMap<>(animals);
        return new StringJoiner(" ")
                .add(scorer.toString())
                .add(String.valueOf(points))
                .add(animalsTreeMap.toString())
                .toString();
    }

    @Override
    public String playerScoredLogboat(PlayerColor scorer, int points, int lakeCount) {
        return new StringJoiner(" ")
                .add(scorer.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(lakeCount))
                .toString();
    }

    @Override
    public String playersScoredMeadow(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        Set<PlayerColor> scorerrsTreeSet = new TreeSet<>(scorers);
        Map<Animal.Kind, Integer> animalsTreeMap = new TreeMap<>(animals);
        return new StringJoiner(" ")
                .add(scorerrsTreeSet.toString())
                .add(String.valueOf(points))
                .add(animalsTreeMap.toString())
                .toString();
    }

    @Override
    public String playersScoredRiverSystem(Set<PlayerColor> scorers, int points, int fishCount) {
        Set<PlayerColor> scorerrsTreeSet = new TreeSet<>(scorers);
        return new StringJoiner(" ")
                .add(scorerrsTreeSet.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(fishCount))
                .toString();
    }

    @Override
    public String playersScoredPitTrap(Set<PlayerColor> scorers, int points, Map<Animal.Kind, Integer> animals) {
        Set<PlayerColor> scorerrsTreeSet = new TreeSet<>(scorers);
        Map<Animal.Kind, Integer> animalsTreeMap = new TreeMap<>(animals);
        return new StringJoiner(" ")
                .add(scorerrsTreeSet.toString())
                .add(String.valueOf(points))
                .add(animalsTreeMap.toString())
                .toString();
    }

    @Override
    public String playersScoredRaft(Set<PlayerColor> scorers, int points, int lakeCount) {
        Set<PlayerColor> scorerrsTreeSet = new TreeSet<>(scorers);
        return new StringJoiner(" ")
                .add(scorerrsTreeSet.toString())
                .add(String.valueOf(points))
                .add(String.valueOf(lakeCount))
                .toString();
    }

    @Override
    public String playersWon(Set<PlayerColor> winners, int points) {
        Set<PlayerColor> scorerrsTreeSet = new TreeSet<>(winners);
        return new StringJoiner(" ")
                .add(scorerrsTreeSet.toString())
                .add(String.valueOf(points))
                .toString();
    }

    @Override
    public String clickToOccupy() {
        return null;
    }

    @Override
    public String clickToUnoccupy() {
        return null;
    }
}
