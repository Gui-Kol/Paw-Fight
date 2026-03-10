package com.pawfight.game.world.room;

public class Room {
    private int x, y;
    private boolean north, south, east, west;
    private RoomType type;

    public Room(int x, int y, RoomType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public RoomType getType() { return type; }

    public boolean hasNorth() { return north; }
    public boolean hasSouth() { return south; }
    public boolean hasEast() { return east; }
    public boolean hasWest() { return west; }

    public void connectNorth() { this.north = true; }
    public void connectSouth() { this.south = true; }
    public void connectEast() { this.east = true; }
    public void connectWest() { this.west = true; }
}
