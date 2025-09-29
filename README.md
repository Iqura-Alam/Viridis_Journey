# Viridis Journey

*Grid-based city energy management simulation in JavaFX.*


## Overview

Viridis Journey is a single-player, grid-based strategy simulation game where the objective is to power a growing city by placing various energy sources on a grid. Players must strategically manage **budget, pollution, and energy demand** while adapting to **dynamic weather conditions**. The game offers multiple difficulty levels and increasingly complex city layouts, providing a challenging and engaging experience for players.

The user interface is designed with **JavaFX 24** to be intuitive and visually appealing, featuring interactive controls, a dynamic grid display, and real-time feedback on placements and budget.

Viridis Journey follows the **Model-View-Controller (MVC) architecture**, with separate classes for managing the game state, rendering the city grid and HUD, and handling player interactions. The game logic ensures that **energy production, distribution, and pollution mechanics** are correctly simulated, offering a rewarding and educational experience in sustainable resource management.
## Features

- **Energy Source Placement**: Place solar, wind, coal, gas, and battery sources on the grid.  
- **Budget Management**: Manage spending while placing energy sources.  
- **Energy Coverage Check**: Ensure buildings receive enough power.  
- **Weather Effects**: Renewable energy affected by dynamic weather.  
- **Pollution Management**: Balance energy output and environmental impact.  
- **Difficulty Levels**: Easy, Medium, and Hard modes with varying challenges.  
- **Day Progression**: Game advances day by day with updated stats.  
- **Undo & Refund**: Remove sources and get partial refunds.
- **Victory & Failure Conditions**: Meet energy demands and stay within limits.  
- **Scoring System**: Score based on efficiency and sustainability.
## Game Rules

- Place energy sources on empty tiles to power nearby buildings.
- Each source provides energy within its range; buildings outside the range remain unpowered.
- Buildings have specific energy requirements that must be met.
- Pollution increases with coal and gas usage; high pollution affects your score.

**Difficulty Levels:**

- **Easy:** High budget, slow-growing energy demands, minimal pollution penalties, relaxed placement rules.
- **Medium:** Moderate budget, faster energy demand growth, pollution affects score, range checks enforced for priority buildings.
- **Hard:** Low budget, rapid energy demand growth, severe pollution penalties, all buildings must be within energy source range, minimal retries.

The game is won when:

- All buildings receive sufficient energy.
- Budget is not exceeded.
- Pollution remains within limits.
## Controls

### Buttons
- **Start Game / How to Play / About / Settings / Exit:** Navigate the main menu and instructions.  
- **Select Difficulty:** Choose Easy, Medium, or Hard mode before starting a session.  
- **Run Simulation / Next Day / Previous Day:** Progress or rewind the city simulation day by day.  
- **Undo:** Remove the last placed energy source.  
- **Reset Day:** Reset the current day to its initial state.  
- **Play Again:** Restart the game session.

### Mouse Interactions
- **Drag & Drop:** Place energy sources (solar, wind, coal, gas, batteries) on the grid.  
- **Mouse Hover:** Displays information about an energy source, including cost, pollution, and range.  

## Screenshots

### 1. Start Page
<img width="1919" height="1079" alt="Screenshot 2025-09-29 224046" src="https://github.com/user-attachments/assets/8829c3cd-6ca4-46fb-965c-2ad23acdb69f" />


### 2. Difficulty Selection Page
<img width="1919" height="1079" alt="Screenshot 2025-09-29 224247" src="https://github.com/user-attachments/assets/6af48436-e2db-4076-b9ed-afa2c97df318" />


### 3. Weather Overview Page
<img width="1919" height="1079" alt="Screenshot 2025-09-29 224255" src="https://github.com/user-attachments/assets/3823d6cc-4427-48b8-8fd0-e5f8dcbd5488" />


### 4. City Grid 
<img width="1919" height="1079" alt="Screenshot 2025-09-29 224303" src="https://github.com/user-attachments/assets/39643b55-f128-4979-8d67-e8e0a163f7cb" />

### 5. Energy Placement
<img width="1919" height="1079" alt="Screenshot 2025-09-29 224314" src="https://github.com/user-attachments/assets/26a7f041-16ad-4324-b26f-a66ba9fb73e1" />


### 6. Simulation Running
<img width="1919" height="1079" alt="Screenshot 2025-09-29 224444" src="https://github.com/user-attachments/assets/481adf04-41a8-4924-b08d-0d0431b01a76" />


### 7. Victory / Game Over Page
<img width="1919" height="1079" alt="Screenshot 2025-09-29 224518" src="https://github.com/user-attachments/assets/f09d0dbb-d540-4018-8e14-c3118f70130e" />

## Tech Stack

**Frontend:**  
- JavaFX 24  
- SceneBuilder (for UI layout)  

**Backend:**  
- Java 24 (game logic, energy distribution, weather simulation, scoring)  

**Development Tools:**  
- Eclipse IDE (code development, debugging)  
- Git (version control and collaboration)  

**External Libraries / Dependencies:**  
- None (pure JavaFX)  

*The project follows MVC architecture and was developed using the above tools.*
## Project Structure

```text
ViridisJourney/
├── src/
│   ├── application/
│   │   └── Main.java                # Entry point of the application
│   ├── controllers/                 # JavaFX controllers for UI
│   ├── model/                       # Game logic classes (Buildings, EnergySource, Weather, etc.)
│   ├── css/                         # CSS files for styling
│   ├── Font/                        # Font files
│   ├── FXML/                        # FXML layout files
│   ├── img/                         # Images, icons, tiles, etc.
│   └── music/                       # Background music and sound effects
├── README.md
└── .gitignore

```

## How to Run

### Prerequisites
- Java 24 installed
- JavaFX 24 installed
- An IDE (Eclipse, IntelliJ, or any Java IDE)
- Git (optional, for cloning the repository)


1. **Clone the repository** (or download as ZIP):
   ```bash
   git clone https://github.com/MaishaNajAlam/Viridis_Journey.git
### Running the Game

2. **Open the Project**  
   Open the cloned/downloaded project in your preferred Java IDE (Eclipse, IntelliJ, etc.).

3. **Add JavaFX 24 Library**  
   - **Eclipse**: Right-click the project → Properties → Java Build Path → Libraries → Add Library → JavaFX SDK 24  
   - **IntelliJ**: File → Project Structure → Modules → Dependencies → + → JARs or directories → Select JavaFX SDK 24 folder

4. **Run the Main Class**  
   - `Main.java` is the entry point of the application.

5. **Start Playing**  
   The game window will open, and you can start placing energy sources on the city grid.


## Future Enhancements

- Add new energy sources such as nuclear or hydro power.  
- Implement multiplayer or competitive modes.  
- Add more dynamic weather events and seasonal variations.  
- Include a tutorial or guided learning mode for educational purposes.  
- Enhance graphics and grid visuals for a more immersive experience.  

> **Note:** This project was developed purely for academic purposes and is not licensed for commercial use.
