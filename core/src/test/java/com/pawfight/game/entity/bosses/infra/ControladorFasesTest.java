package com.pawfight.game.entity.bosses.infra;

import com.pawfight.game.entity.bosses.BossTemplate;
import com.pawfight.game.entity.bosses.FaseBoss;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ControladorFases")
class ControladorFasesTest {

    private static FaseBoss fase(String nome, float limiar) {
        return new FaseBoss(nome, limiar) {
            @Override public void executarAtaqueNormal(BossTemplate boss) { }
            @Override public void executarAtaqueEspecial(BossTemplate boss) { }
        };
    }

    private static List<FaseBoss> tresFases() {
        List<FaseBoss> fases = new ArrayList<>();
        fases.add(fase("Inicial", 0.65f));
        fases.add(fase("Intermediária", 0.30f));
        fases.add(fase("Final", 0f));
        return fases;
    }

    @Test
    @DisplayName("Vida acima do limiar não dispara transição")
    void semTransicaoAcimaDoLimiar() {
        ControladorFases controlador = new ControladorFases(tresFases());
        List<Integer> transicoes = new ArrayList<>();

        controlador.sincronizar(0, 66, 100, transicoes::add);

        assertTrue(transicoes.isEmpty());
    }

    @Test
    @DisplayName("Vida no limiar dispara exatamente uma transição")
    void umaTransicaoNoLimiar() {
        ControladorFases controlador = new ControladorFases(tresFases());
        List<Integer> transicoes = new ArrayList<>();

        controlador.sincronizar(0, 65, 100, transicoes::add);

        assertEquals(List.of(1), transicoes);
    }

    @Test
    @DisplayName("Dano que cruza múltiplos limiares notifica cada transição em ordem no mesmo frame")
    void multiplosLimiaresNoMesmoFrame() {
        ControladorFases controlador = new ControladorFases(tresFases());
        List<Integer> transicoes = new ArrayList<>();

        controlador.sincronizar(0, 10, 100, transicoes::add);

        assertEquals(List.of(1, 2), transicoes);
    }

    @Test
    @DisplayName("Última fase nunca transiciona, mesmo com vida zerada")
    void ultimaFaseNaoTransiciona() {
        ControladorFases controlador = new ControladorFases(tresFases());
        List<Integer> transicoes = new ArrayList<>();

        controlador.sincronizar(2, 0, 100, transicoes::add);

        assertTrue(transicoes.isEmpty());
    }
}
