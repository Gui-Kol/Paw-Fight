package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pawfight.game.PawFight;
import com.pawfight.game.entity.player.PlayerBlackBird;
import com.pawfight.game.entity.player.PlayerBlackCat;
import com.pawfight.game.entity.player.PlayerOrangeCat;
import com.pawfight.game.entity.player.PlayerTemplate;
import com.pawfight.game.entity.player.dove.PlayerDove;

import java.util.Random;

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
        batch = new SpriteBatch();
        viewport = new FitViewport(1920, 1080);

        personagens = new Texture[]{
            new Texture("entitys/player/selecao/black_cat.png"),
            new Texture("entitys/player/selecao/orange_cat.png"),
            new Texture("entitys/player/selecao/black_bird.png"),
            new Texture("entitys/player/selecao/blue_bird.png")
        };

        backGroud = new Texture("world/base/nuvens/back.png");
        nuvem = new Texture("world/base/nuvens/4.png");

        personagemAtual = 0;

        random = new Random();

        backGroud = new Texture("world/base/nuvens/back.png");

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
            escolhido.autoSave(); // inicia o ciclo de autosave
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
        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

        float baseW = 1920f;
        float baseH = 1080f;

        float scaleX = worldW / baseW;
        float scaleY = worldH / baseH;
        float scale = Math.min(scaleX, scaleY);

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        // Background
        batch.draw(backGroud, bgX1, 0, worldW, worldH);
        batch.draw(backGroud, bgX2, 0, worldW, worldH);

        // Personagem centralizado
        Texture personagem = personagens[personagemAtual];
        float largura = 200 * scale;
        float altura = 200 * scale;
        float playerX = (worldW - largura) / 2f;
        float playerY = (worldH - altura) / 2f;
        batch.draw(personagem, playerX, playerY, largura, altura);

        // Nuvem nos pés do player
        float nuvemW = (nuvem.getWidth() * scale) * 1.5f;
        float nuvemH = (nuvem.getHeight() * scale) * 1.5f;
        float nuvemX = (playerX - (nuvemW - largura) / 2f) * 1.1f; // centraliza com player
        float nuvemY = (playerY - nuvemH) + playerY * 0.22f;                  // pés do player
        batch.draw(nuvem, nuvemX, nuvemY, nuvemW, nuvemH);

        batch.end();

        // HUD à esquerda do player
        exibirDadosPersonagem.draw(batch, personagemPreview, playerX, playerY, scale);
    }

    public PlayerTemplate getPlayerEscolhido() {
        PlayerTemplate playerEscolhido;
        switch (personagemAtual) {
            case 0:
                playerEscolhido = new PlayerBlackCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
            case 1:
                playerEscolhido = new PlayerOrangeCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
            case 2:
                playerEscolhido = new PlayerBlackBird(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
            case 3:
                playerEscolhido = new PlayerDove(33, 2335, 3200, 1280, 2400, 720, 0.5f);
                break;
            default:
                playerEscolhido = new PlayerBlackCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
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
        for (Texture t : personagens) {
            t.dispose();
        }
        exibirDadosPersonagem.dispose();
        backGroud.dispose();
        nuvem.dispose();
        batch.dispose();
        exibirDadosPersonagem.dispose();
    }
}
