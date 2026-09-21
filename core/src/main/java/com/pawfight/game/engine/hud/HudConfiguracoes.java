package com.pawfight.game.engine.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pawfight.game.engine.GameConfig;
import com.pawfight.game.engine.font.FontFactory;
import com.pawfight.game.world.template.WorldTemplate;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class HudConfiguracoes extends Table {
    private final GameConfig config = GameConfig.getInstance();
    private final BitmapFont fonte;
    private final Texture textura;
    private final Label.LabelStyle estiloTexto;
    private final TextButton.TextButtonStyle estiloBotao;
    private final CriarBotao criarBotao;
    private final Supplier<WorldTemplate> mundo;
    private final Label musica;
    private final Label efeitos;
    private final Label passos;
    private final TextButton telaCheia;

    public HudConfiguracoes(CriarBotao criarBotao, Supplier<WorldTemplate> mundo, Runnable voltar) {
        this.criarBotao = criarBotao;
        this.mundo = mundo;
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
        painel.add(botao("Voltar", voltar)).colspan(4).width(260).height(70).padTop(28);
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

    private void atualizarValores() {
        musica.setText(Math.round(config.getVolumeMusica() * 100) + "%");
        efeitos.setText(Math.round(config.getVolumeEfeitos() * 100) + "%");
        passos.setText(Math.round(config.getVolumePassos() * 100) + "%");
        telaCheia.setText(config.isTelaCheia() ? "Ligado" : "Desligado");
    }

    @Override
    public void act(float delta) {
        if (isVisible()) atualizarValores();
        super.act(delta);
    }

    public void definirVisivel(boolean visivel) {
        setVisible(visivel);
        setTouchable(visivel ? Touchable.childrenOnly : Touchable.disabled);
        if (visivel) atualizarValores();
    }

    public void dispose() {
        fonte.dispose();
        textura.dispose();
    }
}
