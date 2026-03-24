package com.pawfight.game.engine.procedural.room;

public class Room {
    private final int x, y;
    private final String index;
    private boolean north, south, east, west;
    private final RoomType type;

    // Referências para salas vizinhas
    private Room northRoom, southRoom, eastRoom, westRoom;

    public Room(int x, int y, RoomType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        index = x + "," + y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public RoomType getType() { return type; }

    public boolean hasNorth() { return north; }
    public boolean hasSouth() { return south; }
    public boolean hasEast() { return east; }
    public boolean hasWest() { return west; }

    // Métodos de conexão que também guardam referência
    public void connectNorth(Room room) {
        this.north = true;
        this.northRoom = room;
    }
    public void connectSouth(Room room) {
        this.south = true;
        this.southRoom = room;
    }
    public void connectEast(Room room) {
        this.east = true;
        this.eastRoom = room;
    }
    public void connectWest(Room room) {
        this.west = true;
        this.westRoom = room;
    }

    public Room getRoom(Direction dir){
        switch (dir) {
            case NORTH -> { return northRoom; }
            case SOUTH -> { return southRoom; }
            case EAST  -> { return eastRoom; }
            case WEST  -> { return westRoom; }
        }
        return null;
    }


    public String getIndex() {
        return index;
    }
}
