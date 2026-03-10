package com.pawfight.game.world.mundo_areia;

import com.badlogic.gdx.Gdx;

import java.util.*;

public class RoomGenerator {
    private Random random = new Random();

    private Map<String, Room> roomMap = new HashMap<>();

    public List<Room> generate(int numRooms) {
        List<Room> response = gerar(numRooms);
        Room spawn = response.get(0);
        while (!spawn.hasEast() && !spawn.hasNorth() && !spawn.hasSouth() && !spawn.hasWest()){
            response = gerar(numRooms); // Regenera se a sala de spawn não tiver conexões
            spawn = response.get(0);
            Gdx.app.log("RoomGenerator", "Regerando mundo para spawn ter conexões...");
        }

        return response;
    }

    public List<Room> gerar(int numRooms){
        List<Room> rooms = new ArrayList<>();
        Room spawn = new Room(0, 0, RoomType.SPAWN);
        rooms.add(spawn);
        roomMap.put("0,0", spawn);

        // cadeia principal
        for (int i = 1; i < numRooms; i++) {
            RoomType type = (i == numRooms - 1) ? RoomType.BOSS : RoomType.INIMIGOS;

            Room base = rooms.get(i - 1); // sempre conecta à sala anterior
            Room next = null;

            while (next == null) {
                int dir = random.nextInt(4); // 0=N, 1=S, 2=E, 3=W
                int nx = base.getX();
                int ny = base.getY();

                if (dir == 0) ny++;
                else if (dir == 1) ny--;
                else if (dir == 2) nx++;
                else nx--;

                String key = nx + "," + ny;
                if (!roomMap.containsKey(key)) {
                    next = new Room(nx, ny, type);

                    // conecta obrigatoriamente
                    if (dir == 0) { base.connectNorth(); next.connectSouth(); }
                    if (dir == 1) { base.connectSouth(); next.connectNorth(); }
                    if (dir == 2) { base.connectEast(); next.connectWest(); }
                    if (dir == 3) { base.connectWest(); next.connectEast(); }

                    rooms.add(next);
                    roomMap.put(key, next);
                }
            }
        }

        // lista secundária de 5 salas extras
        int extras = 5;
        RoomType[] tipos = {RoomType.INIMIGOS, RoomType.INIMIGOS_FORTES, RoomType.TESOURO};

        for (int i = 0; i < extras; i++) {
            Room base = rooms.get(random.nextInt(rooms.size())); // sala aleatória já existente
            Room next = null;

            while (next == null) {
                int dir = random.nextInt(4);
                int nx = base.getX();
                int ny = base.getY();

                if (dir == 0) ny++;
                else if (dir == 1) ny--;
                else if (dir == 2) nx++;
                else nx--;

                String key = nx + "," + ny;
                if (!roomMap.containsKey(key)) {
                    RoomType type = tipos[random.nextInt(tipos.length)]; // tipo aleatório, nunca BOSS
                    next = new Room(nx, ny, type);

                    if (dir == 0) { base.connectNorth(); next.connectSouth(); }
                    if (dir == 1) { base.connectSouth(); next.connectNorth(); }
                    if (dir == 2) { base.connectEast(); next.connectWest(); }
                    if (dir == 3) { base.connectWest(); next.connectEast(); }

                    rooms.add(next);
                    roomMap.put(key, next);
                }
            }
        }

        return rooms;
    }

    public Map<String, Room> getRoomMap() {
        return roomMap;
    }
}
