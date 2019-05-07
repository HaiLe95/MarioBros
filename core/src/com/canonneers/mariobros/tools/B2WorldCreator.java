package com.canonneers.mariobros.tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.screens.PlayScreen;
import com.canonneers.mariobros.sprites.Enemies.Enemy;
import com.canonneers.mariobros.sprites.Enemies.Turtle;
import com.canonneers.mariobros.sprites.TileObjects.Brick;
import com.canonneers.mariobros.sprites.TileObjects.Coin;
import com.canonneers.mariobros.sprites.Enemies.Goomba;

public class B2WorldCreator {

    private Array<Goomba> goombas;
    private Array<Turtle> turtles;

    private TiledMap map;
    private World world;

    public B2WorldCreator(PlayScreen screen) {
        map = screen.getMap();
        world = screen.getWorld();

        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();
        Body body;

        // ground
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PIXEL_PER_METER, (rectangle.getY() + rectangle.getHeight() / 2 ) / MarioBros.PIXEL_PER_METER);

            body = world.createBody(bodyDef);

            shape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PIXEL_PER_METER, rectangle.getHeight() / 2 / MarioBros.PIXEL_PER_METER);
            fDef.shape = shape;
            body.createFixture(fDef);
        }

        // pipes
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PIXEL_PER_METER, (rectangle.getY() + rectangle.getHeight() / 2 ) / MarioBros.PIXEL_PER_METER);

            body = world.createBody(bodyDef);

            shape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PIXEL_PER_METER, rectangle.getHeight() / 2 / MarioBros.PIXEL_PER_METER);
            fDef.shape = shape;
            fDef.filter.categoryBits = MarioBros.OBJECT_BIT;
            body.createFixture(fDef);
        }

        //coins
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            new Coin(screen, object);
        }
        //bricks
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            new Brick(screen, object);
        }
        //create all goombas
        goombas = new Array();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
            goombas.add(new Goomba(screen, rectangle.getX() / MarioBros.PIXEL_PER_METER, rectangle.getY() / MarioBros.PIXEL_PER_METER));
        }

        turtles = new Array();
        for (MapObject object: map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rectangle = ((RectangleMapObject)object).getRectangle();
            turtles.add(new Turtle(screen, rectangle.getX() / MarioBros.PIXEL_PER_METER, rectangle.getY() / MarioBros.PIXEL_PER_METER));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(turtles);
        return enemies;
    }
}
