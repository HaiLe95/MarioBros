package com.canonneers.mariobros.sprites.Items;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.screens.PlayScreen;
import com.canonneers.mariobros.sprites.Mario;

public abstract class Item extends Sprite {

    protected PlayScreen screen;
    protected World world;
    protected Vector2 velocity;
    protected boolean toDestroy;
    protected boolean destroyed;
    protected Body body;

    public Item(PlayScreen screen, float x, float y) {
        this.screen = screen;
        this.world = screen.getWorld();
        setPosition(x, y);
        setBounds(getX(), getY(), 16/MarioBros.PIXEL_PER_METER, 16/MarioBros.PIXEL_PER_METER);
        defineItem();
        toDestroy = false;
        destroyed = false;
    }

    public abstract void defineItem();
    public abstract void useItem(Mario mario);

    public void update(float deltaTime) {
        if(toDestroy && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }
    }
    public void draw(Batch batch) {
        if(!destroyed) {
            super.draw(batch);
        }
    }

    public void destroy() {
        toDestroy = true;
    }

    public void reverseVelocity(boolean x, boolean y) {
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
