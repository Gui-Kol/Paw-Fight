package com.pawfight.game.world.base;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.pawfight.game.engine.loading.Assets;
import com.pawfight.game.entity.player.PlayerTemplate;

import com.pawfight.game.engine.GameConfig;

public class ExibirDadosPersonagem {
    private static final Color BG_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.8f);

    private BitmapFont font;
    private Texture coracao;
    private Texture raio;
    private Texture musculo;
    private Texture requa;
    private Texture nuvemChao;
    private ShapeRenderer shapeRenderer;
    private final GlyphLayout layoutStatus = new GlyphLayout();
    private final GlyphLayout layoutLife = new GlyphLayout();
    private final GlyphLayout layoutSpeed = new GlyphLayout();
    private final GlyphLayout layoutMuscle = new GlyphLayout();

    public ExibirDadosPersonagem() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/PixelOperator8-Bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 24;
        font = generator.generateFont(parameter);
        generator.dispose();

        coracao = Assets.get("Hud/coracao1.png", Texture.class);
        raio = Assets.get("Hud/raio.png", Texture.class);
        musculo = Assets.get("Hud/musculo.png", Texture.class);
        requa = Assets.get("Hud/requa.png", Texture.class);
        nuvemChao = Assets.get("Hud/nuvemChao.png", Texture.class);
        shapeRenderer = new ShapeRenderer();
    }

    public void draw(SpriteBatch batch, PlayerTemplate player, float playerX, float playerY) {
        if (player == null) return;
        var scale = GameConfig.getInstance().getScale();

        float offsetX = playerX - 400 * scale; // desloca para a esquerda do player
        float centerY = playerY + 100 * scale; // altura alinhada ao player

        float widthFundo = 330 * scale;
        float heightFundo = 250 * scale;

        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(BG_COLOR);
        shapeRenderer.rect(offsetX - 10 * scale, centerY - 125 * scale,widthFundo, heightFundo);
        shapeRenderer.end();

        batch.begin();

        font.getData().setScale(scale);
        font.setColor(Color.WHITE);

        layoutStatus.setText(font, "Status");
        float statusX = offsetX + (widthFundo - layoutStatus.width) / 2 - 10 * scale;
        font.draw(batch, "Status", statusX, centerY + 110 * scale);
        font.draw(batch, player.getName(), offsetX, centerY + 80 * scale);

        String textoLife = "Life: " + player.getVidaBase();
        layoutLife.setText(font, textoLife);
        font.draw(batch, textoLife, offsetX, centerY + 20 * scale);
        batch.draw(coracao, offsetX + layoutLife.width + 10 * scale, centerY, 24 * scale, 24 * scale);

        String textoSpeed = "Speed: " + player.getVelocidade() / 100;
        layoutSpeed.setText(font, textoSpeed);
        font.draw(batch, textoSpeed, offsetX, centerY - 10 * scale);
        batch.draw(raio, offsetX + layoutSpeed.width, centerY - 45 * scale, 56 * scale, 56 * scale);

        String textoMuscle = "Strength: " + player.getForca();
        layoutMuscle.setText(font, textoMuscle);
        font.draw(batch, textoMuscle, offsetX, centerY - 40 * scale);
        batch.draw(musculo, offsetX + layoutMuscle.width, centerY - 85 * scale, 56 * scale, 56 * scale);

        font.draw(batch, "Size: " + player.getTamanho() + " cm", offsetX, centerY - 70 * scale);
        batch.draw(requa, offsetX + 235 * scale, centerY - 120 * scale, 56 * scale, 56 * scale);

        batch.end();
    }

    public void dispose() {
        font.dispose();
        // Texturas são gerenciadas pelo AssetManager — NÃO dar dispose aqui
        shapeRenderer.dispose();
    }
}
