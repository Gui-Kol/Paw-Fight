package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Timer;
import com.pawfight.game.entity.player.*;

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



    public EscolherPersonagem() {
        batch = new SpriteBatch();

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
        bgX2 = Gdx.graphics.getWidth() -1; // começa logo após a primeira

        exibirDadosPersonagem = new ExibirDadosPersonagem();
        personagemPreview = getPlayerEscolhido();

        // Inicializa nuvem fora da tela à direita
        resetNuvem();
    }

    private void resetNuvem() {
        nuvemX = Gdx.graphics.getWidth(); // começa fora da tela
        nuvemY = random.nextInt(Gdx.graphics.getHeight() - nuvem.getHeight()); // altura aleatória
    }
    private void updateNuvem(){
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
            return getPlayerEscolhido();
        }
        return null;
    }

    private void drawNuvem(){
        batch.draw(backGroud, bgX1, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(backGroud, bgX2, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.draw(nuvem, nuvemX, nuvemY);
    }

    public void draw() {
        batch.begin();
        drawNuvem();
        Texture personagem = personagens[personagemAtual];
        float largura = 200;
        float altura = 200;
        float x = (Gdx.graphics.getWidth() - largura) / 2f;
        float y = (Gdx.graphics.getHeight() - altura) / 2f;
        batch.draw(personagem, x, y, largura, altura);
        batch.end();

        exibirDadosPersonagem.draw(batch, personagemPreview);
    }

    public PlayerTemplate getPlayerEscolhido() {
        switch (personagemAtual) {
            case 0: return new PlayerBlackCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
            case 1: return new PlayerOrangeCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
            case 2: return new PlayerBlackBird(33, 2335, 3200, 1280, 2400, 720, 0.5f);
            case 3: return new PlayerDove(33, 2335, 3200, 1280, 2400, 720, 0.5f);
            default: return new PlayerBlackCat(33, 2335, 3200, 1280, 2400, 720, 0.5f);
        }
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
