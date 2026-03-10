package com.pawfight.game.world.room;

import com.badlogic.gdx.Gdx;

import java.util.*;

public class RoomGenerator {
    private final Random random = new Random();
    private final Map<String, Room> roomMap = new HashMap<>();

    public List<Room> generate(int numRooms, int extras) {
        roomMap.clear(); // IMPORTANTE: Limpa mapa anterior

        List<Room> response = gerar(numRooms, extras);
        Room spawn = response.get(0);
        int tentativas = 0;

        while ((!spawn.hasEast() && !spawn.hasNorth() && spawn.hasSouth() && !spawn.hasWest()) && tentativas < 5) {
            roomMap.clear(); // Limpa antes de regenerar
            response = gerar(numRooms, extras);
            spawn = response.get(0);
            tentativas++;
            Gdx.app.log("RoomGenerator", "Regenerando mundo para spawn ter conexões... (tentativa " + tentativas + ")");
        }

        return response;
    }

    public List<Room> gerar(int numRooms,int extras) {
        List<Room> rooms = new ArrayList<>();
        Room spawn = new Room(0, 0, RoomType.SPAWN);
        rooms.add(spawn);
        roomMap.put("0,0", spawn);

        // cadeia principal
        for (int i = 1; i < numRooms; i++) {
            RoomType type = (i == numRooms - 1) ? RoomType.BOSS : RoomType.INIMIGOS;
            Room base = rooms.get(i - 1);
            Room next = null;
            int tentativasLocal = 0;

            while (next == null && tentativasLocal < 10) {
                int dir = random.nextInt(4);
                int nx = base.getX();
                int ny = base.getY();

                if (dir == 0) ny++;
                else if (dir == 1) ny--;
                else if (dir == 2) nx++;
                else nx--;

                String key = nx + "," + ny;
                if (!roomMap.containsKey(key)) {
                    next = new Room(nx, ny, type);

                    if (dir == 0) {
                        base.connectNorth();
                        next.connectSouth();
                    }
                    if (dir == 1) {
                        base.connectSouth();
                        next.connectNorth();
                    }
                    if (dir == 2) {
                        base.connectEast();
                        next.connectWest();
                    }
                    if (dir == 3) {
                        base.connectWest();
                        next.connectEast();
                    }

                    rooms.add(next);
                    roomMap.put(key, next);
                } else {
                    tentativasLocal++;
                }
            }
        }

        // Adicionar sala de TESOURO sempre após a sala CHEFE
        Room boss = rooms.get(rooms.size() - 1); // Última sala da cadeia principal é CHEFE
        Room treasure = null;
        int tentativasTreasure = 0;

        while (treasure == null && tentativasTreasure < 10) {
            int dir = random.nextInt(4);
            int nx = boss.getX();
            int ny = boss.getY();

            if (dir == 0) ny++;
            else if (dir == 1) ny--;
            else if (dir == 2) nx++;
            else nx--;

            String key = nx + "," + ny;
            if (!roomMap.containsKey(key)) {
                treasure = new Room(nx, ny, RoomType.TESOURO);

                if (dir == 0) {
                    boss.connectNorth();
                    treasure.connectSouth();
                }
                if (dir == 1) {
                    boss.connectSouth();
                    treasure.connectNorth();
                }
                if (dir == 2) {
                    boss.connectEast();
                    treasure.connectWest();
                }
                if (dir == 3) {
                    boss.connectWest();
                    treasure.connectEast();
                }

                rooms.add(treasure);
                roomMap.put(key, treasure);
            } else {
                tentativasTreasure++;
            }
        }

        // lista secundária de 5 salas extras
        RoomType[] tipos = {RoomType.INIMIGOS, RoomType.INIMIGOS_FORTES, RoomType.TESOURO};

        for (int i = 0; i < extras; i++) {
            Room base = rooms.get(random.nextInt(rooms.size()));
            Room next = null;
            int tentativasLocal = 0;

            while (next == null && tentativasLocal < 10) {
                int dir = random.nextInt(4);
                int nx = base.getX();
                int ny = base.getY();

                if (dir == 0) ny++;
                else if (dir == 1) ny--;
                else if (dir == 2) nx++;
                else nx--;

                String key = nx + "," + ny;
                if (!roomMap.containsKey(key)) {
                    RoomType type = tipos[random.nextInt(tipos.length)];
                    next = new Room(nx, ny, type);

                    if (dir == 0) {
                        base.connectNorth();
                        next.connectSouth();
                    }
                    if (dir == 1) {
                        base.connectSouth();
                        next.connectNorth();
                    }
                    if (dir == 2) {
                        base.connectEast();
                        next.connectWest();
                    }
                    if (dir == 3) {
                        base.connectWest();
                        next.connectEast();
                    }

                    rooms.add(next);
                    roomMap.put(key, next);
                } else {
                    tentativasLocal++;
                }
            }
        }

        return rooms;
    }

    public Map<String, Room> getRoomMap() {
        return new HashMap<>(roomMap); // Retorna cópia para segurança
    }
}
