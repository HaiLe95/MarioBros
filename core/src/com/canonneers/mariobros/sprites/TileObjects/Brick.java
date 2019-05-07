package com.canonneers.mariobros.sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.scenes.Hud;
import com.canonneers.mariobros.screens.PlayScreen;
import com.canonneers.mariobros.sprites.Mario;

public class Brick extends InteractiveTileObject {
    private Hud hud;
    private MarioBros game;

    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
        this.hud = screen.getHud();
        this.game = screen.getGame();
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBig()) {
            setCategoryFilter(MarioBros.DESTROY_BIT);
            getCell().setTile(null);
            game.getAssetManager().get("audio/sound/breakblock.ogg", Sound.class).play(1.0F);
            hud.addScore(200);
        } else {
            game.getAssetManager().get("audio/sound/bump.ogg", Sound.class).play(1.0F);
        }
    }
}
