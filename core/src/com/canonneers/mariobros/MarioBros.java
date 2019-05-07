package com.canonneers.mariobros;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.canonneers.mariobros.screens.PlayScreen;

public class MarioBros extends Game {

	public static final short NOTHING_BIT = 0;
	public static final short GROUND_BIT = 1;
	public static final short MARIO_BIT = 2;
	public static final short BRICK_BIT = 4;
	public static final short COIN_BIT = 8;
	public static final short DESTROY_BIT = 16;
	public static final short OBJECT_BIT = 32;
	public static final short ENEMY_BIT = 64;
	public static final short ENEMY_HEAD_BIT = 128;
	public static final short ITEM_BIT = 256;
	public static final short MARIO_HEAD_BIT = 512;
	public static final short FIREBALL_BIT = 1024;

	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float PIXEL_PER_METER = 100;

	private AssetManager assetManager;

	public SpriteBatch batch;

	@Override
	public void create() {
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		assetManager.load("audio/music/mario_music.ogg", Music.class);
		assetManager.load("audio/sound/bump.ogg", Sound.class);
		assetManager.load("audio/sound/coin.ogg", Sound.class);
		assetManager.load("audio/sound/powerup_spawn.ogg", Sound.class);
		assetManager.load("audio/sound/breakblock.ogg", Sound.class);
		assetManager.load("audio/sound/powerup.ogg", Sound.class);
		assetManager.load("audio/sound/powerdown.ogg", Sound.class);
		assetManager.load("audio/sound/stomp.ogg", Sound.class);
		assetManager.load("audio/sound/mariodie.ogg", Sound.class);
		assetManager.finishLoading();
		setScreen(new PlayScreen(this));
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		batch.dispose();
	}

	@Override
	public void render() {
		super.render();
	}
}
