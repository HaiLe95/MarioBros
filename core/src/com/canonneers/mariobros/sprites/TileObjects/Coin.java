package com.canonneers.mariobros.sprites.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.scenes.Hud;
import com.canonneers.mariobros.screens.PlayScreen;
import com.canonneers.mariobros.sprites.Items.ItemDef;
import com.canonneers.mariobros.sprites.Items.Mushroom;
import com.canonneers.mariobros.sprites.Mario;

public class Coin extends InteractiveTileObject {

    private static TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;
    private Hud hud;
    private MarioBros game;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        this.hud = screen.getHud();
        setCategoryFilter(MarioBros.COIN_BIT);
        this.game = screen.getGame();
    }


    @Override
    public void onHeadHit(Mario mario) {
        if(getCell().getTile().getId() == BLANK_COIN) {
            game.getAssetManager().get("audio/sound/bump.ogg", Sound.class).play(1.0F);
        }
        else {
            if(object.getProperties().containsKey("mushroom")) {
                screen.spawnItem(new ItemDef(new Vector2(body.getPosition().x, body.getPosition().y + 16 / MarioBros.PIXEL_PER_METER), Mushroom.class));
                game.getAssetManager().get("audio/sound/powerup_spawn.ogg", Sound.class).play(1.0F);
            }
            else {
                game.getAssetManager().get("audio/sound/coin.ogg", Sound.class).play(1.0F);
            }
            hud.addScore(500);
        }
        getCell().setTile(tileSet.getTile(BLANK_COIN));
    }
}
