package com.pawfight.game.engine.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.utils.Array;
import com.pawfight.game.HeadlessGdx;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GerenciadorGamepadTest {

    // Índices físicos do mapping de teste (documentados para leitura dos cenários)
    private static final int EIXO_ESQ_X = 0;
    private static final int EIXO_ESQ_Y = 1;
    private static final int BT_X = 2;
    private static final int BT_DPAD_ESQ = 14;
    private static final int TOTAL_BOTOES = 16; // 0..15
    private static final float DELTA_FRAME = 0.1f;

    @BeforeAll
    static void prepararGdx() {
        HeadlessGdx.ensureGdx();
    }

    /** Provedor falso: lista de controles mutável + listeners capturados para simular hotplug. */
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

        void conectar(Controller controle) {
            controles.add(controle);
            listeners.forEach(l -> l.connected(controle));
        }

        void desconectar(Controller controle) {
            controles.removeValue(controle, true);
            listeners.forEach(l -> l.disconnected(controle));
        }
    }

    private static ControllerMapping mappingPadrao() {
        return new ControllerMapping(
            EIXO_ESQ_X, EIXO_ESQ_Y, 2, 3,      // eixos esq. X/Y, dir. X/Y
            0, 1, BT_X, 3,                      // A, B, X, Y
            4, 5,                               // back, start
            6, 7, 8, 9,                         // L1, L2, R1, R2
            10, 11,                             // cliques dos analógicos
            12, 13, BT_DPAD_ESQ, 15) { };       // dpad cima/baixo/esq/dir
    }

    private static Controller controleFake(String nome) {
        Controller controle = mock(Controller.class);
        when(controle.getName()).thenReturn(nome);
        when(controle.getMapping()).thenReturn(mappingPadrao());
        when(controle.getMinButtonIndex()).thenReturn(0);
        when(controle.getMaxButtonIndex()).thenReturn(TOTAL_BOTOES - 1);
        when(controle.getAxisCount()).thenReturn(4);
        return controle;
    }

    @Test
    void semControleNadaEstaAtivo() {
        GerenciadorGamepad gamepad = new GerenciadorGamepad(new ProvedorFake());
        assertFalse(gamepad.isControleConectado());
        assertNull(gamepad.getNomeControleAtivo());
        assertFalse(gamepad.pressionado(EntradaGamepad.botao(EntradaGamepad.BOTAO_A)));
        gamepad.atualizar(DELTA_FRAME); // não deve lançar exceção
    }

    @Test
    void hotplugSelecionaNovoControleQuandoNaoHaAtivo() {
        ProvedorFake provedor = new ProvedorFake();
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);

        provedor.conectar(controleFake("Controle 1"));

        assertTrue(gamepad.isControleConectado());
        assertEquals("Controle 1", gamepad.getNomeControleAtivo());
    }

    @Test
    void deadzoneIgnoraMovimentosPequenos() {
        ProvedorFake provedor = new ProvedorFake();
        Controller controle = controleFake("C1");
        provedor.conectar(controle);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);

        EntradaGamepad cima = EntradaGamepad.eixoNegativo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y);

        when(controle.getAxis(EIXO_ESQ_Y)).thenReturn(-0.1f); // drift dentro da deadzone
        assertFalse(gamepad.pressionado(cima));

        when(controle.getAxis(EIXO_ESQ_Y)).thenReturn(-0.6f);
        assertTrue(gamepad.pressionado(cima));

        // Lado oposto do eixo não ativa esta entrada
        when(controle.getAxis(EIXO_ESQ_Y)).thenReturn(0.9f);
        assertFalse(gamepad.pressionado(cima));
        assertTrue(gamepad.pressionado(EntradaGamepad.eixoPositivo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y)));
        // Exatamente na deadzone (0.25) ainda é considerado fora
        when(controle.getAxis(EIXO_ESQ_Y)).thenReturn(-GerenciadorGamepad.DEADZONE_PADRAO);
        assertTrue(gamepad.pressionado(cima));
    }

    @Test
    void deadzoneCustomizavelEClampada() {
        ProvedorFake provedor = new ProvedorFake();
        Controller controle = controleFake("C1");
        provedor.conectar(controle);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);
        EntradaGamepad direita = EntradaGamepad.eixoPositivo(EntradaGamepad.EIXO_ANALOGICO_ESQ_X);

        when(controle.getAxis(EIXO_ESQ_X)).thenReturn(0.6f);
        assertTrue(gamepad.pressionado(direita));

        gamepad.setDeadzone(0.7f);
        assertFalse(gamepad.pressionado(direita));

        gamepad.setDeadzone(-1f); // clamp para [0, 0.95]
        assertTrue(gamepad.pressionado(direita));
        assertEquals(0f, gamepad.getDeadzone());
    }

    @Test
    void justPressedDisparaUmaUnicaVezPorPressionamento() {
        ProvedorFake provedor = new ProvedorFake();
        Controller controle = controleFake("C1");
        provedor.conectar(controle);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);
        EntradaGamepad ataque = EntradaGamepad.botao(EntradaGamepad.BOTAO_X);

        // Registro com o botão solto: sem borda e com linha de base "não pressionado"
        assertFalse(gamepad.pressionadoAgora(ataque));

        when(controle.getButton(BT_X)).thenReturn(true);

        gamepad.atualizar(DELTA_FRAME);
        assertTrue(gamepad.pressionadoAgora(ataque), "Borda no primeiro frame");

        gamepad.atualizar(DELTA_FRAME);
        gamepad.atualizar(DELTA_FRAME);
        assertFalse(gamepad.pressionadoAgora(ataque), "Segurar não repete a borda");

        when(controle.getButton(BT_X)).thenReturn(false);
        gamepad.atualizar(DELTA_FRAME);
        when(controle.getButton(BT_X)).thenReturn(true);
        gamepad.atualizar(DELTA_FRAME);
        assertTrue(gamepad.pressionadoAgora(ataque), "Novo pressionamento gera nova borda");
    }

    @Test
    void repeticaoControladaParaNavegacao() {
        ProvedorFake provedor = new ProvedorFake();
        Controller controle = controleFake("C1");
        provedor.conectar(controle);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);
        EntradaGamepad esquerda = EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_ESQUERDA);
        gamepad.pressionado(esquerda); // registra solto — a contagem do delay começa do aperto

        when(controle.getButton(BT_DPAD_ESQ)).thenReturn(true);

        int bordas = 0;
        int repeticoes = 0;
        boolean repetiuAntesDoDelay = false;
        for (int frame = 1; frame <= 10; frame++) { // 1 segundo segurando (10 x 0.1s)
            gamepad.atualizar(DELTA_FRAME);
            if (gamepad.pressionadoAgora(esquerda)) bordas++;
            if (gamepad.pressionadoComRepeticao(esquerda)) {
                repeticoes++;
                // Frames 2 e 3 (0.2s e 0.3s) estão antes do delay de repetição (0.4s)
                if (frame == 2 || frame == 3) repetiuAntesDoDelay = true;
            }
        }

        assertEquals(1, bordas, "justPressed conta a borda uma única vez");
        assertFalse(repetiuAntesDoDelay, "Sem repetição antes do atraso inicial");
        assertTrue(repeticoes > 1, "Segurar emite repetições controladas");
    }

    @Test
    void apenasUmControleAtivoPorVezEAtividadeTrocaSelecao() {
        ProvedorFake provedor = new ProvedorFake();
        Controller primeiro = controleFake("Primeiro");
        Controller segundo = controleFake("Segundo");
        provedor.conectar(primeiro);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);
        provedor.conectar(segundo);

        assertEquals("Primeiro", gamepad.getNomeControleAtivo());

        when(segundo.getButton(BT_X)).thenReturn(true); // atividade no segundo controle
        gamepad.atualizar(DELTA_FRAME);

        assertEquals("Segundo", gamepad.getNomeControleAtivo());
    }

    @Test
    void desconexaoDoAtivoCaiParaOProximoDisponivel() {
        ProvedorFake provedor = new ProvedorFake();
        Controller primeiro = controleFake("Primeiro");
        Controller segundo = controleFake("Segundo");
        provedor.conectar(primeiro);
        provedor.conectar(segundo);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);
        assertEquals("Primeiro", gamepad.getNomeControleAtivo());

        provedor.desconectar(primeiro);
        assertEquals("Segundo", gamepad.getNomeControleAtivo());

        provedor.desconectar(segundo);
        assertFalse(gamepad.isControleConectado());
        assertNull(gamepad.getNomeControleAtivo());
        assertFalse(gamepad.pressionado(EntradaGamepad.botao(EntradaGamepad.BOTAO_A)));
    }

    @Test
    void capturaRetornaEntradaLogicaQuandoBotaoPressionado() {
        ProvedorFake provedor = new ProvedorFake();
        Controller controle = controleFake("C1");
        provedor.conectar(controle);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);

        gamepad.iniciarCaptura();
        assertNull(gamepad.capturarEntrada(), "Nada pressionado ainda");

        when(controle.getButton(BT_X)).thenReturn(true);
        EntradaGamepad capturada = gamepad.capturarEntrada();
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_X), capturada);
        assertNull(gamepad.capturarEntrada(), "Captura encerra após a primeira entrada");
    }

    @Test
    void capturaRetornaEixoComSinal() {
        ProvedorFake provedor = new ProvedorFake();
        Controller controle = controleFake("C1");
        provedor.conectar(controle);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);

        gamepad.iniciarCaptura();
        when(controle.getAxis(EIXO_ESQ_Y)).thenReturn(-0.8f);

        assertEquals(EntradaGamepad.eixoNegativo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y), gamepad.capturarEntrada());
    }

    @Test
    void capturaUsaCodigoBrutoQuandoBotaoNaoExisteNoMapping() {
        ProvedorFake provedor = new ProvedorFake();
        Controller controle = controleFake("C1");
        int botaoExotico = TOTAL_BOTOES - 1; // 15 é dpad-direita no mapping; usar além do mapeado
        when(controle.getMaxButtonIndex()).thenReturn(TOTAL_BOTOES + 3); // 0..18
        provedor.conectar(controle);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);

        gamepad.iniciarCaptura();
        when(controle.getButton(botaoExotico + 3)).thenReturn(true); // índice 18 sem equivalente lógico

        assertEquals(EntradaGamepad.botaoBruto(18), gamepad.capturarEntrada());
    }

    @Test
    void capturaIgnoraBotoesJaPressionadosNoInicio() {
        ProvedorFake provedor = new ProvedorFake();
        Controller controle = controleFake("C1");
        when(controle.getButton(1)).thenReturn(true); // já segurado antes do remap
        provedor.conectar(controle);
        GerenciadorGamepad gamepad = new GerenciadorGamepad(provedor);

        gamepad.iniciarCaptura();
        assertNull(gamepad.capturarEntrada(), "Botão já segurado não conta como nova entrada");

        when(controle.getButton(1)).thenReturn(false);
        when(controle.getButton(BT_X)).thenReturn(true);
        assertEquals(EntradaGamepad.botao(EntradaGamepad.BOTAO_X), gamepad.capturarEntrada());
    }

    @Test
    void resolucaoSemanticaDeIndicesViaMapping() {
        ControllerMapping mapping = mappingPadrao();
        assertEquals(5, GerenciadorGamepad.resolverIndiceBotao(EntradaGamepad.BOTAO_START, mapping));
        assertEquals(10, GerenciadorGamepad.resolverIndiceBotao(EntradaGamepad.BOTAO_ANALOGICO_ESQ, mapping));
        assertEquals(42, GerenciadorGamepad.resolverIndiceBotao("BOTAO_42", mapping));
        assertEquals(1, GerenciadorGamepad.resolverIndiceEixo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y, mapping));
        assertEquals(3, GerenciadorGamepad.resolverIndiceEixo("EIXO_3", mapping));
        assertEquals(ControllerMapping.UNDEFINED, GerenciadorGamepad.resolverIndiceBotao("DESCONHECIDO", mapping));
    }
}
