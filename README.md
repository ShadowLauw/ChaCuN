# ChaCuN

ChaCuN is a digital adaptation of the popular board game Carcassonne chasseurs et cueilleurs, developed in Java. This project aims to bring the classic tile-placement game to your computer, allowing you to enjoy it solo or with friends.

## Features

- **Digital Carcassonne**: Experience the traditional tile-placement mechanics of Carcassonne on your computer.
- **Multiplayer Support**: Play with friends locally.
- **User-Friendly Interface**: Intuitive design for easy navigation and gameplay.

## **Launching the Game in IntelliJ IDEA**

### **1. Clone the Repository**  
First, clone the repository to your local machine using the following command:
```bash
git clone https://github.com/ShadowLauw/ChaCuN.git
```

### **2. Open the Project in IntelliJ IDEA**  
- Open IntelliJ IDEA.
- Select **Open** and choose the `ChaCuN` folder that you cloned.

### **3. Set the Run Configuration in IntelliJ IDEA**

1. Open the `Main.java` file. This file contains the entry point for the game.

2. **Create a Run Configuration**:
   - In the top-right corner of IntelliJ IDEA, click the **Run/Debug Configurations** dropdown and select **Edit Configurations**.
   - Click the **+** button on the top left and select **Application**.
   - In the **Name** field, name the configuration (e.g., `ChaCuN`).
   - In the **Main class** field, select `Main`.

3. **Add Program Arguments**:
   In the **Program arguments** field, you will enter the player names and the optional seed argument when running the game. For example:
   
   - To start a game with **Dalia** and **Claude**, and use the seed `2024`, enter:
     ```text
     Dalia Claude --seed=2024
     ```
   - If you don't want to use a specific seed, you can leave the **Program arguments** field blank or omit the `--seed` argument.
   
   The argument structure is as follows:
   - Player names (in order) are passed as space-separated values.
   - The optional seed can be passed as `--seed=<seed_value>`, where `<seed_value>` is an integer (64-bit).

4. **Save and Run**:
   - Click **OK** to save your run configuration.
   - Now, you can select your configuration from the **Run/Debug Configurations** dropdown and click the green play button to run the game.

### **Example Run Configuration**

For a game with **Dalia** (red) and **Claude** (blue), with a seed of `2024`, your **Program arguments** field should look like this:
```text
Dalia Claude --seed=2024
```

If you don't specify the seed, it will be chosen randomly.

### **4. Invalid Arguments Handling**  
If the following invalid arguments are provided, the game will throw an exception:
- **Number of players** is less than 2 or more than 5.
- **Invalid seed** (not a valid 64-bit integer).
  
## **How to Play**

ChaCuN is a strategy board game designed for **2 to 5 players**. The goal is to gradually build a prehistoric landscape by placing square tiles next to each other. Various areas of the landscape, such as forests, rivers, and plains, can be occupied by hunters, gatherers, or fishermen to earn points.

The game starts with a single **starting tile** placed at the center of the board, and each player takes turns drawing and placing tiles.

### **1. Initial Setup**
When running the main with good parameters, game begins with the **starting tile**, which forms the initial landscape. The first tile is visible and placed in the center of the game board.

