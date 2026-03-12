package com.pawfight.game.engine.procedural;

import com.badlogic.gdx.Gdx;

import java.util.*;

public class RoomGenerator {
    private final Random random = new Random();
    private final Map<String, Room> roomMap = new HashMap<>();
    private int tentativasMax = 10;


    public List<Room> generate(int numRooms, int extras) {
        roomMap.clear();

        List<Room> response = gerar(numRooms, extras);
        int tentativas = 0;

        while (!validarConexoes(response, numRooms) && tentativas < tentativasMax) {
            roomMap.clear();
            response = gerar(numRooms, extras);
            tentativas++;
            Gdx.app.log("RoomGenerator", "Regenerando mundo porque extras não estão conectados à cadeia principal... (tentativa " + tentativas + ")");
        }

        return response;
    }

    private boolean validarConexoes(List<Room> rooms, int numRooms) {
        // cadeia principal são as primeiras numRooms
        Set<Room> principais = new HashSet<>(rooms.subList(0, numRooms));
        Room spawn = rooms.get(0);

        for (int i = numRooms; i < rooms.size(); i++) {
            Room extra = rooms.get(i);
            boolean conectado = false;

            if (!spawn.hasEast() && !spawn.hasNorth() && !spawn.hasWest()) {
                return false; // spawn isolado
            }

            // checa se alguma conexão do extra leva a uma sala principal
            for (Room principal : principais) {
                if (saoConectados(extra, principal)) {
                    conectado = true;
                    break;
                }
            }

            if (!conectado) {
                return false; // achou um extra isolado
            }
        }
        return true;
    }
    private boolean saoConectados(Room a, Room b) {
        if (a.getX() == b.getX() && a.getY() == b.getY() + 1 && a.hasSouth() && b.hasNorth()) return true;
        if (a.getX() == b.getX() && a.getY() == b.getY() - 1 && a.hasNorth() && b.hasSouth()) return true;
        if (a.getX() == b.getX() + 1 && a.getY() == b.getY() && a.hasWest() && b.hasEast()) return true;
        if (a.getX() == b.getX() - 1 && a.getY() == b.getY() && a.hasEast() && b.hasWest()) return true;
        return false;
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

            while (next == null && tentativasLocal < tentativasMax) {
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
                    Gdx.app.log("RoomGenerator", "Sala gerada em " + tentativasLocal + " tentativas");
                } else {
                    tentativasLocal++;
                }
            }
        }

        // Adicionar sala de TESOURO sempre após a sala CHEFE
        Room boss = rooms.get(rooms.size() - 1); // Última sala da cadeia principal é CHEFE
        Room treasure = null;
        int tentativasTreasure = 0;

        while (treasure == null && tentativasTreasure < tentativasMax) {
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
                Gdx.app.log("RoomGenerator", "Sala do Tesouro gerada em " + tentativasTreasure + " tentativas");
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

            while (next == null && tentativasLocal < tentativasMax) {
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
                    Gdx.app.log("RoomGenerator", "Sala extras gerada em " + tentativasLocal + " tentativas");
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
