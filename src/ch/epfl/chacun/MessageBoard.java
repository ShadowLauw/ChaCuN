package ch.epfl.chacun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    public MessageBoard(TextMaker textMaker, List<Message> messages) {
        this.textMaker = textMaker;
        this.messages = List.copyOf(messages);
    }

    public Map<PlayerColor, Integer> points() {
        return null;
    }

    public MessageBoard withScoredForest(Area<Zone.Forest> forest) {
        if(forest.isOccupied()){
            List<Message> newmessages = new ArrayList<>(messages);
            int mushroomGroups = Area.mushroomGroupCount(forest);
            int points = Points.forClosedForest(forest.zones().size(), mushroomGroups);
            Message message  = new Message(textMaker.playersScoredForest(forest.majorityOccupants(), points, mushroomGroups, forest.zones().size()), points, forest.majorityOccupants(), forest.tileIds());
            newmessages.add(message);
            return new MessageBoard(this.textMaker, newmessages);
        } else {
            return new MessageBoard(this.textMaker, this.messages);
        }
    }

    public MessageBoard withClosedMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        List<Message> newmessages = new ArrayList<>(messages);
        int mushroomGroups = Area.mushroomGroupCount(forest);
        int points = Points.forClosedForest(forest.zones().size(), mushroomGroups);
        Message message  = new Message(textMaker.playerClosedForestWithMenhir(player), points, Set.of(player), forest.tileIds());
        newmessages.add(message);
        return new MessageBoard(this.textMaker, newmessages);
    }

    public MessageBoard withScoredRiver(Area<Zone.River> river) {
        if(river.isOccupied()){
            List<Message> newmessages = new ArrayList<>(messages);
            int fishCount = Area.riverFishCount(river);
            int points = Points.forClosedRiver(river.zones().size(), fishCount);
            Message message  = new Message(textMaker.playersScoredRiver(river.majorityOccupants(), points, fishCount, river.zones().size()), points, river.majorityOccupants(), river.tileIds());
            newmessages.add(message);
            return new MessageBoard(this.textMaker, newmessages);
        } else {
            return new MessageBoard(this.textMaker, this.messages);
        }
    }

    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> ajdacentMeadow) {
        return null;
    }

    public MessageBoard withScoredLoagboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        return null;
    }

    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        return null;
    }

    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        return null;
    }

    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> ajdacentMeadow, Set<Animal> cancelledAnimals) {
        return null;
    }

    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        return null;
    }

    public MessageBoard withWinners(PlayerColor winners, int points) {
        return null;
    }

    public record Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
        public Message(String text, int points, Set<PlayerColor> scorers, Set<Integer> tileIds) {
            Preconditions.checkArgument(text!=null);
            Preconditions.checkArgument(points>=0);
            this.text = text;
            this.points = points;
            this.scorers = Set.copyOf(scorers);
            this.tileIds = Set.copyOf(tileIds);
        }
    }
}
