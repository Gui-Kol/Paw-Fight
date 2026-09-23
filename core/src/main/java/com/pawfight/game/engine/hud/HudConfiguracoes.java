package com.pawfight.game.engine.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.font.FontFactory;
import com.pawfight.game.engine.input.EntradaGamepad;
import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.GamepadBindings;
import com.pawfight.game.engine.input.GerenciadorGamepad;
import com.pawfight.game.engine.input.GerenciadorInput;
import com.pawfight.game.engine.input.KeyBindings;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HudConfiguracoes extends Table {

    // Layout da seção Controles
    private static final int LARGURA_COLUNA_ACAO = 250;
    private static final int LARGURA_COLUNA_BINDING = 170;
    private static final int ALTURA_LINHA_BINDING = 52;
    private static final int ALTURA_LISTA_BINDINGS = 340;
    // Remapeamento
    private static final float TIMEOUT_CAPTURA_SEGUNDOS = 8f;
    private static final int MAIOR_KEYCODE = 255;
    private static final String TEXTO_CAPTURANDO = "...";

    private final GameConfig config = GameConfig.getInstance();
    private final BitmapFont fonte;
    private final Texture textura;
    private final Label.LabelStyle estiloTexto;
    private final TextButton.TextButtonStyle estiloBotao;
    private final CriarBotao criarBotao;
    private final Supplier<WorldTemplate> mundo;
    private final Runnable voltar;
    private final Label musica;
    private final Label efeitos;
    private final Label passos;
    private final TextButton telaCheia;

    // Seção Controles
    private final Label statusControle;
    private final Map<GameAction, TextButton> botoesTeclado = new EnumMap<>(GameAction.class);
    private final Map<GameAction, TextButton> botoesGamepad = new EnumMap<>(GameAction.class);
    private GameAction acaoEmCaptura;
    private boolean capturaGamepad;
    private float capturaRestante;

    public HudConfiguracoes(CriarBotao criarBotao, Supplier<WorldTemplate> mundo, Runnable voltar) {
        this.criarBotao = criarBotao;
        this.mundo = mundo;
        this.voltar = voltar;
        fonte = FontFactory.createCustomFont("fonts/PixelOperator8-Bold.ttf", 28);
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();
        textura = new Texture(pixel);
        pixel.dispose();
        TextureRegionDrawable base = new TextureRegionDrawable(new TextureRegion(textura));
        estiloTexto = new Label.LabelStyle(fonte, Color.WHITE);
        estiloBotao = new TextButton.TextButtonStyle(
            base.tint(Color.valueOf("423e52")), base.tint(Color.valueOf("77667e")), null, fonte);
        estiloBotao.over = base.tint(Color.valueOf("60566c"));

        setFillParent(true);
        Table painel = new Table();
        painel.setBackground(base.tint(Color.valueOf("211e2bed")));
        painel.pad(40);
        painel.defaults().pad(12);
        painel.add(new Label("CONFIGURAÇÕES", estiloTexto)).colspan(4).padBottom(28).row();
        musica = adicionarVolume(painel, "Música", config::getVolumeMusica, valor -> {
            config.setVolumeMusica(valor);
            WorldTemplate atual = mundo.get();
            if (atual != null && atual.getBackMusic() != null) {
                atual.getBackMusic().setVolume(config.getVolumeMusica());
            }
        });
        efeitos = adicionarVolume(painel, "Efeitos", config::getVolumeEfeitos, config::setVolumeEfeitos);
        passos = adicionarVolume(painel, "Passos", config::getVolumePassos, config::setVolumePassos);
        painel.add(new Label("Tela cheia", estiloTexto)).left().width(250);
        telaCheia = botao("", () -> {
            WorldTemplate atual = mundo.get();
            if (atual != null && atual.getGame() != null) {
                atual.getGame().definirTelaCheia(!config.isTelaCheia());
            }
        });
        painel.add(telaCheia).colspan(3).fillX().height(64).row();

        // ============================ CONTROLES ============================
        painel.add(new Label("CONTROLES", estiloTexto)).colspan(4).padTop(10).row();
        statusControle = new Label("", estiloTexto);
        painel.add(statusControle).colspan(4).left().padBottom(2).row();

        Table cabecalho = new Table();
        cabecalho.add(new Label("Ação", estiloTexto)).width(LARGURA_COLUNA_ACAO).left();
        cabecalho.add(new Label("Teclado", estiloTexto)).width(LARGURA_COLUNA_BINDING).center();
        cabecalho.add(new Label("Controle", estiloTexto)).width(LARGURA_COLUNA_BINDING).center();
        painel.add(cabecalho).colspan(4).padBottom(2).row();

        Table lista = new Table();
        for (GameAction acao : GameAction.values()) {
            botoesTeclado.put(acao, botaoBinding(acao, false));
            botoesGamepad.put(acao, botaoBinding(acao, true));
            lista.add(new Label(acao.getLabel(), estiloTexto)).width(LARGURA_COLUNA_ACAO).left();
            lista.add(botoesTeclado.get(acao)).width(LARGURA_COLUNA_BINDING).height(ALTURA_LINHA_BINDING).pad(2);
            lista.add(botoesGamepad.get(acao)).width(LARGURA_COLUNA_BINDING).height(ALTURA_LINHA_BINDING).pad(2);
            lista.row();
        }
        ScrollPane scroll = new ScrollPane(lista);
        scroll.setScrollingDisabled(true, false);
        scroll.setFadeScrollBars(false);
        scroll.setForceScroll(false, true);
        painel.add(scroll).colspan(4).width(LARGURA_COLUNA_ACAO + LARGURA_COLUNA_BINDING * 2 + 24).height(ALTURA_LISTA_BINDINGS).row();

        painel.add(botao("Restaurar padrões", this::restaurarPadroes)).colspan(4).width(360).height(64).padTop(10).row();
        painel.add(botao("Voltar", voltar)).colspan(4).width(260).height(70).padTop(10);
        add(painel);
        atualizarValores();
        definirVisivel(false);
    }

    private Label adicionarVolume(Table painel, String nome, Supplier<Float> obter, Consumer<Float> alterar) {
        Label valor = new Label("", estiloTexto);
        painel.add(new Label(nome, estiloTexto)).left().width(250);
        painel.add(botao("-", () -> alterarVolume(obter, alterar, -5))).size(70, 64);
        painel.add(valor).width(110).center();
        painel.add(botao("+", () -> alterarVolume(obter, alterar, 5))).size(70, 64).row();
        return valor;
    }

    private void alterarVolume(Supplier<Float> obter, Consumer<Float> alterar, int pontos) {
        alterar.accept((Math.round(obter.get() * 100) + pontos) / 100f);
        config.salvarConfiguracoes();
    }

    private TextButton botao(String texto, Runnable acao) {
        TextButton botao = new TextButton(texto, estiloBotao);
        botao.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                acao.run();
                criarBotao.playClickSound();
                atualizarValores();
            }
        });
        return botao;
    }

    // ============================ Seção Controles ============================

    private TextButton botaoBinding(GameAction acao, boolean gamepad) {
        TextButton botao = new TextButton("", estiloBotao);
        botao.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                iniciarCaptura(acao, gamepad);
                criarBotao.playClickSound();
                atualizarValores();
            }
        });
        return botao;
    }

    private void iniciarCaptura(GameAction acao, boolean gamepad) {
        cancelarCaptura();
        acaoEmCaptura = acao;
        capturaGamepad = gamepad;
        capturaRestante = TIMEOUT_CAPTURA_SEGUNDOS;
        if (gamepad) {
            GerenciadorGamepad.getInstance().iniciarCaptura();
        }
    }

    private void cancelarCaptura() {
        acaoEmCaptura = null;
        GerenciadorGamepad.getInstance().cancelarCaptura();
    }

    private void concluirCaptura() {
        cancelarCaptura();
        atualizarValores();
    }

    private void restaurarPadroes() {
        KeyBindings.getInstance().resetToDefaults();
        KeyBindings.getInstance().save();
        GamepadBindings.getInstance().resetToDefaults();
        GamepadBindings.getInstance().save();
        atualizarValores();
    }

    private void processarCaptura(float delta) {
        capturaRestante -= delta;
        if (capturaGamepad) {
            EntradaGamepad entrada = GerenciadorGamepad.getInstance().capturarEntrada();
            if (entrada != null) {
                // Swap automático em caso de conflito + persistência imediata
                GamepadBindings.getInstance().setBinding(acaoEmCaptura, entrada);
                GamepadBindings.getInstance().save();
                concluirCaptura();
                return;
            }
        } else {
            for (int keycode = 0; keycode <= MAIOR_KEYCODE; keycode++) {
                if (!Gdx.input.isKeyJustPressed(keycode)) {
                    continue;
                }
                if (keycode != Input.Keys.ESCAPE) {
                    KeyBindings.getInstance().setKey(acaoEmCaptura, keycode);
                    KeyBindings.getInstance().save();
                }
                concluirCaptura(); // ESC cancela
                return;
            }
        }
        if (capturaRestante <= 0) {
            concluirCaptura();
        }
    }

    private void atualizarValores() {
        musica.setText(Math.round(config.getVolumeMusica() * 100) + "%");
        efeitos.setText(Math.round(config.getVolumeEfeitos() * 100) + "%");
        passos.setText(Math.round(config.getVolumePassos() * 100) + "%");
        telaCheia.setText(config.isTelaCheia() ? "Ligado" : "Desligado");

        GerenciadorGamepad gamepad = GerenciadorGamepad.getInstance();
        statusControle.setText(gamepad.isControleConectado()
            ? "Controle: " + gamepad.getNomeControleAtivo()
            : "Controle: nenhum conectado");

        for (GameAction acao : GameAction.values()) {
            botoesTeclado.get(acao).setText(
                acao == acaoEmCaptura && !capturaGamepad ? TEXTO_CAPTURANDO
                    : KeyBindings.getInstance().getKeyName(acao));
            botoesGamepad.get(acao).setText(
                acao == acaoEmCaptura && capturaGamepad ? TEXTO_CAPTURANDO
                    : GamepadBindings.getInstance().descricaoBinding(acao));
        }
    }

    @Override
    public void act(float delta) {
        if (isVisible()) {
            atualizarValores();
            if (acaoEmCaptura != null) {
                processarCaptura(delta);
            } else if (GerenciadorInput.getInstance().isPressionadaAgora(GameAction.MENU_BACK)) {
                // B (controle) / Backspace (teclado) fecha a tela de configurações
                voltar.run();
            }
        }
        super.act(delta);
    }

    public void definirVisivel(boolean visivel) {
        setVisible(visivel);
        setTouchable(visivel ? Touchable.childrenOnly : Touchable.disabled);
        cancelarCaptura();
        if (visivel) atualizarValores();
    }

    public void dispose() {
        fonte.dispose();
        textura.dispose();
    }
}
