package com.pawfight.game.engine.fisica;

import com.badlogic.gdx.math.Rectangle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Quadtree")
class QuadtreeTest {

    private Quadtree<String> quadtree;
    private final Rectangle limites = new Rectangle(0, 0, 100, 100);

    @BeforeEach
    void setUp() {
        quadtree = new Quadtree<>(limites);
    }

    private List<String> consultar(Rectangle regiao) {
        List<String> resultado = new ArrayList<>();
        quadtree.consultar(regiao, resultado);
        return resultado;
    }

    @Test
    @DisplayName("Consulta vazia retorna lista vazia")
    void consultaVazia() {
        assertTrue(consultar(new Rectangle(0, 0, 50, 50)).isEmpty());
    }

    @Test
    @DisplayName("Item inserido é encontrado na região correta")
    void insercaoEConsulta() {
        quadtree.inserir("A", new Rectangle(10, 10, 5, 5));
        List<String> resultado = consultar(new Rectangle(0, 0, 50, 50));
        assertEquals(1, resultado.size());
        assertTrue(resultado.contains("A"));
    }

    @Test
    @DisplayName("Item fora dos limites da quadtree é ignorado")
    void itemForaDosLimites() {
        quadtree.inserir("FORA", new Rectangle(200, 200, 5, 5));
        assertTrue(consultar(new Rectangle(190, 190, 30, 30)).isEmpty());
    }

    @Test
    @DisplayName("Consulta fora da região do item não o retorna")
    void consultaForaDaRegiao() {
        quadtree.inserir("A", new Rectangle(10, 10, 5, 5));
        assertTrue(consultar(new Rectangle(80, 80, 10, 10)).isEmpty());
    }

    @Test
    @DisplayName("Subdivisão ocorre e itens continuam consultáveis após mais de 8 inserções")
    void subdivisao() {
        for (int i = 0; i < 20; i++) {
            // Posições espalhadas sem sobreposição, forçando subdivisão
            float x = (i % 5) * 20f;
            float y = (i / 5) * 25f;
            quadtree.inserir("item-" + i, new Rectangle(x, y, 5, 5));
        }
        List<String> tudo = consultar(new Rectangle(0, 0, 100, 100));
        assertEquals(20, tudo.size(), "Todos os itens devem permanecer consultáveis após subdividir");
    }

    @Test
    @DisplayName("Consulta em quadrante filho retorna apenas itens daquela área")
    void consultaQuadrante() {
        quadtree.inserir("SW", new Rectangle(5, 5, 5, 5));
        quadtree.inserir("NE", new Rectangle(80, 80, 5, 5));
        List<String> resultado = consultar(new Rectangle(70, 70, 30, 30));
        assertEquals(1, resultado.size());
        assertTrue(resultado.contains("NE"));
    }

    @Test
    @DisplayName("Item na borda de subdivisões é encontrado em consulta que o toca")
    void itemNaBorda() {
        // Cruza a linha x=50 (limite entre quadrantes filhos)
        quadtree.inserir("BORDA", new Rectangle(45, 45, 10, 10));
        assertFalse(consultar(new Rectangle(45, 45, 10, 10)).isEmpty());
    }

    @Test
    @DisplayName("clear() remove todos os itens, inclusive após subdivisão")
    void clearRemoveTudo() {
        for (int i = 0; i < 20; i++) {
            quadtree.inserir("item-" + i, new Rectangle((i % 5) * 20f, (i / 5) * 25f, 5, 5));
        }
        quadtree.clear();
        assertTrue(consultar(new Rectangle(0, 0, 100, 100)).isEmpty());
    }

    @Test
    @DisplayName("Reutilização após clear() funciona corretamente")
    void reutilizacaoAposClear() {
        quadtree.inserir("ANTIGO", new Rectangle(10, 10, 5, 5));
        quadtree.clear();
        quadtree.inserir("NOVO", new Rectangle(10, 10, 5, 5));
        List<String> resultado = consultar(new Rectangle(0, 0, 50, 50));
        assertEquals(List.of("NOVO"), resultado);
    }
}