![image](https://github.com/user-attachments/assets/8d663845-d99f-426b-9e91-999ca1aea7e6)

The UI consists of the board on the left, the players and their meeples, hut and points on the top right. Under the players is the game message box, containing messages when players earn points. Under this is the action box for the remote play. Then there are the two decks of tiles, normal on the left and menhir on the right, and then the image of the tile to place.

### **2. Placing Tiles**
Each player takes turns drawing a tile from the draw pile and placing it on the game board. A tile must be placed adjacent to an already placed tile, with the edges matching to form a continuous landscape.

- Players can rotate the tile before placing it, but it must always fit with the landscape already on the board.
- **Example**: If the first player draws a tile with a forest on the left side, they could place it to the right of the starting tile, aligning the forest edges to continue the landscape.

- The fringe cells (adjacent cells of the one already placed) are highlighted in the current player's color (e.g., red for player 1, blue for player 2). This indicates where the player can potentially place their tile during their turn.
- If you hover your mouse cursor over a fringe cell, the current tile you have selected will appear on that cell. If the tile can be placed there, the previewed tile will show up normally. If the tile cannot be placed due to misalignment or other placement rules, the tile image will be covered by a white veil, signaling that placement is invalid.

  Invalid placement:
  ![image](https://github.com/user-attachments/assets/37988dd7-53fe-4796-9a4b-c4ddd776f20b)
  Valid placement:
  ![image](https://github.com/user-attachments/assets/345ac747-12ce-4257-b1e7-427549625d68)

After placing a tile, a player may have the opportunity to place a **second tile** under certain conditions, as follows:
- If the tile placed **completes a forest** that contains a **menhir** (a special feature tile), the player may place a second tile from the **menhir pile**. Menhir tiles are typically more valuable and may grant special abilities.

### **3. Occupying Tiles**
After placing a tile, a player may choose to **occupy** it with one of their **meeples** (5 pions) or one of their **huts** (3 huts).

![image](https://github.com/user-attachments/assets/ea793d82-a6dd-459b-8da1-67681bff6d61)

- The types of occupancy vary depending on the type of landscape:
  - **Forests**: Occupied by **gatherers** (meeples).
  - **Rivers**: Occupied by **fishermen** (meeples).
  - **Plains**: Occupied by **hunters** (meeples).
  - **Hydrographic Networks**: Occupied by **fishing huts** (huts), which are placed in lakes or rivers connected by other water elements.

Occupying specific features allows players to earn points at different stages of the game. For example, in a **closed forest**, the player with the most gatherers in that forest earns points, and all gatherers are returned to their owner after scoring, so they can be reused for future moves.

If a player doesn't want to occupy the tile, they can click on the tile to place position that changed to a text saying "Click on the meeple or the hut you want to place, or here if you don't".

![image](https://github.com/user-attachments/assets/c0f85232-6778-454c-a92e-276408881119)

### **4. Scoring**
Players score points by completing features in the landscape:
- **Forests**: When a forest is fully closed (surrounded by other tiles or landscape elements), the players with the most **gatherers** in the forest earn points.
- **Rivers**: When a river is completed (it is connected on both ends), the players with the most **fishermen** in the river earn points.
  - Rivers can be terminated by a lake or another landscape element, which "completes" the river and allows for scoring.
- **Plains**: **Hunters** in the plains earn points, but these points are only counted at the **end of the game**, when the last tile is placed.
- **Hydrographic Networks**: Similar to rivers, **fishing huts** in hydrographic networks are counted at the **end of the game**, once the network is completed.

Points are awarded based on the number of occupants in the closed zone, plus number of animals for plains, or fishes for the hydrographic networks. 

### **5. Special Features**
- **Menhirs**: These are special tiles that can trigger additional actions, such as granting players the ability to place a second tile. Menhir tiles are also worth more points when used.
- **Reclaiming Occupants**: When a forest, river, or other feature is completed, all occupied **meeples** and **huts** are returned to their owners and can be reused for future moves.

### **6. End of the Game**
The game ends when all normal tiles have been placed. At that point, players will calculate their final points based on:
- The points from completed forests, rivers, and plains.
- The number of **hunters** and **fishermen** remaining on the board at the time the game ends.

Players cannot reclaim **hunters** or **fishing huts** once the game ends; they remain in place until the game finishes and contribute to scoring at the end.

### **Controls**

- Left-click: Place the currently selected tile onto the board at a valid position. You must ensure that the edges of the tile match the surrounding landscape to form a continuous feature.
- Right-click: Rotate the current tile counterclockwise. This allows you to adjust the tile to fit better with the landscape.
- Alt + Right-click (or Option + Right-click on Mac): Rotate the current tile clockwise. This provides another way to adjust the tile’s orientation before placement.

## **Remote Play**  

ChaCuN also supports **remote play**, allowing players to connect and play the game from different locations, provided they use the same game parameters (player names and seed). The game relies on a system of **move codes**, which will be generated after each move and shared between players to sync the game state across different devices.

### **How Remote Play Works**
- When starting a remote game, all players must use the **same seed** and **same player names** in the same order. This ensures that the game board, tile order, and player turns are identical for all participants.
- Once the game begins, each player will be provided with a **move code** after their turn. This code contains all the necessary information about the move and can be shared with the next player.
- The next player enters the **move code** to apply the move on their own system. This keeps the game in sync, ensuring that players are always on the same page.

![image](https://github.com/user-attachments/assets/20ac2c49-1b6d-4635-861a-044af7ed76a0)

### **Example for Remote Play**
1. **Player Setup**:  Player 1 (Dalia) and Player 2 (Claude) start the game with the same arguments passed, configuring a set seed
2. **Move Codes**: After each turn, Player 1 will receive a **move code**. Player 2 will enter this code to continue from the same state on their system.
3. **Syncing the Game**: As long as both players use the same seed and share their move codes correctly, the game will remain synchronized.

For detailed rules and strategies, refer to the [official Carcassonne rulebook]([https://cdn.1j1ju.com/medias/2b/62/02-carcassonne-chasseurs-et-cueilleurs-regle.pdf]).
