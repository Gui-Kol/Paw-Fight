package com.pawfight.game.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Centraliza tudo que envolve gamepad: detecção e hotplug (via gdx-controllers),
 * seleção de um único controle ativo, leitura de botões/eixos com deadzone,
 * detecção de borda, repetição controlada para navegação e captura de entrada
 * para remapeamento. Não há nenhuma referência a APIs específicas de fabricante.
 */
public class GerenciadorGamepad {

    private static final String TAG = "GerenciadorGamepad";

    public static final float DEADZONE_PADRAO = 0.25f;
    /** Limiar mais alto para considerar movimento intencional (captura e troca de controle). */
    private static final float LIMIAR_INTENCAO = 0.5f;
    /** Atraso até a repetição automática começar (segundos segurando). */
    public static final float REPEAT_DELAY = 0.4f;
    /** Intervalo entre repetições depois do atraso inicial. */
    public static final float REPEAT_INTERVAL = 0.15f;

    /** Abstração da fachada estática {@link Controllers} — permite testes sem hardware. */
    public interface ProvedorControles {
        Array<Controller> obterControles();
        void adicionarListener(ControllerListener listener);
        void removerListener(ControllerListener listener);
    }

    private static GerenciadorGamepad instance;

    private final ProvedorControles provedor;
    private final ControllerListener listenerHotplug;

    private Controller controleAtivo;
    private float deadzone = DEADZONE_PADRAO;

    // Estado por entrada observada (cálculo de borda + repetição controlada)
    private final Set<EntradaGamepad> observadas = new HashSet<>();
    private final Map<EntradaGamepad, Boolean> pressionadoAnterior = new HashMap<>();
    private final Map<EntradaGamepad, Float> tempoSegurado = new HashMap<>();
    private final Map<EntradaGamepad, Float> proximaRepeticao = new HashMap<>();
    private final Set<EntradaGamepad> bordas = new HashSet<>();
    private final Set<EntradaGamepad> bordasComRepeticao = new HashSet<>();

    // Captura de entrada para remapeamento
    private boolean capturando;
    private final Set<Integer> botoesNaCaptura = new HashSet<>();
    private final Set<Integer> eixosNaCaptura = new HashSet<>();

    public GerenciadorGamepad(ProvedorControles provedor) {
        this.provedor = provedor;
        this.listenerHotplug = criarListenerHotplug();
        provedor.adicionarListener(listenerHotplug);
        selecionarPrimeiroControle();
    }

    public static void init() {
        instance = new GerenciadorGamepad(provedorPadrao());
        Gdx.app.log(TAG, "Inicializado. Controle ativo: " + instance.getNomeControleAtivo());
    }

