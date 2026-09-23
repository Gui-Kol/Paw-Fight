package com.pawfight.game.engine.input;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.pawfight.game.HeadlessGdx;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GamepadBindingsTest {

    private Files arquivosAnteriores;

    @BeforeAll
    static void prepararGdx() {
        HeadlessGdx.ensureGdx();
    }

    @AfterEach
    void restaurarArquivos() {
        Gdx.files = arquivosAnteriores;
    }

    private void apontarSavesPara(File pasta) {
        arquivosAnteriores = Gdx.files;
        Files arquivos = mock(Files.class);
        when(arquivos.local(anyString())).thenAnswer(inv -> new FileHandle(new File(pasta, "gamepad-bindings.json")));
        Gdx.files = arquivos;
    }

    @Test
    void defaultsSeguemAEspecificacao() {
        GamepadBindings bindings = new GamepadBindings();

        // Movimento no analógico esquerdo
        assertEquals(EntradaGamepad.eixoPositivo(EntradaGamepad.EIXO_ANALOGICO_ESQ_X), bindings.getBinding(GameAction.MOVE_RIGHT));
        assertEquals(EntradaGamepad.eixoNegativo(EntradaGamepad.EIXO_ANALOGICO_ESQ_X), bindings.getBinding(GameAction.MOVE_LEFT));
        assertEquals(EntradaGamepad.eixoNegativo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y), bindings.getBinding(GameAction.MOVE_UP));
        assertEquals(EntradaGamepad.eixoPositivo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y), bindings.getBinding(GameAction.MOVE_DOWN));

        // Gameplay
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_X), bindings.getBinding(GameAction.ATTACK_SPECIAL));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_Y), bindings.getBinding(GameAction.ABILITY));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_START), bindings.getBinding(GameAction.PAUSE_TOGGLE));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_ANALOGICO_ESQ), bindings.getBinding(GameAction.DEBUG_TOGGLE));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_ANALOGICO_DIR), bindings.getBinding(GameAction.CHEAT_TOGGLE));

        // Menus: D-Pad navega, A confirma, B volta
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_CIMA), bindings.getBinding(GameAction.MENU_UP));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_BAIXO), bindings.getBinding(GameAction.MENU_DOWN));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_ESQUERDA), bindings.getBinding(GameAction.MENU_LEFT));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_DIREITA), bindings.getBinding(GameAction.MENU_RIGHT));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_A), bindings.getBinding(GameAction.MENU_CONFIRM));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_B), bindings.getBinding(GameAction.MENU_BACK));

        // Zoom em L1/R1
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_R1), bindings.getBinding(GameAction.ZOOM_MAIS));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_L1), bindings.getBinding(GameAction.ZOOM_MENOS));
    }

    @Test
    void telaCheiaNasceSemBindingDeGamepad() {
        GamepadBindings bindings = new GamepadBindings();
        assertNull(bindings.getBinding(GameAction.TELA_CHEIA));
        assertEquals("---", bindings.descricaoBinding(GameAction.TELA_CHEIA));
    }

    @Test
    void alterarBindingSubstituiOAnterior() {
        GamepadBindings bindings = new GamepadBindings();
        EntradaGamepad novo = EntradaGamepad.botao(EntradaGamepad.BOTAO_R1);
        bindings.setBinding(GameAction.ATTACK_SPECIAL, novo);
        assertEquals(novo, bindings.getBinding(GameAction.ATTACK_SPECIAL));
    }

    @Test
    void removerBindingFicaSemAtribuicao() {
        GamepadBindings bindings = new GamepadBindings();
        bindings.setBinding(GameAction.PAUSE_TOGGLE, null);
        assertNull(bindings.getBinding(GameAction.PAUSE_TOGGLE));
    }

    @Test
    void conflitoEResolvidoPorSwap() {
        GamepadBindings bindings = new GamepadBindings();
        // ABILITY (Y) assume o X de ATTACK_SPECIAL → ATTACK_SPECIAL herda Y
        bindings.setBinding(GameAction.ABILITY, EntradaGamepad.botao(EntradaGamepad.BOTAO_X));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_X), bindings.getBinding(GameAction.ABILITY));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_Y), bindings.getBinding(GameAction.ATTACK_SPECIAL));
    }

    @Test
    void swapComBindingNuloLiberaAEntradaAntiga() {
        GamepadBindings bindings = new GamepadBindings();
        bindings.setBinding(GameAction.PAUSE_TOGGLE, null);
        // TELA_CHEIA (sem binding) assume START → PAUSE_TOGGLE recebe o antigo (nulo)
        bindings.setBinding(GameAction.TELA_CHEIA, EntradaGamepad.botao(EntradaGamepad.BOTAO_START));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_START), bindings.getBinding(GameAction.TELA_CHEIA));
        assertNull(bindings.getBinding(GameAction.PAUSE_TOGGLE));
    }

    @Test
    void buscarConflitoIgnoraAPropriaAcao() {
        GamepadBindings bindings = new GamepadBindings();
        EntradaGamepad start = EntradaGamepad.botao(EntradaGamepad.BOTAO_START);
        assertEquals(GameAction.PAUSE_TOGGLE, bindings.buscarConflito(start, GameAction.ABILITY));
        assertNull(bindings.buscarConflito(start, GameAction.PAUSE_TOGGLE));
    }

    @Test
    void resetRestauraTodosOsDefaults() {
        GamepadBindings bindings = new GamepadBindings();
        bindings.setBinding(GameAction.ATTACK_SPECIAL, EntradaGamepad.botao(EntradaGamepad.BOTAO_L1));
        bindings.setBinding(GameAction.PAUSE_TOGGLE, null);
        bindings.resetToDefaults();
        assertEquals(GamepadBindings.getDefaultBinding(GameAction.ATTACK_SPECIAL), bindings.getBinding(GameAction.ATTACK_SPECIAL));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_START), bindings.getBinding(GameAction.PAUSE_TOGGLE));
    }

    @Test
    void serializacaoDesserializacaoPreservaTudo() {
        GamepadBindings original = new GamepadBindings();
        original.setBinding(GameAction.ATTACK_SPECIAL, EntradaGamepad.botaoBruto(9));
        original.setBinding(GameAction.MOVE_UP, EntradaGamepad.eixoPositivo(EntradaGamepad.PREFIXO_EIXO_BRUTO + "2"));
        original.setBinding(GameAction.ZOOM_MAIS, null);

        GamepadBindings restaurado = new GamepadBindings();
        restaurado.carregarDe(original.serializar());

        for (GameAction acao : GameAction.values()) {
            assertEquals(original.getBinding(acao), restaurado.getBinding(acao), "Binding divergente em " + acao);
        }
    }

    @Test
    void carregarIgnoraAcoesDesconhecidas() {
        GamepadBindings bindings = new GamepadBindings();
        bindings.carregarDe("{ \"ACAO_REMOVIDA\": \"BOTAO:X\", \"ATTACK_SPECIAL\": \"BOTAO:B\" }");
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_B), bindings.getBinding(GameAction.ATTACK_SPECIAL));
        // Demais ações mantêm o default
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_Y), bindings.getBinding(GameAction.ABILITY));
    }

    @Test
    void saveEloadUsamArquivoReal(@TempDir File pastaTemp) {
        apontarSavesPara(pastaTemp);

        GamepadBindings gravador = new GamepadBindings();
        gravador.setBinding(GameAction.MENU_CONFIRM, EntradaGamepad.botao(EntradaGamepad.BOTAO_START));
        gravador.setBinding(GameAction.TELA_CHEIA, EntradaGamepad.botao(EntradaGamepad.BOTAO_SELECT));
        gravador.save();

        assertTrue(new File(pastaTemp, "gamepad-bindings.json").exists());

        GamepadBindings leitor = new GamepadBindings();
        leitor.load();
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_START), leitor.getBinding(GameAction.MENU_CONFIRM));
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_SELECT), leitor.getBinding(GameAction.TELA_CHEIA));
    }

    @Test
    void arquivoCorrompidoVoltaAosDefaults(@TempDir File pastaTemp) {
        apontarSavesPara(pastaTemp);
        new FileHandle(new File(pastaTemp, "gamepad-bindings.json")).writeString("{ json quebrado !!!", false);

        GamepadBindings bindings = new GamepadBindings();
        bindings.setBinding(GameAction.ABILITY, null);
        bindings.load();

        assertEquals(GamepadBindings.getDefaultBinding(GameAction.ABILITY), bindings.getBinding(GameAction.ABILITY));
    }

    @Test
    void semArquivoMantemDefaults(@TempDir File pastaTemp) {
        apontarSavesPara(pastaTemp);
        GamepadBindings bindings = new GamepadBindings();
        bindings.setBinding(GameAction.ABILITY, null);
        bindings.load();
        assertNull(bindings.getBinding(GameAction.ABILITY)); // load sem arquivo não toca no estado atual
    }
}
