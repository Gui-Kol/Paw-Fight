package com.pawfight.game.engine.input;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.pawfight.game.HeadlessGdx;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Testa a fachada combinada (teclado + gamepad simultâneos) sem hardware. */
class GerenciadorInputTest {

    private static final int EIXO_ESQ_X = 0;
    private static final int BT_X = 2;
    private static final int BT_START = 5;
    private static final float DELTA_FRAME = 0.1f;

    private static Files arquivosAnteriores;
    private static Input inputAnterior;

    private static class ProvedorFake implements GerenciadorGamepad.ProvedorControles {
        final Array<Controller> controles = new Array<>();
        final List<ControllerListener> listeners = new CopyOnWriteArrayList<>();

        @Override
        public Array<Controller> obterControles() {
            return controles;
        }

        @Override
        public void adicionarListener(ControllerListener listener) {
            listeners.add(listener);
        }

        @Override
        public void removerListener(ControllerListener listener) {
            listeners.remove(listener);
        }
    }

    private static ControllerMapping mappingPadrao() {
        return new ControllerMapping(
            EIXO_ESQ_X, 1, 2, 3,
            0, 1, BT_X, 3,
            4, BT_START,
            6, 7, 8, 9,
            10, 11,
            12, 13, 14, 15) { };
    }

    private static Controller controleFake() {
        Controller controle = mock(Controller.class);
        when(controle.getName()).thenReturn("Controle Teste");
        when(controle.getMapping()).thenReturn(mappingPadrao());
        when(controle.getMinButtonIndex()).thenReturn(0);
        when(controle.getMaxButtonIndex()).thenReturn(15);
        when(controle.getAxisCount()).thenReturn(4);
        return controle;
    }

    private static File pastaTemporaria;

    @BeforeAll
    static void prepararAmbiente() throws Exception {
        HeadlessGdx.ensureGdx();
        arquivosAnteriores = Gdx.files;
        inputAnterior = Gdx.input;

        // KeyBindings.init() exige Gdx.files; aponta para pasta temporária → defaults limpos
        pastaTemporaria = java.nio.file.Files.createTempDirectory("pawfight-test-input").toFile();
        Files arquivos = mock(Files.class);
        when(arquivos.local(anyString())).thenAnswer(inv ->
            new FileHandle(new File(pastaTemporaria, new File((String) inv.getArgument(0)).getName())));
        Gdx.files = arquivos;
        KeyBindings.init();
    }

    @AfterAll
    static void restaurarAmbiente() {
        Gdx.files = arquivosAnteriores;
        Gdx.input = inputAnterior;
        if (pastaTemporaria != null) {
            File[] arquivos = pastaTemporaria.listFiles();
            if (arquivos != null) {
                for (File arquivo : arquivos) arquivo.delete();
            }
            pastaTemporaria.delete();
        }
    }

    /** Teclado novo por teste: evita vazamento de stubs entre cenários. */
    private static Input novoTeclado() {
        Input teclado = mock(Input.class);
        Gdx.input = teclado;
        return teclado;
    }

    private GerenciadorInput novaFachada(Controller controle) {
        ProvedorFake provedor = new ProvedorFake();
        if (controle != null) {
            provedor.controles.add(controle);
        }
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);
        return new GerenciadorInput(KeyBindings.getInstance(), gamepad, new GamepadBindings());
    }

    @Test
    void semControleTecladoFuncionaNormalmente() {
        Input teclado = novoTeclado();
        GerenciadorInput input = novaFachada(null);

        when(teclado.isKeyPressed(Input.Keys.D)).thenReturn(true);
        assertTrue(input.isAtiva(GameAction.MOVE_RIGHT));

        when(teclado.isKeyJustPressed(Input.Keys.ENTER)).thenReturn(true);
        assertTrue(input.isPressionadaAgora(GameAction.MENU_CONFIRM));
    }

    @Test
    void tecladoEGamepadOperamSimultaneamente() {
        Input teclado = novoTeclado();
        Controller controle = controleFake();
        GerenciadorInput input = novaFachada(controle);

        when(controle.getAxis(EIXO_ESQ_X)).thenReturn(0.8f); // analógico esquerdo → MOVE_RIGHT
        assertTrue(input.isAtiva(GameAction.MOVE_RIGHT), "Gamepad move sem teclado");

        when(teclado.isKeyPressed(Input.Keys.A)).thenReturn(true);
        assertTrue(input.isAtiva(GameAction.MOVE_LEFT), "Teclado move com gamepad conectado");
    }

    @Test
    void acaoDeBordaNoGamepadNaoRepete() {
        Controller controle = controleFake();
        GerenciadorInput input = novaFachada(controle);

        // Primeira consulta acontece com o botão solto (como no loop real do jogo)
        assertFalse(input.isPressionadaAgora(GameAction.PAUSE_TOGGLE));

        when(controle.getButton(BT_START)).thenReturn(true); // PAUSE_TOGGLE
        input.atualizar(DELTA_FRAME);
        assertTrue(input.isPressionadaAgora(GameAction.PAUSE_TOGGLE));

        input.atualizar(DELTA_FRAME);
        assertFalse(input.isPressionadaAgora(GameAction.PAUSE_TOGGLE), "Segurar não pausa/despausa em loop");
    }

    @Test
    void tecladoDisparaBordaDeAcaoSemBindingDeGamepad() {
        Input teclado = novoTeclado();
        GerenciadorInput input = novaFachada(null); // TELA_CHEIA não tem binding de controle por padrão
        when(teclado.isKeyJustPressed(Input.Keys.F11)).thenReturn(true);
        assertTrue(input.isPressionadaAgora(GameAction.TELA_CHEIA));
    }

    @Test
    void remapeamentoSurteEfeitoImediato() {
        Controller controle = controleFake();
        GamepadBindings bindings = new GamepadBindings();
        ProvedorFake provedor = new ProvedorFake();
        provedor.controles.add(controle);
        GerenciadorInput input = new GerenciadorInput(KeyBindings.getInstance(), new GerenciadorGamepad(provedor), bindings);

        // Ataque especial sai do X e vai para o L1 (índice 6 no mapping de teste)
        bindings.setBinding(GameAction.ATTACK_SPECIAL, EntradaGamepad.botao(EntradaGamepad.BOTAO_L1));
        bindings.save(); // persistência — save/restore coberto em GamepadBindingsTest

        // Consulta com o botão solto registra a entrada; o aperto posterior vira borda
        assertFalse(input.isPressionadaAgora(GameAction.ATTACK_SPECIAL));
        when(controle.getButton(6)).thenReturn(true);
        input.atualizar(DELTA_FRAME);
        assertTrue(input.isPressionadaAgora(GameAction.ATTACK_SPECIAL));
    }

    @Test
    void navegacaoComRepeticaoNaoMudaComportamentoDoTeclado() {
        Input teclado = novoTeclado();
        GerenciadorInput input = novaFachada(null);
        when(teclado.isKeyJustPressed(Input.Keys.LEFT)).thenReturn(true);
        assertTrue(input.isAtivaComRepeticao(GameAction.MENU_LEFT));
    }
}