    public static GerenciadorGamepad getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GerenciadorGamepad não foi inicializado! Chame GerenciadorGamepad.init() primeiro.");
        }
        return instance;
    }

    // Fachada padrão sobre Controllers, tolerante a falhas do backend (jogo deve rodar sem suporte a controle)
    private static ProvedorControles provedorPadrao() {
        return new ProvedorControles() {
            @Override
            public Array<Controller> obterControles() {
                try {
                    return Controllers.getControllers();
                } catch (Throwable t) {
                    Gdx.app.error(TAG, "Backend de controles indisponível: " + t.getMessage());
                    return new Array<>();
                }
            }

            @Override
            public void adicionarListener(ControllerListener listener) {
                try {
                    Controllers.addListener(listener);
                } catch (Throwable t) {
                    Gdx.app.error(TAG, "Não foi possível registrar listener de controles: " + t.getMessage());
                }
            }

            @Override
            public void removerListener(ControllerListener listener) {
                try {
                    Controllers.removeListener(listener);
                } catch (Throwable t) {
                    Gdx.app.error(TAG, "Não foi possível remover listener de controles: " + t.getMessage());
                }
            }
        };
    }

    private ControllerListener criarListenerHotplug() {
        return new ControllerAdapter() {
            @Override
            public void connected(Controller controller) {
                Gdx.app.log(TAG, "Controle conectado: " + controller.getName());
                if (controleAtivo == null) {
                    trocarControleAtivo(controller);
                }
            }

            @Override
            public void disconnected(Controller controller) {
                Gdx.app.log(TAG, "Controle desconectado: " + controller.getName());
                if (controller == controleAtivo) {
                    controleAtivo = null;
                    limparEstado();
                    selecionarPrimeiroControle();
                }
            }
        };
    }

    private void selecionarPrimeiroControle() {
        if (controleAtivo != null) {
            return;
        }
        Array<Controller> controles = provedor.obterControles();
        if (controles.size > 0) {
            trocarControleAtivo(controles.first());
        }
    }

    private void trocarControleAtivo(Controller novo) {
        controleAtivo = novo;
        limparEstado();
        Gdx.app.log(TAG, "Controle ativo: " + (novo == null ? "nenhum" : novo.getName()));
    }

    private void limparEstado() {
        pressionadoAnterior.clear();
        tempoSegurado.clear();
        proximaRepeticao.clear();
        bordas.clear();
        bordasComRepeticao.clear();
    }

    /** Chamado uma vez por frame, antes de qualquer consulta de input. */
    public void atualizar(float delta) {
        bordas.clear();
        bordasComRepeticao.clear();

        // Hotplug defensivo: valida o controle ativo contra a lista real
        if (controleAtivo != null && !provedor.obterControles().contains(controleAtivo, true)) {
            Gdx.app.log(TAG, "Controle ativo saiu da lista de conectados.");
            controleAtivo = null;
            limparEstado();
        }
        if (controleAtivo == null) {
            selecionarPrimeiroControle();
        } else {
            detectarTrocaPorAtividade();
        }

        // Cópia defensiva: pressionado() pode registrar novas entradas observadas durante o loop
        for (EntradaGamepad entrada : new HashSet<>(observadas)) {
            boolean agora = pressionado(entrada);
            boolean antes = pressionadoAnterior.getOrDefault(entrada, false);
            pressionadoAnterior.put(entrada, agora);

            if (agora && !antes) {
                // Borda de descida: dispara uma vez, sem repetição contínua
                bordas.add(entrada);
                bordasComRepeticao.add(entrada);
                tempoSegurado.put(entrada, 0f);
                proximaRepeticao.put(entrada, REPEAT_DELAY);
            } else if (agora) {
                float segurado = tempoSegurado.getOrDefault(entrada, 0f) + delta;
                tempoSegurado.put(entrada, segurado);
                float proxima = proximaRepeticao.getOrDefault(entrada, REPEAT_DELAY);
                if (segurado >= proxima) {
                    bordasComRepeticao.add(entrada);
                    proximaRepeticao.put(entrada, proxima + REPEAT_INTERVAL);
                }
            } else {
                tempoSegurado.remove(entrada);
                proximaRepeticao.remove(entrada);
            }
        }
    }

    /** Outro controle com atividade intencional assume o lugar do ativo (apenas um ativo por vez). */
    private void detectarTrocaPorAtividade() {
        Array<Controller> controles = provedor.obterControles();
        if (controles.size <= 1) {
            return;
        }
        for (Controller outro : controles) {
            if (outro == controleAtivo) {
                continue;
            }
            if (temAtividade(outro)) {
                trocarControleAtivo(outro);
                return;
            }
        }
    }

    private boolean temAtividade(Controller controller) {
        for (int botao = controller.getMinButtonIndex(); botao <= controller.getMaxButtonIndex(); botao++) {
            if (controller.getButton(botao)) {
                return true;
            }
        }
        for (int eixo = 0; eixo < controller.getAxisCount(); eixo++) {
            if (Math.abs(controller.getAxis(eixo)) >= LIMIAR_INTENCAO) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // Leitura de estado
    // ------------------------------------------------------------------

    /** Nível: true enquanto a entrada estiver pressionada/além da deadzone. */
    public boolean pressionado(EntradaGamepad entrada) {
        if (entrada == null) {
            return false;
        }
        boolean estado = lerNivel(entrada);
        observar(entrada, estado);
        return estado;
    }

    /** Borda: true apenas no frame em que a entrada foi pressionada. */
    public boolean pressionadoAgora(EntradaGamepad entrada) {
        if (entrada == null) {
            return false;
        }
        observar(entrada, lerNivel(entrada));
        return bordas.contains(entrada);
    }

    /** Borda + repetição controlada enquanto segurar (navegação de menus por analógico). */
    public boolean pressionadoComRepeticao(EntradaGamepad entrada) {
        if (entrada == null) {
            return false;
        }
        observar(entrada, lerNivel(entrada));
        return bordasComRepeticao.contains(entrada);
    }

    // Registra a entrada para detecção de borda/repetição; o primeiro estado lido vira a
    // linha de base — algo já segurado quando a leitura começa não dispara uma borda.
    private void observar(EntradaGamepad entrada, boolean estadoAtual) {
        if (observadas.add(entrada)) {
            pressionadoAnterior.put(entrada, estadoAtual);
        }
    }

    private boolean lerNivel(EntradaGamepad entrada) {
        if (controleAtivo == null) {
            return false;
        }
        if (entrada.getTipo() == TipoEntradaGamepad.BOTAO) {
            int indice = resolverIndiceBotao(entrada.getCodigo(), controleAtivo.getMapping());
            return indice >= controleAtivo.getMinButtonIndex()
                && indice <= controleAtivo.getMaxButtonIndex()
                && controleAtivo.getButton(indice);
        }
        int eixo = resolverIndiceEixo(entrada.getCodigo(), controleAtivo.getMapping());
        if (eixo < 0 || eixo >= controleAtivo.getAxisCount()) {
            return false;
        }
        float valor = controleAtivo.getAxis(eixo);
        if (Math.abs(valor) < deadzone) {
            return false;
        }
        return entrada.getTipo() == TipoEntradaGamepad.EIXO_POSITIVO ? valor > 0 : valor < 0;
    }

    // ------------------------------------------------------------------
    // Captura de entrada (remapeamento)
    // ------------------------------------------------------------------

    public void iniciarCaptura() {
        capturando = true;
        botoesNaCaptura.clear();
        eixosNaCaptura.clear();
        if (controleAtivo != null) {
            for (int botao = controleAtivo.getMinButtonIndex(); botao <= controleAtivo.getMaxButtonIndex(); botao++) {
                if (controleAtivo.getButton(botao)) {
                    botoesNaCaptura.add(botao);
                }
            }
            for (int eixo = 0; eixo < controleAtivo.getAxisCount(); eixo++) {
                if (Math.abs(controleAtivo.getAxis(eixo)) >= LIMIAR_INTENCAO) {
                    eixosNaCaptura.add(eixo);
                }
            }
        }
    }

    /** Retorna a primeira entrada nova detectada desde o início da captura (ou null se nada ainda). */
    public EntradaGamepad capturarEntrada() {
        if (!capturando || controleAtivo == null) {
            return null;
        }
        ControllerMapping mapping = controleAtivo.getMapping();
        for (int botao = controleAtivo.getMinButtonIndex(); botao <= controleAtivo.getMaxButtonIndex(); botao++) {
            if (controleAtivo.getButton(botao)) {
                // Ignora botões já segurados no início da captura; só conta nova pressão
                if (botoesNaCaptura.add(botao)) {
                    capturando = false;
                    return EntradaGamepad.botao(nomeLogicoBotao(botao, mapping));
                }
            } else {
                botoesNaCaptura.remove(botao); // soltou → pode ser capturado de novo
            }
        }
        for (int eixo = 0; eixo < controleAtivo.getAxisCount(); eixo++) {
            float valor = controleAtivo.getAxis(eixo);
            if (Math.abs(valor) >= LIMIAR_INTENCAO) {
                if (eixosNaCaptura.add(eixo)) {
                    capturando = false;
                    String nome = nomeLogicoEixo(eixo, mapping);
                    return valor > 0 ? EntradaGamepad.eixoPositivo(nome) : EntradaGamepad.eixoNegativo(nome);
                }
            } else {
                eixosNaCaptura.remove(eixo); // voltou ao centro → pode ser capturado de novo
            }
        }
        return null;
    }

    public void cancelarCaptura() {
        capturando = false;
    }

    public boolean isCapturando() {
        return capturando;
    }

    // ------------------------------------------------------------------
    // Resolução de mappings (lógico ↔ índice físico)
    // ------------------------------------------------------------------

    public static int resolverIndiceBotao(String codigo, ControllerMapping mapping) {
        if (mapping == null || codigo == null) {
            return ControllerMapping.UNDEFINED;
        }
        switch (codigo) {
            case EntradaGamepad.BOTAO_A: return mapping.buttonA;
            case EntradaGamepad.BOTAO_B: return mapping.buttonB;
            case EntradaGamepad.BOTAO_X: return mapping.buttonX;
            case EntradaGamepad.BOTAO_Y: return mapping.buttonY;
            case EntradaGamepad.BOTAO_L1: return mapping.buttonL1;
            case EntradaGamepad.BOTAO_R1: return mapping.buttonR1;
            case EntradaGamepad.BOTAO_L2: return mapping.buttonL2;
            case EntradaGamepad.BOTAO_R2: return mapping.buttonR2;
            case EntradaGamepad.BOTAO_SELECT: return mapping.buttonBack;
            case EntradaGamepad.BOTAO_START: return mapping.buttonStart;
            case EntradaGamepad.BOTAO_DPAD_CIMA: return mapping.buttonDpadUp;
            case EntradaGamepad.BOTAO_DPAD_BAIXO: return mapping.buttonDpadDown;
            case EntradaGamepad.BOTAO_DPAD_ESQUERDA: return mapping.buttonDpadLeft;
            case EntradaGamepad.BOTAO_DPAD_DIREITA: return mapping.buttonDpadRight;
            case EntradaGamepad.BOTAO_ANALOGICO_ESQ: return mapping.buttonLeftStick;
            case EntradaGamepad.BOTAO_ANALOGICO_DIR: return mapping.buttonRightStick;
            default: return parseBruto(codigo, EntradaGamepad.PREFIXO_BOTAO_BRUTO);
        }
    }

    public static int resolverIndiceEixo(String codigo, ControllerMapping mapping) {
        if (mapping == null || codigo == null) {
            return ControllerMapping.UNDEFINED;
        }
        switch (codigo) {
            case EntradaGamepad.EIXO_ANALOGICO_ESQ_X: return mapping.axisLeftX;
            case EntradaGamepad.EIXO_ANALOGICO_ESQ_Y: return mapping.axisLeftY;
            case EntradaGamepad.EIXO_ANALOGICO_DIR_X: return mapping.axisRightX;
            case EntradaGamepad.EIXO_ANALOGICO_DIR_Y: return mapping.axisRightY;
            default: return parseBruto(codigo, EntradaGamepad.PREFIXO_EIXO_BRUTO);
        }
    }

    private static int parseBruto(String codigo, String prefixo) {
        if (codigo.startsWith(prefixo)) {
            try {
                return Integer.parseInt(codigo.substring(prefixo.length()));
            } catch (NumberFormatException e) {
                return ControllerMapping.UNDEFINED;
            }
        }
        return ControllerMapping.UNDEFINED;
    }

    static String nomeLogicoBotao(int indice, ControllerMapping mapping) {
        if (mapping != null) {
            if (indice == mapping.buttonA && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_A;
            if (indice == mapping.buttonB && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_B;
            if (indice == mapping.buttonX && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_X;
            if (indice == mapping.buttonY && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_Y;
            if (indice == mapping.buttonL1 && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_L1;
            if (indice == mapping.buttonR1 && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_R1;
            if (indice == mapping.buttonL2 && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_L2;
            if (indice == mapping.buttonR2 && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_R2;
            if (indice == mapping.buttonBack && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_SELECT;
            if (indice == mapping.buttonStart && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_START;
            if (indice == mapping.buttonDpadUp && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_DPAD_CIMA;
            if (indice == mapping.buttonDpadDown && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_DPAD_BAIXO;
            if (indice == mapping.buttonDpadLeft && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_DPAD_ESQUERDA;
            if (indice == mapping.buttonDpadRight && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_DPAD_DIREITA;
            if (indice == mapping.buttonLeftStick && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_ANALOGICO_ESQ;
            if (indice == mapping.buttonRightStick && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.BOTAO_ANALOGICO_DIR;
        }
        return EntradaGamepad.PREFIXO_BOTAO_BRUTO + indice;
    }

    static String nomeLogicoEixo(int indice, ControllerMapping mapping) {
        if (mapping != null) {
            if (indice == mapping.axisLeftX && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.EIXO_ANALOGICO_ESQ_X;
            if (indice == mapping.axisLeftY && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.EIXO_ANALOGICO_ESQ_Y;
            if (indice == mapping.axisRightX && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.EIXO_ANALOGICO_DIR_X;
            if (indice == mapping.axisRightY && indice != ControllerMapping.UNDEFINED) return EntradaGamepad.EIXO_ANALOGICO_DIR_Y;
        }
        return EntradaGamepad.PREFIXO_EIXO_BRUTO + indice;
    }

    // ------------------------------------------------------------------
    // Consultas de estado do controle
    // ------------------------------------------------------------------

    public boolean isControleConectado() {
        return controleAtivo != null;
    }

    public String getNomeControleAtivo() {
        return controleAtivo == null ? null : controleAtivo.getName();
    }

    public int getQuantidadeControles() {
        return provedor.obterControles().size;
    }

    public float getDeadzone() {
        return deadzone;
    }

    public void setDeadzone(float deadzone) {
        this.deadzone = MathUtils.clamp(deadzone, 0f, 0.95f);
    }

    public void dispose() {
        provedor.removerListener(listenerHotplug);
        limparEstado();
        controleAtivo = null;
    }
}
