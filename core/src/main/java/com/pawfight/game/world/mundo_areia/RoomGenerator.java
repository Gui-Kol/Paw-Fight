package com.pawfight.game.world.mundo_areia;

import java.util.*;

public class RoomGenerator {
    private Random random = new Random();

    private Map<String, Room> roomMap = new HashMap<>();

    public List<Room> generate(int numRooms) {
        List<Room> rooms = new ArrayList<>();
        Room spawn = new Room(0, 0, RoomType.SPAWN);
        rooms.add(spawn);
        roomMap.put("0,0", spawn);

        Random random = new Random();

        for (int i = 1; i < numRooms; i++) {
            RoomType type = (i == numRooms - 1) ? RoomType.BOSS : RoomType.INIMIGOS;

            Room base = rooms.get(random.nextInt(rooms.size())); // pega uma sala já existente
            Room next = null;

            // tenta até achar uma posição livre
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

                    // conecta as duas salas
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
