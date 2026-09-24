package com.pawfight.game.engine.procedural.sala;

import com.pawfight.game.HeadlessGdx;
import com.pawfight.game.world.template.RoomManager;
import com.pawfight.game.world.template.WorldTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

// RoomManager real carrega fontes via Gdx.files (DesenharMiniMapa); o mock captura rooms, currentRoom e salasVisitadas sem gráficos.
@DisplayName("GeradorSalas")
class GeradorSalasTest {

    private final AtomicReference<List<Sala>> rooms = new AtomicReference<>();
    private final AtomicReference<Sala> currentRoom = new AtomicReference<>();
    private final Set<CoordenadaSala> salasVisitadas = new HashSet<>();

    @BeforeAll
    static void initGdx() {
        HeadlessGdx.ensureGdx(); // gerador chama Gdx.app.log()
    }

    @BeforeEach
    void setUp() {
        GeradorSalas gerador = new GeradorSalas();
        rooms.set(null);
        currentRoom.set(null);
        salasVisitadas.clear();

        gerador.gerarRooms(mockWorld());
    }

    private WorldTemplate mockWorld() {
        RoomManager roomManager = mock(RoomManager.class);
        doAnswer(inv -> { rooms.set(new ArrayList<>(inv.getArgument(0))); return null; })
            .when(roomManager).setRooms(anyList());
        when(roomManager.getRooms()).thenAnswer(inv -> rooms.get());
        doAnswer(inv -> { currentRoom.set(inv.getArgument(0)); return null; })
            .when(roomManager).setCurrentRoom(any());
        when(roomManager.getCurrentRoom()).thenAnswer(inv -> currentRoom.get());
        doAnswer(inv -> { salasVisitadas.add(inv.getArgument(0)); return null; })
            .when(roomManager).addSalaVisitada(any(CoordenadaSala.class));

        WorldTemplate world = mock(WorldTemplate.class);
        when(world.getRoomManager()).thenReturn(roomManager);
        when(world.getWorldName()).thenReturn("MundoTeste");
        return world;
    }

    @Test
    @DisplayName("Gera salas com sucesso")
    void geraSalas() {
        assertNotNull(rooms.get());
        assertFalse(rooms.get().isEmpty());
    }

    @Test
    @DisplayName("Primeira sala é o SPAWN na posição 0,0")
    void spawnNaOrigem() {
        Sala primeira = rooms.get().get(0);
        assertEquals(TipoSala.SPAWN, primeira.getType());
        assertEquals(0, primeira.getX());
        assertEquals(0, primeira.getY());
    }

    @Test
    @DisplayName("Sala atual é definida e marcada como visitada")
    void salaAtualVisitada() {
        Sala atual = currentRoom.get();
        assertNotNull(atual);
        assertTrue(salasVisitadas.contains(CoordenadaSala.de(atual)));
    }

    @Test
    @DisplayName("Existe exatamente uma sala BOSS")
    void umBoss() {
        long bosses = rooms.get().stream()
            .filter(s -> s.getType() == TipoSala.BOSS)
            .count();
        assertEquals(1, bosses);
    }

    @Test
    @DisplayName("Existe sala TESOURO conectada (pós-boss)")
    void tesouroExiste() {
        long tesouros = rooms.get().stream()
            .filter(s -> s.getType() == TipoSala.TESOURO)
            .count();
        assertTrue(tesouros >= 1, "Deveria existir ao menos 1 sala tesouro (gerada após o boss)");
    }

    @Test
    @DisplayName("Não há duas salas na mesma posição")
    void posicoesUnicas() {
        Set<String> posicoes = new HashSet<>();
        for (Sala sala : rooms.get()) {
            String chave = sala.getX() + "," + sala.getY();
            assertTrue(posicoes.add(chave), "Posição duplicada: " + chave);
        }
    }

    @Test
    @DisplayName("Todas as salas são alcançáveis a partir do spawn (BFS nas conexões)")
    void todasAlcancaveis() {
        List<Sala> salas = rooms.get();
        Sala spawn = salas.get(0);

        Set<Sala> visitadas = new HashSet<>();
        ArrayDeque<Sala> fila = new ArrayDeque<>();
        fila.add(spawn);
        visitadas.add(spawn);

        while (!fila.isEmpty()) {
            Sala atual = fila.poll();
            for (Direction dir : Direction.values()) {
                Sala vizinha = atual.getRoom(dir);
                if (vizinha != null && visitadas.add(vizinha)) {
                    fila.add(vizinha);
                }
            }
        }

        assertEquals(salas.size(), visitadas.size(),
            "Toda sala gerada deve ser alcançável a partir do spawn");
    }

    @Test
    @DisplayName("Conexões são bidirecionais (ir e voltar)")
    void conexoesBidirecionais() {
        for (Sala sala : rooms.get()) {
            assertBidirecional(sala, Direction.NORTH, Direction.SOUTH);
            assertBidirecional(sala, Direction.SOUTH, Direction.NORTH);
            assertBidirecional(sala, Direction.EAST, Direction.WEST);
            assertBidirecional(sala, Direction.WEST, Direction.EAST);
        }
    }

    private void assertBidirecional(Sala sala, Direction ida, Direction volta) {
        Sala vizinha = sala.getRoom(ida);
        if (vizinha != null) {
            assertSame(sala, vizinha.getRoom(volta),
                "Conexão " + ida + " deveria ser simétrica");
        }
    }

    @RepeatedTest(5)
    @DisplayName("Gerações repetidas produzem mapas válidos e dentro de limites esperados")
    void geracoesRepetidas() {
        GeradorSalas g = new GeradorSalas();
        g.gerarRooms(mockWorld());

        List<Sala> salas = rooms.get();
        assertNotNull(salas);
        assertTrue(salas.size() >= 10, "Cadeia principal + tesouro devem existir");
        assertTrue(salas.size() <= 16, "Máximo: 10 principais + 1 tesouro + 5 extras");
    }
}
