package com.pawfight.game.engine.procedural.sala;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sala")
class SalaTest {

    @Test
    @DisplayName("Index é gerado no formato x,y")
    void indexFormato() {
        Sala sala = new Sala(3, -2, TipoSala.INIMIGOS);
        assertEquals("3,-2", sala.getIndex());
    }

    @Test
    @DisplayName("Sala nova não tem conexões")
    void semConexoesIniciais() {
        Sala sala = new Sala(0, 0, TipoSala.SPAWN);
        assertFalse(sala.hasNorth());
        assertFalse(sala.hasSouth());
        assertFalse(sala.hasEast());
        assertFalse(sala.hasWest());
    }

    @Test
    @DisplayName("connectNorth liga a flag e guarda referência")
    void conectarNorte() {
        Sala a = new Sala(0, 0, TipoSala.INIMIGOS);
        Sala b = new Sala(0, 1, TipoSala.INIMIGOS);
        a.connectNorth(b);
        assertTrue(a.hasNorth());
        assertSame(b, a.getRoom(Direction.NORTH));
    }

    @Test
    @DisplayName("Conexões são simétricas quando ligadas nos dois lados")
    void conexaoSimetrica() {
        Sala a = new Sala(0, 0, TipoSala.INIMIGOS);
        Sala b = new Sala(1, 0, TipoSala.INIMIGOS);
        a.connectEast(b);
        b.connectWest(a);
        assertTrue(a.hasEast());
        assertTrue(b.hasWest());
        assertSame(b, a.getRoom(Direction.EAST));
        assertSame(a, b.getRoom(Direction.WEST));
    }

    @Test
    @DisplayName("getRoom retorna null para direção sem conexão")
    void getRoomSemConexao() {
        Sala sala = new Sala(0, 0, TipoSala.SPAWN);
        assertNull(sala.getRoom(Direction.NORTH));
        assertNull(sala.getRoom(Direction.SOUTH));
        assertNull(sala.getRoom(Direction.EAST));
        assertNull(sala.getRoom(Direction.WEST));
    }
}
