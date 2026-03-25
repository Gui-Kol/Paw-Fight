package com.pawfight.game.engine.procedural.room;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.world.WorldTemplate;

import java.util.*;

public class RoomGenerator {
    private final Random random = new Random();
    private final Map<String, Room> roomMap = new HashMap<>();
    private int tentativasMax;

    public void gerarRooms(WorldTemplate world) {
        try {
            world.setRooms(generate(10, 5, 100));

            if (world.getRooms() == null || world.getRooms().isEmpty()) {
                throw new RuntimeException("Erro: Nenhuma sala foi gerada.");
            }
            world.setCurrentRoom(world.getRooms().get(0));
            world.addSalasVisitadas(world.getCurrentRoom().getX() + "," + world.getCurrentRoom().getY());
            Gdx.app.log(world.getWorldName(), "Salas geradas com sucesso: " + world.getRooms().size());
        } catch (Exception e) {
            Gdx.app.error(world.getWorldName(), "Erro ao gerar salas: " + e.getMessage(), e);
            world.setCurrentRoom(null);
            throw new RuntimeException(e);
        }
    }


    private List<Room> generate(int numRooms, int extras, int tentativasMax) {
        roomMap.clear();
        this.tentativasMax = tentativasMax;

        List<Room> response = gerar(numRooms, extras);
        int tentativas = 0;

        while ((!validarConexoes(response, numRooms) && tentativas < tentativasMax)) {
            roomMap.clear();
            response = gerar(numRooms, extras);
            tentativas++;
            Gdx.app.log("RoomGenerator", "Regenerando mundo porque extras não estão conectados corretamente... (tentativa " + tentativas + ")");
        }

        return response;
    }

    private boolean validarConexoes(List<Room> rooms, int numRooms) {
        // cadeia principal são as primeiras numRooms
        Set<Room> principais = new HashSet<>(rooms.subList(0, numRooms));
        Room spawn = rooms.get(0);

        if (!spawn.hasEast() && !spawn.hasNorth() && !spawn.hasWest() || spawn.hasSouth()) {
            return false; // spawn isolado
        } else if (!typeInimigo(spawn, numRooms)) {
            return false; // spawn incorreto
        }

        for (int i = numRooms; i < rooms.size(); i++) {
            Room extra = rooms.get(i);
            boolean conectado = false;

            // checa se alguma conexão do extraleva a uma sala principal
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

    private boolean typeInimigo(Room room, int numRooms) {
        for (Direction dir : Direction.values()) {
            Room vizinho = room.getRoom(dir);
            if (vizinho != null && vizinho.getType() == RoomType.INIMIGOS) {
                if (verifyTypeInimigo(vizinho.getX(), numRooms)) return true;
            }
        }
        return false;
    }


    private boolean verifyTypeInimigo(int index, int numRooms) {
        return index >= 0 && index < numRooms;
    }

    private void conectar(Room base, Room next, Direction dir) {
        switch (dir) {
            case NORTH -> {
                base.connectNorth(next);
                next.connectSouth(base);
            }
            case SOUTH -> {
                base.connectSouth(next);
                next.connectNorth(base);
            }
            case EAST -> {
                base.connectEast(next);
                next.connectWest(base);
            }
            case WEST -> {
                base.connectWest(next);
                next.connectEast(base);
            }
        }
    }


    private boolean saoConectados(Room a, Room b) {
        if (a.getX() == b.getX() && a.getY() == b.getY() + 1 && a.hasSouth() && b.hasNorth()) return true;
        if (a.getX() == b.getX() && a.getY() == b.getY() - 1 && a.hasNorth() && b.hasSouth()) return true;
        if (a.getX() == b.getX() + 1 && a.getY() == b.getY() && a.hasWest() && b.hasEast()) return true;
        return a.getX() == b.getX() - 1 && a.getY() == b.getY() && a.hasEast() && b.hasWest();
    }


    private List<Room> gerar(int numRooms, int extras) {
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
                Direction dir = Direction.values()[random.nextInt(Direction.values().length)];
                int nx = base.getX();
                int ny = base.getY();

                switch (dir) {
                    case NORTH -> ny++;
                    case SOUTH -> ny--;
                    case EAST -> nx++;
                    case WEST -> nx--;
                }

                String key = nx + "," + ny;
                if (!roomMap.containsKey(key)) {
                    next = new Room(nx, ny, type);
                    conectar(base, next, dir);

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
            Direction dir = Direction.values()[random.nextInt(Direction.values().length)];

            int nx = boss.getX();
            int ny = boss.getY();

            switch (dir) {
                case NORTH -> ny++;
                case SOUTH -> ny--;
                case EAST -> nx++;
                case WEST -> nx--;
            }

            String key = nx + "," + ny;
            if (!roomMap.containsKey(key)) {
                treasure = new Room(nx, ny, RoomType.TESOURO);
                conectar(boss, treasure, dir);

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
                Direction dir = Direction.values()[random.nextInt(Direction.values().length)];
                int nx = base.getX();
                int ny = base.getY();

                switch (dir) {
                    case NORTH -> ny++;
                    case SOUTH -> ny--;
                    case EAST -> nx++;
                    case WEST -> nx--;
                }

                String key = nx + "," + ny;
                if (!roomMap.containsKey(key)) {
                    RoomType type = tipos[random.nextInt(tipos.length)];
                    next = new Room(nx, ny, type);
                    conectar(base, next, dir);

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
