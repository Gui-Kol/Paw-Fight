package com.pawfight.game.commun.font;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class FontFactory {
    public static BitmapFont createCustomFont(String path, int size) {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;          // tamanho da fonte
        parameter.color = Color.WHITE;  // cor padrão
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }
}
