package model;

public enum TileType {
    EMPTY,      // Tile is empty, no energy source or building
    SOURCE_REF, // Tile contains an energy source
    CONSUMER,   // Tile contains a building (house, school, etc.)
    OBSTACLE    // Tile cannot be used (e.g., part of the map that's blocked)
}
