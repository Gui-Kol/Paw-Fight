package com.pawfight.game.entity.component;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class InputComponent {

    public boolean isMoveRight()  { return Gdx.input.isKeyPressed(Input.Keys.D); }
    public boolean isMoveLeft()   { return Gdx.input.isKeyPressed(Input.Keys.A); }
    public boolean isMoveUp()     { return Gdx.input.isKeyPressed(Input.Keys.W); }
    public boolean isMoveDown()   { return Gdx.input.isKeyPressed(Input.Keys.S); }

    public boolean isAttackSpecial() { return Gdx.input.isKeyJustPressed(Input.Keys.SPACE); }
    public boolean isAbility()       { return Gdx.input.isKeyJustPressed(Input.Keys.R); }

    public boolean isPauseToggle()  { return Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE); }
    public boolean isDebugToggle()  { return Gdx.input.isKeyJustPressed(Input.Keys.F3); }
    public boolean isCheatToggle()  { return Gdx.input.isKeyJustPressed(Input.Keys.F6); }
}

