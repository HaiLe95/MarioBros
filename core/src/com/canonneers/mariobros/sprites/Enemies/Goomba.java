package com.canonneers.mariobros.sprites.Enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.screens.PlayScreen;
import com.canonneers.mariobros.sprites.Mario;

public class Goomba extends Enemy {

    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;

    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i =0; i < 2; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }
        walkAnimation = new Animation(0.5f, frames);
        stateTime = 0;
        setBounds(getX(), getY(), 16 / MarioBros.PIXEL_PER_METER, 16 / MarioBros.PIXEL_PER_METER);
        setToDestroy = false;
        destroyed = false;
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if(setToDestroy && !destroyed) {
            world.destroyBody(b2dbody);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTime = 0;
        } else if(!destroyed) {
            // make goomba walk
            b2dbody.setLinearVelocity(velocity);
            setPosition(b2dbody.getPosition().x - getWidth() / 2, b2dbody.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(),getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6.5f / MarioBros.PIXEL_PER_METER);

        fixtureDef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.MARIO_BIT;

        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);

        //Create head hitbox vertice is points in XY coordinates
        PolygonShape headShape = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-5, 8).scl(1/MarioBros.PIXEL_PER_METER);
        vertices[1] = new Vector2(5, 8).scl(1/MarioBros.PIXEL_PER_METER);
        vertices[2] = new Vector2(-3, 3).scl(1/MarioBros.PIXEL_PER_METER);
        vertices[3] = new Vector2(3, 3).scl(1/MarioBros.PIXEL_PER_METER);
        headShape.set(vertices);

        fixtureDef.shape = headShape;
        //jumping when he's get jumped on head
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;
        b2dbody.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void draw(Batch batch) {
        if(!destroyed || stateTime < 1)
            super.draw(batch);
    }

    @Override
    public void hitOnHead(Mario mario) {
        setToDestroy = true;
        screen.getGame().getAssetManager().get("audio/sound/stomp.ogg", Sound.class).play(1.0F);
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle &&((Turtle)enemy).currentState == Turtle.State.MOVING_SHELL)
            setToDestroy = true;
         else
            this.reverseVelocity(true, false);

    }


}
