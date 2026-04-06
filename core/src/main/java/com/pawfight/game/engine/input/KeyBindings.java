package com.pawfight.game.engine.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.OrderedMap;

import java.util.EnumMap;
import java.util.Map;

public class KeyBindings {

    private static final String SAVE_PATH = "save/keybindings.json";

    private static KeyBindings instance;

    private final EnumMap<GameAction, Integer> bindings = new EnumMap<>(GameAction.class);

    // ── Singleton ─────────────────────────────────────────────

    private KeyBindings() {
        resetToDefaults();
    }

    public static void init() {
        instance = new KeyBindings();
        instance.load();
        Gdx.app.log("KeyBindings", "Inicializado com " + instance.bindings.size() + " ações mapeadas.");
    }

    public static KeyBindings getInstance() {
        if (instance == null) {
            throw new IllegalStateException("KeyBindings não foi inicializado! Chame KeyBindings.init() primeiro.");
        }
        return instance;
    }

    // ── API Principal ─────────────────────────────────────────
    public boolean isActive(GameAction action) {
        int key = bindings.getOrDefault(action, action.getDefaultKey());
        return action.isJustPressed()
            ? Gdx.input.isKeyJustPressed(key)
            : Gdx.input.isKeyPressed(key);
    }

    public int getKey(GameAction action) {
        return bindings.getOrDefault(action, action.getDefaultKey());
    }

    public String getKeyName(GameAction action) {
        return Input.Keys.toString(getKey(action));
    }

    public void setKey(GameAction action, int newKeyCode) {
        // Procura se outra ação já usa essa tecla
        GameAction conflict = null;
        for (Map.Entry<GameAction, Integer> entry : bindings.entrySet()) {
            if (entry.getValue() == newKeyCode && entry.getKey() != action) {
                conflict = entry.getKey();
                break;
            }
        }

        // Swap: a ação conflitante recebe a tecla antiga desta ação
        if (conflict != null) {
            int oldKey = bindings.get(action);
            bindings.put(conflict, oldKey);
            Gdx.app.log("KeyBindings", "Swap: " + conflict.getLabel() + " → " + Input.Keys.toString(oldKey));
        }

        bindings.put(action, newKeyCode);
        Gdx.app.log("KeyBindings", action.getLabel() + " → " + Input.Keys.toString(newKeyCode));
    }

    /** Restaura todas as teclas para os valores padrão. */
    public void resetToDefaults() {
        bindings.clear();
        for (GameAction action : GameAction.values()) {
            bindings.put(action, action.getDefaultKey());
        }
    }

    public Map<GameAction, Integer> getAllBindings() {
        return Map.copyOf(bindings);
    }

    // ── Persistência ──────────────────────────────────────────
    public void save() {
        try {
            Json json = new Json();
            // Converte para OrderedMap<String, Integer> para serialização limpa
            OrderedMap<String, Integer> map = new OrderedMap<>();
            for (Map.Entry<GameAction, Integer> entry : bindings.entrySet()) {
                map.put(entry.getKey().name(), entry.getValue());
            }

            FileHandle file = Gdx.files.local(SAVE_PATH);
            file.writeString(json.prettyPrint(map), false);
            Gdx.app.log("KeyBindings", "Bindings salvos em " + SAVE_PATH);
        } catch (Exception e) {
            Gdx.app.error("KeyBindings", "Erro ao salvar bindings: " + e.getMessage(), e);
        }
    }

    public void load() {
        try {
            FileHandle file = Gdx.files.local(SAVE_PATH);
            if (!file.exists()) {
                Gdx.app.log("KeyBindings", "Nenhum arquivo de bindings encontrado. Usando defaults.");
                return;
            }

            JsonValue root = new com.badlogic.gdx.utils.JsonReader().parse(file.readString());

            for (JsonValue entry = root.child; entry != null; entry = entry.next) {
                try {
                    GameAction action = GameAction.valueOf(entry.name);
                    int keyCode = entry.asInt();
                    bindings.put(action, keyCode);
                } catch (IllegalArgumentException e) {
                    // Ação removida ou renomeada — ignora
                    Gdx.app.log("KeyBindings", "Ação desconhecida no save: " + entry.name + " (ignorada)");
                }
            }

            Gdx.app.log("KeyBindings", "Bindings carregados de " + SAVE_PATH);
        } catch (Exception e) {
            Gdx.app.error("KeyBindings", "Erro ao carregar bindings: " + e.getMessage(), e);
            resetToDefaults();
        }
    }
}



