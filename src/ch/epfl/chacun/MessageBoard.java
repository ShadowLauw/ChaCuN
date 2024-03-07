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
    public MessageBoard(TextMaker textMaker, List<Message> messages) {
        this.textMaker = textMaker;
        this.messages = List.copyOf(messages);
    }

    /**
     * Return a table with all the scorers and their points
     * @return (Map<PlayerColor, Integer>) the table with all the scorers and their points
     */
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

    /**
     * Return a message board with the given closed forest
     * @param forest (Area<Zone.Forest>) the placed tile
     * @return (MessageBoard) the message board with the given placed tile
     */
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

    /**
     * Return a message board with the given closed forest with menhir
     * @param player (PlayerColor) the player who closed the forest with menhir
     * @param forest (Area<Zone.Forest>) the closed forest with menhir
     * @return (MessageBoard) the message board with the given closed forest with menhir
     */
    public MessageBoard withClosedMenhir(PlayerColor player, Area<Zone.Forest> forest) {
        List<Message> newmessages = new ArrayList<>(messages);
        int mushroomGroups = Area.mushroomGroupCount(forest);
        int points = Points.forClosedForest(forest.zones().size(), mushroomGroups);
        Message message  = new Message(textMaker.playerClosedForestWithMenhir(player), points, Set.of(player), forest.tileIds());
        newmessages.add(message);
        return new MessageBoard(this.textMaker, newmessages);
    }

    /**
     * Return a message board with the given closed river
     * @param river (Area<Zone.River>) the closed river
     * @return (MessageBoard) the message board with the given closed river
     */
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

    /**
     * Return a message board with the message when the player scored a hunting trap
     * @param scorer (PlayerColor) the player who scored the hunting trap
     * @param adjacentMeadow (Area<Zone.Meadow>) the meadow where the hunting trap is placed
     * @return (MessageBoard) the message board with the message when the player scored a hunting trap
     */
    public MessageBoard withScoredHuntingTrap(PlayerColor scorer, Area<Zone.Meadow> adjacentMeadow) {
        Set<Animal> animals = Area.animals(adjacentMeadow, Set.of());
        Map<Animal.Kind, Integer> animalsCount = new HashMap<>();
        for (Animal animal : animals) {
            animalsCount.merge(animal.kind(), 1, (Integer value1, Integer value2)-> {return value1 + value2;});
        }
        int points = Points.forMeadow(animalsCount.get(Animal.Kind.MAMMOTH), animalsCount.get(Animal.Kind.AUROCHS), animalsCount.get(Animal.Kind.DEER));
        if(points > 0){
            List<Message> newmessages = new ArrayList<>(messages);
            Message message  = new Message(textMaker.playerScoredHuntingTrap(scorer, points, animalsCount), points, Set.of(scorer), adjacentMeadow.tileIds());
            newmessages.add(message);
            return new MessageBoard(this.textMaker, newmessages);
        } else {
            return new MessageBoard(this.textMaker, this.messages);
        }
    }
    /**
     * Return a message board with the message when the player scored a logboat
     * @param scorer (PlayerColor) the player who scored the logboat
     * @param riverSystem (Area<Zone.Water>) the river system where the logboat is placed
     * @return (MessageBoard) the message board with the message when the player scored a logboat
     */
    public MessageBoard withScoredLogboat(PlayerColor scorer, Area<Zone.Water> riverSystem) {
        List<Message> newmessages = new ArrayList<>(messages);
        int points = Points.forLogboat(Area.lakeCount(riverSystem));
        Message message  = new Message(textMaker.playerClosedForestWithMenhir(scorer), points, Set.of(scorer), riverSystem.tileIds());
        newmessages.add(message);
        return new MessageBoard(this.textMaker, newmessages);
    }

    /**
     * Return a message board with the message when a player closed a meadow
     * @param meadow (Area<Zone.Meadow>) the closed meadow
     * @param cancelledAnimals (Set<Animal>) the animals that are not counted in the meadow
     * @return (MessageBoard) the message board with the message when a player closed a meadow
     */
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

    /**
     * Return a message board with the message when a player closed a river system
     * @param riverSystem (Area<Zone.Water>) the closed river system
     * @return (MessageBoard) the message board with the message when a player closed a river system
     */
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

    /**
     * Return a message board with the message when a player scored a pit trap
     * @param adjacentMeadow (Area<Zone.Meadow>) the meadow where the pit trap is placed
     * @param cancelledAnimals (Set<Animal>) the animals that are not counted in the meadow
     * @return (MessageBoard) the message board with the message when a player scored a pit trap
     */
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

    /**
     * Return a message board with the message when a player scored a raft
     * @param riverSystem (Area<Zone.Water>) the river system where the raft is placed
     * @return (MessageBoard) the message board with the message when a player scored a raft
     */
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

    /**
     * Return a message board with the message when it is the end of the game
     * @param winners (Set<PlayerColor>) the winners of the game
     * @param points (int) the points of the winners
     * @return (MessageBoard) the message board with the message when it is the end of the game
     */
    public MessageBoard withWinners(Set<PlayerColor> winners, int points) {
        List<Message> newmessages = new ArrayList<>(messages);
        Message message  = new Message(textMaker.playersWon(winners, points), points, winners, Set.of());
        newmessages.add(message);
        return new MessageBoard(this.textMaker, newmessages);
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
