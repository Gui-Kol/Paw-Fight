package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
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

    public void draw(SpriteBatch batch, PlayerTemplate player, float playerX, float playerY, float scale) {
        if (player == null) return;

        float offsetX = playerX - 400 * scale; // desloca para a esquerda do player
        float centerY = playerY + 100 * scale; // altura alinhada ao player

        // Caixa de fundo
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
        shapeRenderer.rect(offsetX - 10 * scale, centerY - 125 * scale, 320 * scale, 250 * scale);
        shapeRenderer.end();

        batch.begin();

        font.getData().setScale(scale);
        font.setColor(Color.WHITE);

        // Status alinhados à esquerda do player
        font.draw(batch, "Status", offsetX + 90 * scale, centerY + 110 * scale);
        font.draw(batch, player.getName(), offsetX + 50 * scale, centerY + 80 * scale);

        font.draw(batch, "Life: " + player.getVidaBase(), offsetX, centerY + 20 * scale);
        batch.draw(coracao, offsetX + 250 * scale, centerY, 24 * scale, 24 * scale);

        font.draw(batch, "Speed: " + player.getVelocidade() / 100, offsetX, centerY - 10 * scale);
        batch.draw(raio, offsetX + 235 * scale, centerY - 45 * scale, 56 * scale, 56 * scale);

        font.draw(batch, "Strength: " + player.getForca(), offsetX, centerY - 40 * scale);
        batch.draw(musculo, offsetX + 235 * scale, centerY - 85 * scale, 56 * scale, 56 * scale);

        font.draw(batch, "Size: " + player.getTamanhoPx() + " cm", offsetX, centerY - 70 * scale);
        batch.draw(requa, offsetX + 235 * scale, centerY - 120 * scale, 56 * scale, 56 * scale);

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
