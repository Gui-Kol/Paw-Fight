package com.pawfight.game.engine.procedural.sala;

import com.badlogic.gdx.Gdx;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.*;

public class GeradorSalas {
    private final Random random = new Random();
    private final Map<String, Sala> roomMap = new HashMap<>();
    private int tentativasMax;

    public void gerarRooms(WorldTemplate world) {
        try {
            world.getRoomManager().setRooms(generate(10, 5, 100));

            if (world.getRoomManager().getRooms() == null || world.getRoomManager().getRooms().isEmpty()) {
                throw new RuntimeException("Erro: Nenhuma sala foi gerada.");
            }
            world.getRoomManager().setCurrentRoom(world.getRoomManager().getRooms().get(0));
            world.getRoomManager().addSalasVisitadas(world.getRoomManager().getCurrentRoom().getX() + "," + world.getRoomManager().getCurrentRoom().getY());
            Gdx.app.log(world.getWorldName(), "Salas geradas com sucesso: " + world.getRoomManager().getRooms().size());
        } catch (Exception e) {
            Gdx.app.error(world.getWorldName(), "Erro ao gerar salas: " + e.getMessage(), e);
            world.getRoomManager().setCurrentRoom(null);
            throw new RuntimeException(e);
        }
    }


    private List<Sala> generate(int numRooms, int extras, int tentativasMax) {
        roomMap.clear();
        this.tentativasMax = tentativasMax;

        List<Sala> response = gerar(numRooms, extras);
        int tentativas = 0;

        // Cadeia completa = numRooms principais + 1 tesouro pós-boss.
        // Menos que isso significa que a cadeia ficou encurralada e deve ser regerada
        // (antes, validarConexoes estourava com IndexOutOfBoundsException em subList).
        while ((response.size() <= numRooms || !validarConexoes(response, numRooms)) && tentativas < tentativasMax) {
            roomMap.clear();
            response = gerar(numRooms, extras);
            tentativas++;
            Gdx.app.log("GeradorSalas", "Regenerando mundo porque extras não estão conectados corretamente... (tentativa " + tentativas + ")");
        }

        if (response.size() <= numRooms) {
            throw new IllegalStateException(
                "Falha ao gerar mapa válido após " + tentativasMax + " tentativas (cadeia principal incompleta).");
        }

        return response;
    }

    private boolean validarConexoes(List<Sala> rooms, int numRooms) {
        // cadeia principal são as primeiras numRooms
        Set<Sala> principais = new HashSet<>(rooms.subList(0, numRooms));
        Sala spawn = rooms.get(0);

        if (!spawn.hasEast() && !spawn.hasNorth() && !spawn.hasWest() || spawn.hasSouth()) {
            return false; // spawn isolado
        } else if (!typeInimigo(spawn, numRooms)) {
            return false; // spawn incorreto
        }

        for (int i = numRooms; i < rooms.size(); i++) {
            Sala extra = rooms.get(i);
            boolean conectado = false;

            // checa se alguma conexão do extraleva a uma sala principal
            for (Sala principal : principais) {
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

    private boolean typeInimigo(Sala sala, int numRooms) {
        for (Direction dir : Direction.values()) {
            Sala vizinho = sala.getRoom(dir);
            if (vizinho != null && vizinho.getType() == TipoSala.INIMIGOS) {
                if (verifyTypeInimigo(vizinho.getX(), numRooms)) return true;
            }
        }
        return false;
    }


    private boolean verifyTypeInimigo(int index, int numRooms) {
        return index >= 0 && index < numRooms;
    }

    private void conectar(Sala base, Sala next, Direction dir) {
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


    private boolean saoConectados(Sala a, Sala b) {
        if (a.getX() == b.getX() && a.getY() == b.getY() + 1 && a.hasSouth() && b.hasNorth()) return true;
        if (a.getX() == b.getX() && a.getY() == b.getY() - 1 && a.hasNorth() && b.hasSouth()) return true;
        if (a.getX() == b.getX() + 1 && a.getY() == b.getY() && a.hasWest() && b.hasEast()) return true;
        return a.getX() == b.getX() - 1 && a.getY() == b.getY() && a.hasEast() && b.hasWest();
    }


    private List<Sala> gerar(int numRooms, int extras) {
        List<Sala> rooms = new ArrayList<>();
        Sala spawn = new Sala(0, 0, TipoSala.SPAWN);
        rooms.add(spawn);
        roomMap.put("0,0", spawn);

        // cadeia principal
        for (int i = 1; i < numRooms; i++) {
            TipoSala type = (i == numRooms - 1) ? TipoSala.BOSS : TipoSala.INIMIGOS;
            Sala base = rooms.get(i - 1);
            Sala next = null;
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
                    next = new Sala(nx, ny, type);
                    conectar(base, next, dir);

                    rooms.add(next);
                    roomMap.put(key, next);
                    Gdx.app.log("GeradorSalas", "Sala gerada em " + tentativasLocal + " tentativas");
                } else {
                    tentativasLocal++;
                }
            }

            // Se a cadeia ficou encurralada (sem célula livre), desiste: devolve a lista
            // incompleta para que generate() regenere o mapa inteiro.
            if (next == null) {
                Gdx.app.log("GeradorSalas", "Cadeia principal encurralada em " + i + " salas; regenerando...");
                return rooms;
            }
        }

        // Adicionar sala de TESOURO sempre após a sala CHEFE
        Sala boss = rooms.get(rooms.size() - 1); // Última sala da cadeia principal é CHEFE
        Sala treasure = null;
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
                treasure = new Sala(nx, ny, TipoSala.TESOURO);
                conectar(boss, treasure, dir);

                rooms.add(treasure);
                roomMap.put(key, treasure);
                Gdx.app.log("GeradorSalas", "Sala do Tesouro gerada em " + tentativasTreasure + " tentativas");
            } else {
                tentativasTreasure++;
            }
        }

        // lista secundária de 5 salas extras
        TipoSala[] tipos = {TipoSala.INIMIGOS, TipoSala.INIMIGOS_FORTES, TipoSala.TESOURO};

        for (int i = 0; i < extras; i++) {
            Sala base = rooms.get(random.nextInt(rooms.size()));
            Sala next = null;
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
                    TipoSala type = tipos[random.nextInt(tipos.length)];
                    next = new Sala(nx, ny, type);
                    conectar(base, next, dir);

                    rooms.add(next);
                    roomMap.put(key, next);
                    Gdx.app.log("GeradorSalas", "Sala extras gerada em " + tentativasLocal + " tentativas");
                } else {
                    tentativasLocal++;
                }
            }
        }

        return rooms;
    }

    public Map<String, Sala> getRoomMap() {
        return new HashMap<>(roomMap); // Retorna cópia para segurança
    }
}
