package com.canonneers.mariobros.sprites.Enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.screens.PlayScreen;
import com.canonneers.mariobros.sprites.Mario;

public class Turtle extends Enemy {

    public static final int KICK_LEFT_SPEED = -2;
    public static final int KICK_RIGHT_SPEED = 2;
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL, DEAD}
    public State currentState, previousState;
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;
    private TextureRegion shell;
    private float deadRotationDegrees;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);
        walkAnimation = new Animation(0.2f, frames);
        currentState = previousState = State.WALKING;
        deadRotationDegrees = 0;
        destroyed = false;
        setBounds(getX(), getY(), 16/MarioBros.PIXEL_PER_METER, 24/MarioBros.PIXEL_PER_METER);
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
        vertices[0] = new Vector2(-6, 9).scl(1/MarioBros.PIXEL_PER_METER);
        vertices[1] = new Vector2(6, 9).scl(1/MarioBros.PIXEL_PER_METER);
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
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle) {
            if(((Turtle)enemy).currentState == State.MOVING_SHELL && currentState != State.MOVING_SHELL) {
                killed();
            } else if(currentState == State.MOVING_SHELL && ((Turtle)enemy).currentState == State.WALKING)
                return;
            else
                reverseVelocity(true, false);
        } else if(currentState != State.MOVING_SHELL)
            reverseVelocity(true, false);

    }

    @Override
    public void hitOnHead(Mario mario) {
        if(currentState != State.STANDING_SHELL) {
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        } else {
            kick(mario.getX() <= getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    @Override
    public void update(float deltatime) {
        setRegion(getFrame(deltatime));
        if(currentState == State.STANDING_SHELL && stateTime > 5) {
            currentState = State.WALKING;
            velocity.x = 1;
        }

        setPosition(b2dbody.getPosition().x - getWidth()/2, b2dbody.getPosition().y - 8/MarioBros.PIXEL_PER_METER);
        if(currentState == State.DEAD) {
            deadRotationDegrees += 3;
            rotate(deadRotationDegrees);
            if (stateTime > 5 && !destroyed) {
                world.destroyBody(b2dbody);
                destroyed = true;
            }
        } else {
            b2dbody.setLinearVelocity(velocity);
        }
    }

    public TextureRegion getFrame(float deltatime) {
        TextureRegion region;
        switch (currentState) {
            case STANDING_SHELL:
            case MOVING_SHELL:
                region = shell;
                break;
            case WALKING:
                default:
                region =(TextureRegion) walkAnimation.getKeyFrame(stateTime, true);
                break;
        }
        if(velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        }
        if(velocity.x < 0 && region.isFlipX()) {
            region.flip(true, false);
        }

        stateTime = currentState == previousState? stateTime + deltatime : 0;
        previousState = currentState;

        return region;
    }

    public void kick(int speed) {
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public void killed() {
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;

        for(Fixture fixture : b2dbody.getFixtureList())
            fixture.setFilterData(filter);

        b2dbody.applyLinearImpulse(new Vector2(0, 5f), b2dbody.getWorldCenter(), true);

    }

    public State getCurrentState() {
        return currentState;
    }
}
