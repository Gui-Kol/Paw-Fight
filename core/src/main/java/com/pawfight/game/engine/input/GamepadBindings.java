package com.pawfight.game.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;

import java.util.EnumMap;
import java.util.Map;

/**
 * Bindings de gamepad por ação ({@link GameAction}), persistidos de forma semântica
 * (botão lógico / eixo positivo-negativo / ausência) em save/gamepad-bindings.json.
 * Independente dos saves de teclado ({@link KeyBindings}).
 */
public class GamepadBindings {

    private static final String SAVE_PATH = "save/gamepad-bindings.json";

    private static GamepadBindings instance;

    private final EnumMap<GameAction, EntradaGamepad> bindings = new EnumMap<>(GameAction.class);

    // package-private para instâncias independentes em testes (singleton segue via init/getInstance)
    GamepadBindings() {
        resetToDefaults();
    }

    public static void init() {
        instance = new GamepadBindings();
        instance.load();
        Gdx.app.log("GamepadBindings", "Inicializado com " + instance.bindings.size() + " ações.");
    }

    public static GamepadBindings getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GamepadBindings não foi inicializado! Chame GamepadBindings.init() primeiro.");
        }
        return instance;
    }

    public EntradaGamepad getBinding(GameAction action) {
        return bindings.getOrDefault(action, getDefaultBinding(action));
    }

    public String descricaoBinding(GameAction action) {
        EntradaGamepad entrada = getBinding(action);
        return entrada == null ? "---" : entrada.descricao();
    }

    /**
     * Atribui uma entrada (ou null para limpar). Conflitos são resolvidos por swap:
     * a ação que já usava essa entrada recebe o binding antigo desta ação.
     */
    public void setBinding(GameAction action, EntradaGamepad novaEntrada) {
        if (novaEntrada != null) {
            GameAction conflito = buscarConflito(novaEntrada, action);
            if (conflito != null) {
                EntradaGamepad antigo = bindings.get(action);
                bindings.put(conflito, antigo);
                Gdx.app.log("GamepadBindings", "Swap: " + conflito.getLabel() + " ← entrada antiga de " + action.getLabel());
            }
        }
        bindings.put(action, novaEntrada);
        Gdx.app.log("GamepadBindings", action.getLabel() + " → " + (novaEntrada == null ? "---" : novaEntrada.descricao()));
    }

    public GameAction buscarConflito(EntradaGamepad entrada, GameAction ignorar) {
        for (Map.Entry<GameAction, EntradaGamepad> entry : bindings.entrySet()) {
            if (entry.getKey() != ignorar && entrada.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void resetToDefaults() {
        bindings.clear();
        bindings.putAll(criarDefaults());
    }

    public Map<GameAction, EntradaGamepad> getAllBindings() {
        EnumMap<GameAction, EntradaGamepad> copia = new EnumMap<>(GameAction.class);
        copia.putAll(bindings);
        return copia;
    }

    public static EntradaGamepad getDefaultBinding(GameAction action) {
        return criarDefaults().get(action);
    }

    /** Defaults: movimento no analógico esquerdo, menus no D-Pad, A confirma, B volta, etc. */
    public static Map<GameAction, EntradaGamepad> criarDefaults() {
        EnumMap<GameAction, EntradaGamepad> padrao = new EnumMap<>(GameAction.class);
        padrao.put(GameAction.MOVE_RIGHT, EntradaGamepad.eixoPositivo(EntradaGamepad.EIXO_ANALOGICO_ESQ_X));
        padrao.put(GameAction.MOVE_LEFT, EntradaGamepad.eixoNegativo(EntradaGamepad.EIXO_ANALOGICO_ESQ_X));
        padrao.put(GameAction.MOVE_UP, EntradaGamepad.eixoNegativo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y));
        padrao.put(GameAction.MOVE_DOWN, EntradaGamepad.eixoPositivo(EntradaGamepad.EIXO_ANALOGICO_ESQ_Y));

        padrao.put(GameAction.ATTACK_SPECIAL, EntradaGamepad.botao(EntradaGamepad.BOTAO_X));
        padrao.put(GameAction.ABILITY, EntradaGamepad.botao(EntradaGamepad.BOTAO_Y));

        padrao.put(GameAction.PAUSE_TOGGLE, EntradaGamepad.botao(EntradaGamepad.BOTAO_START));
        padrao.put(GameAction.DEBUG_TOGGLE, EntradaGamepad.botao(EntradaGamepad.BOTAO_ANALOGICO_ESQ));
        padrao.put(GameAction.CHEAT_TOGGLE, EntradaGamepad.botao(EntradaGamepad.BOTAO_ANALOGICO_DIR));

        padrao.put(GameAction.MENU_UP, EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_CIMA));
        padrao.put(GameAction.MENU_DOWN, EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_BAIXO));
        padrao.put(GameAction.MENU_RIGHT, EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_DIREITA));
        padrao.put(GameAction.MENU_LEFT, EntradaGamepad.botao(EntradaGamepad.BOTAO_DPAD_ESQUERDA));
        padrao.put(GameAction.MENU_CONFIRM, EntradaGamepad.botao(EntradaGamepad.BOTAO_A));
        padrao.put(GameAction.MENU_BACK, EntradaGamepad.botao(EntradaGamepad.BOTAO_B));

        padrao.put(GameAction.TELA_CHEIA, null);

        padrao.put(GameAction.ZOOM_MAIS, EntradaGamepad.botao(EntradaGamepad.BOTAO_R1));
        padrao.put(GameAction.ZOOM_MENOS, EntradaGamepad.botao(EntradaGamepad.BOTAO_L1));
        return padrao;
    }

    public void save() {
        try {
            FileHandle file = Gdx.files.local(SAVE_PATH);
            file.writeString(serializar(), false);
            Gdx.app.log("GamepadBindings", "Bindings salvos em " + SAVE_PATH);
        } catch (Exception e) {
            Gdx.app.error("GamepadBindings", "Erro ao salvar bindings: " + e.getMessage(), e);
        }
    }

    public void load() {
        try {
            FileHandle file = Gdx.files.local(SAVE_PATH);
            if (!file.exists()) {
                Gdx.app.log("GamepadBindings", "Nenhum arquivo de bindings encontrado. Usando defaults.");
                return;
            }
            carregarDe(file.readString());
            Gdx.app.log("GamepadBindings", "Bindings carregados de " + SAVE_PATH);
        } catch (Exception e) {
            Gdx.app.error("GamepadBindings", "Erro ao carregar bindings: " + e.getMessage(), e);
            resetToDefaults();
        }
    }

    /** Serializa todos os bindings (inclusive nulos) em JSON. Separado para testes. */
    String serializar() {
        OrderedMap<String, String> map = new OrderedMap<>();
        for (GameAction action : GameAction.values()) {
            EntradaGamepad entrada = bindings.get(action);
            map.put(action.name(), entrada == null ? null : entrada.serializar());
        }
        // Tipos informados explicitamente para valores saírem como texto puro (sem metadados de classe)
        return new Json().toJson(map, OrderedMap.class, String.class);
    }

    /** Restaura bindings a partir de JSON; ações desconhecidas são ignoradas. */
    void carregarDe(String conteudo) {
        JsonValue root = new JsonReader().parse(conteudo);
        for (JsonValue entry = root.child; entry != null; entry = entry.next) {
            try {
                GameAction action = GameAction.valueOf(entry.name);
                bindings.put(action, entry.isNull() ? null : EntradaGamepad.desserializar(entry.asString()));
            } catch (IllegalArgumentException e) {
                Gdx.app.log("GamepadBindings", "Ação desconhecida no save: " + entry.name + " (ignorada)");
            }
        }
    }
}
