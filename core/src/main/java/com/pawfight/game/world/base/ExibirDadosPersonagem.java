package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.entity.player.PlayerTemplate;

public class ExibirDadosPersonagem {
    private BitmapFont font;
    private Texture coracao;
    private Texture raio;
    private Texture musculo;
    private Texture requa;
    private Texture nuvemChao;
    private ShapeRenderer shapeRenderer;

    public ExibirDadosPersonagem() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelOperator8-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24; // tamanho da fonte
        font = generator.generateFont(parameter);
        generator.dispose();

        coracao = new Texture("Hud/coracao1.png");
        raio = new Texture("Hud/raio.png");
        musculo = new Texture("Hud/musculo.png");
        requa = new Texture("Hud/requa.png");
        nuvemChao = new Texture("Hud/nuvemChao.png");
        shapeRenderer = new ShapeRenderer();
    }

    public void draw(SpriteBatch batch, PlayerTemplate player) {
        if (player == null) return;

        // --- Calcula posição centralizada verticalmente ---
        float centerY = Gdx.graphics.getHeight() / 2f; // meio da tela
        float offsetX = Gdx.graphics.getWidth() /4; // margem da esquerda
        float centerX = Gdx.graphics.getWidth()/2; // margem da esquerda
        float boxWidth = 320;
        float boxHeight = 250;

        // --- Quadrado cinza escuro atrás do texto ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
        shapeRenderer.rect(offsetX - 10, centerY - boxHeight / 2, boxWidth, boxHeight);
        shapeRenderer.end();

        // --- Texto e coração ---
        batch.begin();

        //Mensagem para selecionar personagem
        font.setColor(Color.BLACK);
        font.draw(batch, "=============================", (float) (centerX - centerX * 0.26),(float) (centerY * 1.5 + 50));
        font.draw(batch, "Select your character", (float) (centerX - centerX * 0.23), (float) (centerY * 1.5));
        font.draw(batch, "=============================", (float) (centerX - centerX * 0.26), (float) (centerY * 1.5 - 50));

        font.setColor(Color.WHITE);
        font.draw(batch, "Status", offsetX + 90, centerY  + 110);
        font.draw(batch,player.getName(), offsetX + 50, centerY  + 80);
        // Vida
        font.draw(batch, "Life: " + player.getVidaBase(), offsetX, centerY + 20);
        batch.draw(coracao, offsetX + 250, centerY, 24, 24);

        // Velocidade
        font.draw(batch, "Speed: " + player.getVelocidade()/100, offsetX, centerY - 10);
        batch.draw(raio, offsetX + 235, centerY - 45, 56, 56);

        // Força
        font.draw(batch, "Strength: " + player.getForca(), offsetX, centerY - 40);
        batch.draw(musculo, offsetX + 235, centerY - 85, 56, 56);

        // Tamanho
        font.draw(batch, "Size: " + player.getTamanhoPx() + " cm", offsetX, centerY - 70);
        batch.draw(requa, offsetX + 235, centerY - 120, 56, 56);


        batch.draw(nuvemChao, offsetX - 80, centerY - 350, 1024, 280);
        batch.draw(nuvemChao, offsetX - 80, centerY - 430, 1024, 280);

        batch.end();
    }


    public void dispose() {
        font.dispose();
        coracao.dispose();
        raio.dispose();
        musculo.dispose();
        requa.dispose();
        nuvemChao.dispose();
        shapeRenderer.dispose();
    }
}
