package com.pawfight.game.engine.input;

import com.badlogic.gdx.Gdx;

/**
 * Fachada única de consulta de input. Combina teclado ({@link KeyBindings}) e
 * gamepad ({@link GerenciadorGamepad} + {@link GamepadBindings}) sem exigir que
 * o jogador escolha um "modo": qualquer dispositivo responde a qualquer momento.
 */
public class GerenciadorInput {

    private static GerenciadorInput instance;

    private final KeyBindings teclado;
    private final GerenciadorGamepad gamepad;
    private final GamepadBindings gamepadBindings;

    public GerenciadorInput(KeyBindings teclado, GerenciadorGamepad gamepad, GamepadBindings gamepadBindings) {
        this.teclado = teclado;
        this.gamepad = gamepad;
        this.gamepadBindings = gamepadBindings;
    }

    public static void init() {
        instance = new GerenciadorInput(
            KeyBindings.getInstance(),
            GerenciadorGamepad.getInstance(),
            GamepadBindings.getInstance());
        Gdx.app.log("GerenciadorInput", "Inicializado (teclado + gamepad combinados).");
    }

    public static GerenciadorInput getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GerenciadorInput não foi inicializado! Chame GerenciadorInput.init() primeiro.");
        }
        return instance;
    }

    /** Atualiza o estado do gamepad (bordas, repetição, hotplug). Chamar uma vez por frame. */
    public void atualizar(float delta) {
        gamepad.atualizar(delta);
    }

    /**
     * Ação ativa agora. Para ações de borda (justPressed) equivale a {@link #isPressionadaAgora};
     * para ações contínuas (movimento) retorna true enquanto a entrada estiver pressionada.
     */
    public boolean isAtiva(GameAction action) {
        if (action.isJustPressed()) {
            return isPressionadaAgora(action);
        }
        return teclado.isActive(action) || gamepad.pressionado(bindingGamepad(action));
    }

    /** Borda: true apenas no frame em que a ação foi acionada (evita repetição contínua). */
    public boolean isPressionadaAgora(GameAction action) {
        boolean tecladoBorda = Gdx.input.isKeyJustPressed(teclado.getKey(action));
        return tecladoBorda || gamepad.pressionadoAgora(bindingGamepad(action));
    }

    /** Borda + repetição controlada ao segurar (navegação de menus por analógico/D-Pad). */
    public boolean isAtivaComRepeticao(GameAction action) {
        return isPressionadaAgora(action) || gamepad.pressionadoComRepeticao(bindingGamepad(action));
    }

    private EntradaGamepad bindingGamepad(GameAction action) {
        return gamepadBindings.getBinding(action);
    }

    public KeyBindings getTeclado() {
        return teclado;
    }

    public GerenciadorGamepad getGamepad() {
        return gamepad;
    }

    public GamepadBindings getGamepadBindings() {
        return gamepadBindings;
    }
}
