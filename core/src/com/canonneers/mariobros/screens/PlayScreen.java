package com.canonneers.mariobros.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.scenes.Hud;
import com.canonneers.mariobros.sprites.Enemies.Enemy;
import com.canonneers.mariobros.sprites.Items.Item;
import com.canonneers.mariobros.sprites.Items.ItemDef;
import com.canonneers.mariobros.sprites.Items.Mushroom;
import com.canonneers.mariobros.sprites.Mario;
import com.canonneers.mariobros.tools.B2WorldCreator;
import com.canonneers.mariobros.tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

public class PlayScreen implements Screen {

    private TextureAtlas atlas;

    private World world;
    private Box2DDebugRenderer b2dRenderer;
    private B2WorldCreator creator;

    private MarioBros game;
    private OrthographicCamera gameCamera;
    private Viewport gameViewport;
    private Hud hud;

    private Mario player;

    private Music music;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private float worldWidth;


    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    public  PlayScreen(MarioBros game) {
        atlas = new TextureAtlas("sprites/Mario_and_enemies.pack");

        // creating game cam and hud
        this.game = game;
        gameCamera = new OrthographicCamera();
        gameViewport = new FitViewport(MarioBros.V_WIDTH / MarioBros.PIXEL_PER_METER, MarioBros.V_HEIGHT / MarioBros.PIXEL_PER_METER, gameCamera);
        hud = new Hud(game.batch);

        //loading level
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("maps/level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PIXEL_PER_METER);
        gameCamera.position.set(gameViewport.getWorldWidth()/2, gameViewport.getWorldHeight()/2, 0);
        world = new World(new Vector2(0, -10), true);
        b2dRenderer = new Box2DDebugRenderer();

        creator = new B2WorldCreator(this);

        player = new Mario(this);

        world.setContactListener(new WorldContactListener());

        music = game.getAssetManager().get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();

        worldWidth = map.getProperties().get("width", Integer.class) * map.getProperties().get("tilewidth", Integer.class)/MarioBros.PIXEL_PER_METER;

}

    @Override
    public void show() {

    }

    public void handleInput(float deltaTime) {

        /**TODO
         * rewrite this crap
          */
        if(player.currentState != Mario.State.DEAD) {
            // Control player using impulses
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && player.b2dbody.getLinearVelocity().y == 0)
                player.b2dbody.applyLinearImpulse(new Vector2(0, 4F), player.b2dbody.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2dbody.getLinearVelocity().x <= 2)
                player.b2dbody.applyLinearImpulse(new Vector2(0.1F, 0), player.b2dbody.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2dbody.getLinearVelocity().x >= -2)
                player.b2dbody.applyLinearImpulse(new Vector2(-0.1F, 0), player.b2dbody.getWorldCenter(), true);
        }

    }

    public void update(float deltaTime) {
        handleInput(deltaTime);
        handleSpawningItems();

        //Read about it
        world.step(1/60F, 6, 2);
        player.update(deltaTime);
        hud.update(deltaTime);
        for(Enemy enemy : creator.getEnemies()) {
            enemy.update(deltaTime);
            if (enemy.getX() < player.getX() + 224/MarioBros.PIXEL_PER_METER) {
                enemy.b2dbody.setActive(true);
            }
        }
        for (Item item : items) {
            item.update(deltaTime);
        }

        // gameCamera controller
        if(player.b2dbody.getPosition().x < gameCamera.viewportWidth/2) {
            gameCamera.position.x = gameCamera.viewportWidth/2;
        } else if(player.b2dbody.getPosition().x > worldWidth - gameCamera.viewportWidth/2) {
            gameCamera.position.x = worldWidth  - gameCamera.viewportWidth/2;
        } else
            gameCamera.position.x = player.b2dbody.getPosition().x;


        gameCamera.update();
        renderer.setView(gameCamera);
        if(player.marioIsDead()) {
            music.stop();
            gameCamera.position.x = player.b2dbody.getPosition().x;
        }

    }

    @Override
    public void render(float delta) {
        //separate logic from render
        update(delta);

        //clear the game screen with Black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render our game lap
        renderer.render();

        //render our Box2DDebugLines
        b2dRenderer.render(world, gameCamera.combined);

        game.batch.setProjectionMatrix(gameCamera.combined);
        game.batch.begin();
        player.draw(game.batch);
        for(Enemy enemy : creator.getEnemies()) {
            enemy.draw(game.batch);
        }
        for(Item item : items) {
            item.draw(game.batch);
        }
        game.batch.end();

        //Set our batch to now draw what the Hud camera sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

    }
    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dRenderer.dispose();
        hud.dispose();
    }

    public void spawnItem(ItemDef idef) {
        itemsToSpawn.add(idef);
    }

    public void handleSpawningItems() {
        if (!itemsToSpawn.isEmpty()) {
            ItemDef idef = itemsToSpawn.poll();
            if (idef.type == Mushroom.class) {
                items.add(new Mushroom(this, idef.pos.x, idef.pos.y));
            }
        }
    }

    public boolean gameOver() {
        if(player.marioIsDead() && player.getStateTimer() > 3F ) {
            return true;
        } else
        return false;
    }


    public TiledMap getMap() { return map; }

    public World getWorld() { return world; }

    public Hud getHud() { return hud; }

    public MarioBros getGame()  { return game; }

    public TextureAtlas getAtlas() {
        return atlas;
    }

}
