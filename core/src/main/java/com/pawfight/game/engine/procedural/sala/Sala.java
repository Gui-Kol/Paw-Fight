package com.pawfight.game.engine.procedural.sala;

public class Sala {
    private final int x, y;
    private final String index;
    private boolean north, south, east, west;
    private final TipoSala type;

    // Referências para salas vizinhas
    private Sala northRoom, southRoom, eastRoom, westRoom;

    public Sala(int x, int y, TipoSala type) {
        this.x = x;
        this.y = y;
        this.type = type;
        index = x + "," + y;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public TipoSala getType() { return type; }

    public boolean hasNorth() { return north; }
    public boolean hasSouth() { return south; }
    public boolean hasEast() { return east; }
    public boolean hasWest() { return west; }

    // Métodos de conexão que também guardam referência
    public void connectNorth(Sala sala) {
        this.north = true;
        this.northRoom = sala;
    }
    public void connectSouth(Sala sala) {
        this.south = true;
        this.southRoom = sala;
    }
    public void connectEast(Sala sala) {
        this.east = true;
        this.eastRoom = sala;
    }
    public void connectWest(Sala sala) {
        this.west = true;
        this.westRoom = sala;
    }

    public Sala getRoom(Direction dir){
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
