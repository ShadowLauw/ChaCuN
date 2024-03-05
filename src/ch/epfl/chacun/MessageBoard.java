package ch.epfl.chacun;

import java.util.*;

public record MessageBoard(TextMaker textMaker, List<Message> messages) {

    public MessageBoard(TextMaker textMaker, List<Message> messages) {
        this.textMaker = textMaker;
        this.messages = List.copyOf(messages);
    }

    public Map<PlayerColor, Integer> points() {
        Map<PlayerColor, Integer> points = new HashMap<>();
        for (Message message : messages) {
            for (PlayerColor player : message.scorers) {
                //je pense que c'est ce que faut faire, mais pas sûr de moi sur le lambda
                points.merge(player, message.points, (Integer value1, Integer value2)-> {return value1 + value2;});
            }
        }
        return points;
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

    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        List<Message> newmessages = new ArrayList<>(messages);
        int points = Points.forLogboat(Area.lakeCount(riverSystem));
        Message message  = new Message(textMaker.playerClosedForestWithMenhir(scorer), points, Set.of(scorer), riverSystem.tileIds());
        newmessages.add(message);
        return new MessageBoard(this.textMaker, newmessages);
    }

    public MessageBoard withScoredMeadow(Area<Zone.Meadow> meadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animals = Area.animals(meadow, cancelledAnimals);
        Map<Animal.Kind, Integer> animalsCount = new HashMap<>();
        for (Animal animal : animals) {
            animalsCount.merge(animal.kind(), 1, (Integer value1, Integer value2)-> {return value1 + value2;});
        }
        int points = Points.forMeadow(animalsCount.get(Animal.Kind.MAMMOTH), animalsCount.get(Animal.Kind.AUROCHS), animalsCount.get(Animal.Kind.DEER));
        if(meadow.isOccupied() && points > 0){
            List<Message> newmessages = new ArrayList<>(messages);
            Message message  = new Message(textMaker.playersScoredMeadow(meadow.majorityOccupants(), points, animalsCount), points, meadow.majorityOccupants(), meadow.tileIds());
            newmessages.add(message);
            return new MessageBoard(this.textMaker, newmessages);
        } else {
            return new MessageBoard(this.textMaker, this.messages);
        }
    }

    public MessageBoard withScoredRiverSystem(Area<Zone.Water> riverSystem) {
        //alors c'est sûr que ça marche, mais la méthode est un peu bancale vu que j'utilise les fonctions fléchées avec des trucs qu'on a pas trop vu en cours...
        int points = Points.forRiverSystem(Area.riverSystemFishCount(riverSystem));
        if(riverSystem.isOccupied() && points > 0){
            List<Message> newmessages = new ArrayList<>(messages);
            Message message  = new Message(textMaker.playersScoredRiverSystem(riverSystem.majorityOccupants(), points, Area.riverSystemFishCount(riverSystem)), points, riverSystem.majorityOccupants(), riverSystem.tileIds());
            newmessages.add(message);
            return new MessageBoard(this.textMaker, newmessages);
        } else {
            return new MessageBoard(this.textMaker, this.messages);
        }
    }

    public MessageBoard withScoredPitTrap(Area<Zone.Meadow> adjacentMeadow, Set<Animal> cancelledAnimals) {
        Set<Animal> animals = Area.animals(adjacentMeadow, cancelledAnimals);
        Map<Animal.Kind, Integer> animalsCount = new HashMap<>();
        for (Animal animal : animals) {
            animalsCount.merge(animal.kind(), 1, (Integer value1, Integer value2)-> {return value1 + value2;});
        }
        int points = Points.forMeadow(animalsCount.get(Animal.Kind.MAMMOTH), animalsCount.get(Animal.Kind.AUROCHS), animalsCount.get(Animal.Kind.DEER));
        if(adjacentMeadow.isOccupied() && points > 0){
            List<Message> newmessages = new ArrayList<>(messages);
            Message message  = new Message(textMaker.playersScoredPitTrap(adjacentMeadow.majorityOccupants(), points, animalsCount), points, adjacentMeadow.majorityOccupants(), adjacentMeadow.tileIds());
            newmessages.add(message);
            return new MessageBoard(this.textMaker, newmessages);
        } else {
            return new MessageBoard(this.textMaker, this.messages);
        }
    }

    public MessageBoard withScoredRaft(Area<Zone.Water> riverSystem) {
        int points = Points.forRaft(Area.lakeCount(riverSystem));
        if(riverSystem.isOccupied() && points > 0){
            List<Message> newmessages = new ArrayList<>(messages);
            Message message  = new Message(textMaker.playersScoredRaft(riverSystem.majorityOccupants(), points, Area.lakeCount(riverSystem)), points, riverSystem.majorityOccupants(), riverSystem.tileIds());
            newmessages.add(message);
            return new MessageBoard(this.textMaker, newmessages);
        } else {
            return new MessageBoard(this.textMaker, this.messages);
        }
    }

    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        List<Message> newmessages = new ArrayList<>(messages);
        Message message  = new Message(textMaker.playersWon(winners, points), points, winners, Set.of());
        newmessages.add(message);
        return new MessageBoard(this.textMaker, newmessages);
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
