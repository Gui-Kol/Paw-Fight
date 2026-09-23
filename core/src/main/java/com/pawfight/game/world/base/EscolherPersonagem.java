package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.engine.input.GameAction;
import com.pawfight.game.engine.input.GerenciadorInput;
import com.pawfight.game.entity.component.SaveComponent;
import com.pawfight.game.entity.player.BlackBird;
import com.pawfight.game.entity.player.BlackCat;
import com.pawfight.game.entity.player.OrangeCat;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.player.Dove;

import java.util.Random;

import com.pawfight.game.engine.GameConfig;
import static com.pawfight.game.engine.GameConfig.LARGURA_TELA_BASE;
import static com.pawfight.game.engine.GameConfig.ALTURA_TELA_BASE;

public class EscolherPersonagem {
    private final ExibirDadosPersonagem exibirDadosPersonagem;
    private PlayerTemplate personagemPreview;
    private final Texture[] personagens;
    private int personagemAtual;
    private final SpriteBatch batch;
    private final Texture backGroud;
    private final Texture nuvem;

    private float bgX1, bgX2;
    private float bgVelocidade = 50; // pixels por segundo

    private float nuvemX;
    private float nuvemY;
    private float nuvemVelocidade = 200; // pixels por segundo
    private Random random;

    private PawFight game;
    private final FitViewport viewport;

    // Players pré-criados — evita recriar PlayerTemplate a cada mudança de seta
    private final PlayerTemplate[] previews;


    public EscolherPersonagem(PawFight game) {
        this.game = game;
        batch = game.getBatch();
        viewport = new FitViewport(LARGURA_TELA_BASE, ALTURA_TELA_BASE);

        personagens = new Texture[]{
            Assets.get("entitys/player/selecao/black_cat.png", Texture.class),
            Assets.get("entitys/player/selecao/orange_cat.png", Texture.class),
            Assets.get("entitys/player/selecao/black_bird.png", Texture.class),
            Assets.get("entitys/player/selecao/blue_bird.png", Texture.class)
        };

        backGroud = Assets.get("world/base/nuvens/back.png", Texture.class);
        nuvem = Assets.get("world/base/nuvens/4.png", Texture.class);

        personagemAtual = 0;

        random = new Random();

        SaveComponent saveComponent = new SaveComponent();
        previews = new PlayerTemplate[] {
            saveComponent.loadSaveData(new BlackCat(33, 2335, 3200, 1280, 2400, 720, 0.5f)),
            saveComponent.loadSaveData(new OrangeCat(33, 2335, 3200, 1280, 2400, 720, 0.5f)),
            saveComponent.loadSaveData(new BlackBird(33, 2335, 3200, 1280, 2400, 720, 0.5f)),
            saveComponent.loadSaveData(new Dove(33, 2335, 3200, 1280, 2400, 720, 0.5f)),
        };

        // Desativa o input do menu de pausa dos previews; o selecionado é reativado ao entrar no jogo
        for (PlayerTemplate preview : previews) {
            preview.getHudPause().setInputAtivo(false);
        }

        // Duas cópias do background para scroll infinito
        bgX1 = 0;
        bgX2 = Gdx.graphics.getWidth() - 1;

        exibirDadosPersonagem = new ExibirDadosPersonagem();
        personagemPreview = previews[personagemAtual];

        resetNuvem();

        Gdx.app.log("EscolherPersonagem", "Inicializado com " + previews.length + " personagens carregados (saves aplicados).");
    }

    private void resetNuvem() {
        nuvemX = Gdx.graphics.getWidth();
        nuvemY = random.nextInt(Gdx.graphics.getHeight() - nuvem.getHeight());
    }

    private void updateNuvem() {
        bgX1 -= bgVelocidade * Gdx.graphics.getDeltaTime();
        bgX2 -= bgVelocidade * Gdx.graphics.getDeltaTime();

        nuvemX -= nuvemVelocidade * Gdx.graphics.getDeltaTime();

        // Se uma imagem saiu da tela, reposiciona à direita da outra
        if (bgX1 + Gdx.graphics.getWidth() <= 0) {
            bgX1 = bgX2 + Gdx.graphics.getWidth();
        }
        if (bgX2 + Gdx.graphics.getWidth() <= 0) {
            bgX2 = bgX1 + Gdx.graphics.getWidth();
        }


        if (nuvemX + nuvem.getWidth() < 0) {
            resetNuvem();
        }
    }

    public PlayerTemplate update() {
        updateNuvem();

        // Navegação por teclado ou controle (remapável); analógico tem repetição controlada ao segurar
        GerenciadorInput input = GerenciadorInput.getInstance();

        if (input.isAtivaComRepeticao(GameAction.MENU_RIGHT)) {
            personagemAtual++;
            if (personagemAtual >= personagens.length) {
                personagemAtual = 0;
            }
            personagemPreview = previews[personagemAtual];
            Gdx.app.debug("EscolherPersonagem", "Navegou para: " + personagemPreview.getName() + " [" + personagemAtual + "]");
        }

        if (input.isAtivaComRepeticao(GameAction.MENU_LEFT)) {
            personagemAtual--;
            if (personagemAtual < 0) {
                personagemAtual = personagens.length - 1;
            }
            personagemPreview = previews[personagemAtual];
            Gdx.app.debug("EscolherPersonagem", "Navegou para: " + personagemPreview.getName() + " [" + personagemAtual + "]");
        }

        if (input.isPressionadaAgora(GameAction.MENU_CONFIRM)) {
            Gdx.app.log("EscolherPersonagem", "Personagem confirmado: " + previews[personagemAtual].getName());
            return previews[personagemAtual];
        }

        return null;
    }

    private void drawNuvem() {
        batch.draw(backGroud, bgX1, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(backGroud, bgX2, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(nuvem, nuvemX, nuvemY);
    }

    public void draw() {
        var scale = GameConfig.getInstance().getScale();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.draw(backGroud, bgX1, 0, LARGURA_TELA_BASE, ALTURA_TELA_BASE);
        batch.draw(backGroud, bgX2, 0, LARGURA_TELA_BASE, ALTURA_TELA_BASE);

        Texture personagem = personagens[personagemAtual];
        float largura = 200 * scale;
        float altura = 200 * scale;
        float playerX = (LARGURA_TELA_BASE - largura) / 2f;
        float playerY = (ALTURA_TELA_BASE - altura) / 2f;
        batch.draw(personagem, playerX, playerY, largura, altura);

        // Nuvem nos pés do player
        float nuvemW = (nuvem.getWidth() * scale) * 1.5f;
        float nuvemH = (nuvem.getHeight() * scale) * 1.5f;
        float nuvemX = (playerX - (nuvemW - largura) / 2f) + 100 * scale;
        float nuvemY = (playerY - nuvemH) + 100 * scale;
        batch.draw(nuvem, nuvemX, nuvemY, nuvemW, nuvemH);

        batch.end();

        // HUD à esquerda do player
        exibirDadosPersonagem.draw(batch, personagemPreview, playerX, playerY);
    }


    // Mantém a proporção no resize
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.getCamera().position.set(
            viewport.getWorldWidth() / 2f,
            viewport.getWorldHeight() / 2f,
            0
        );
        viewport.getCamera().update();

        bgX1 = 0;
        bgX2 = viewport.getWorldWidth() - 1;
        nuvemX = viewport.getWorldWidth();
        nuvemY = random.nextInt((int) viewport.getWorldHeight() - nuvem.getHeight());
    }


    public void dispose() {
        // Texturas são gerenciadas pelo AssetManager — NÃO dar dispose aqui
        exibirDadosPersonagem.dispose();
    }
}
