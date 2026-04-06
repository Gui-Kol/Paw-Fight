package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.engine.Assets;
import com.pawfight.game.entity.player.BlackBird;
import com.pawfight.game.entity.player.BlackCat;
import com.pawfight.game.entity.player.OrangeCat;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.player.Dove;

import java.util.Random;

import static com.pawfight.game.engine.VariavelComum.*;

public class EscolherPersonagem {
    private ExibirDadosPersonagem exibirDadosPersonagem;
    private PlayerTemplate personagemPreview;
    private Texture[] personagens;   // lista de texturas dos personagens
    private int personagemAtual;     // índice do personagem atual
    private SpriteBatch batch;
    private Texture backGroud;
    private Texture nuvem;

    // Variáveis para controle do background
    private float bgX1, bgX2;
    private float bgVelocidade = 50; // pixels por segundo

    // Controle da nuvem
    private float nuvemX;
    private float nuvemY;
    private float nuvemVelocidade = 200; // pixels por segundo
    private Random random;

    //Game
    private PawFight game;
    private final FitViewport viewport;


    public EscolherPersonagem(PawFight game) {
        this.game = game;
        batch = game.getBatch();
        viewport = new FitViewport(GET_LARGURA_TELA_BASE, GET_ALTURA_TELA_BASE);

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

        // backGroud já foi carregado acima, linha duplicada removida

        // Inicializa duas cópias do background
        bgX1 = 0;
        bgX2 = Gdx.graphics.getWidth() - 1; // começa logo após a primeira

        exibirDadosPersonagem = new ExibirDadosPersonagem();
        personagemPreview = getPlayerEscolhido();

        // Inicializa nuvem fora da tela à direita
        resetNuvem();
    }

    private void resetNuvem() {
        nuvemX = Gdx.graphics.getWidth(); // começa fora da tela
        nuvemY = random.nextInt(Gdx.graphics.getHeight() - nuvem.getHeight()); // altura aleatória
    }

    private void updateNuvem() {
        // Movimento do background
        bgX1 -= bgVelocidade * Gdx.graphics.getDeltaTime();
        bgX2 -= bgVelocidade * Gdx.graphics.getDeltaTime();

        // Movimento da nuvem
        nuvemX -= nuvemVelocidade * Gdx.graphics.getDeltaTime();

        // Se uma imagem saiu da tela, reposiciona à direita da outra
        if (bgX1 + Gdx.graphics.getWidth() <= 0) {
            bgX1 = bgX2 + Gdx.graphics.getWidth();
        }
        if (bgX2 + Gdx.graphics.getWidth() <= 0) {
            bgX2 = bgX1 + Gdx.graphics.getWidth();
        }


        // Se saiu da tela, reinicia
        if (nuvemX + nuvem.getWidth() < 0) {
            resetNuvem();
        }
    }

    public PlayerTemplate update() {
        updateNuvem();

        // Navegar com setas
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            personagemAtual++;
            if (personagemAtual >= personagens.length) {
                personagemAtual = 0;
            }
            personagemPreview = getPlayerEscolhido(); // atualiza preview
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            personagemAtual--;
            if (personagemAtual < 0) {
                personagemAtual = personagens.length - 1;
            }
            personagemPreview = getPlayerEscolhido(); // atualiza preview
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            PlayerTemplate escolhido = getPlayerEscolhido();
            return escolhido; // esse é o objeto que vai ser usado no jogo
        }

        return null;
    }

    private void drawNuvem() {
        batch.draw(backGroud, bgX1, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(backGroud, bgX2, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(nuvem, nuvemX, nuvemY);
    }

    public void draw() {
        var scale = GET_SCALE();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Background
        batch.draw(backGroud, bgX1, 0, GET_LARGURA_TELA_BASE, GET_ALTURA_TELA_BASE);
        batch.draw(backGroud, bgX2, 0, GET_LARGURA_TELA_BASE, GET_ALTURA_TELA_BASE);

        // Personagem centralizado
        Texture personagem = personagens[personagemAtual];
        float largura = 200 * scale;
        float altura = 200 * scale;
        float playerX = (GET_LARGURA_TELA_BASE - largura) / 2f;
        float playerY = (GET_ALTURA_TELA_BASE - altura) / 2f;
        batch.draw(personagem, playerX, playerY, largura, altura);

        // Nuvem nos pés do player
        float nuvemW = (nuvem.getWidth() * scale) * 1.5f;
        float nuvemH = (nuvem.getHeight() * scale) * 1.5f;
        float nuvemX = (playerX - (nuvemW - largura) / 2f) + 100 * scale; // centraliza com player
        float nuvemY = (playerY - nuvemH) + 100 * scale;                  // pés do player
        batch.draw(nuvem, nuvemX, nuvemY, nuvemW, nuvemH);

        batch.end();

        // HUD à esquerda do player
        exibirDadosPersonagem.draw(batch, personagemPreview, playerX, playerY);
    }

    public PlayerTemplate getPlayerEscolhido() {
        PlayerTemplate playerEscolhido;
        switch (personagemAtual) {
            case 0:
                playerEscolhido = new BlackCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
            case 1:
                playerEscolhido = new OrangeCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
            case 2:
                playerEscolhido = new BlackBird(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
            case 3:
                playerEscolhido = new Dove(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
            default:
                playerEscolhido = new BlackCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
        }

        // Carrega os dados salvos para esse personagem
        playerEscolhido = game.loadPlayer(playerEscolhido, playerEscolhido.getName());

        return playerEscolhido;
    }

    // Corrige o resize para manter proporção
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.getCamera().position.set(
            viewport.getWorldWidth() / 2f,
            viewport.getWorldHeight() / 2f,
            0
        );
        viewport.getCamera().update();

        // Reposiciona elementos dependentes
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
