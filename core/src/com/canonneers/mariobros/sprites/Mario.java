package com.canonneers.mariobros.sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.screens.PlayScreen;
import com.badlogic.gdx.utils.Array;
import com.canonneers.mariobros.sprites.Enemies.Enemy;
import com.canonneers.mariobros.sprites.Enemies.Turtle;


public class Mario extends Sprite {

    public enum State { FALLING, RUNNING, STANDING, JUMPING, GROWING, DEAD }

    //Graphics
    private Animation marioRun;
    private Animation bigMarioRun, growMario;
    private TextureRegion marioStand, bigMarioStand, bigMarioJump, marioJump, marioDead;
    private Array<TextureRegion> frames;

    //Condition
    public State currentState, previousState;
    private boolean runningRight, marioIsBig, runGrowAnimation;
    private float stateTimer;
    private boolean timeToDefineBigMario, timeToRedefineMario;
    private boolean marioIsDead;

    //else
    public World world;
    public Body b2dbody;
    public PlayScreen screen;
    private MarioBros game;



    public Mario(PlayScreen screen) {
        this.screen = screen;
        world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;
        game = screen.getGame();

        initGraphicModel();
        defineMario();

        setBounds(0, 0, 16 / MarioBros.PIXEL_PER_METER, 16 / MarioBros.PIXEL_PER_METER);
        setRegion(marioStand);
    }

    private void initGraphicModel() {
        //Creating Mario animations
        frames = new Array<TextureRegion>();
        //Little Mario running animation
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16, 0, 16, 16));
        marioRun = new Animation(0.1F, frames);
        frames.clear();
        //Little Mario jump
        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0 , 16, 16);
        //Little Mario stand
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        //Little Mario dead
        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);
        //Big Mario stand
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0,0,16,32);
        //Big Mario jump
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        //Big Mario run
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16, 0, 16, 32));
        bigMarioRun = new Animation(0.1F, frames);
        frames.clear();
        //Mario grow
        for(int i = 0; i <= 4; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16,32));
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16,32));
        }
        growMario = new Animation(0.1F, frames);
        frames.clear();
    }

    public void defineMario() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / MarioBros.PIXEL_PER_METER, 32 / MarioBros.PIXEL_PER_METER);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7 / MarioBros.PIXEL_PER_METER);

        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);

        EdgeShape headShape = new EdgeShape();
        headShape.set(new Vector2(-2 / MarioBros.PIXEL_PER_METER, 6 / MarioBros.PIXEL_PER_METER), new Vector2(2 / MarioBros.PIXEL_PER_METER, 6 / MarioBros.PIXEL_PER_METER));
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fixtureDef.shape = headShape;
//        fixtureDef.isSensor = true;
        b2dbody.createFixture(fixtureDef).setUserData(this);
    }

    public void defineBigMario() {
        Vector2 currentPosition = b2dbody.getPosition();
        world.destroyBody(b2dbody);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition.add(0, 10 / MarioBros.PIXEL_PER_METER));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        b2dbody = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7 / MarioBros.PIXEL_PER_METER);

        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT |
                MarioBros.ENEMY_HEAD_BIT |
                MarioBros.ITEM_BIT;

        fixtureDef.shape = shape;
        b2dbody.createFixture(fixtureDef).setUserData(this);
        shape.setPosition(new Vector2(0, -14 / MarioBros.PIXEL_PER_METER));
        b2dbody.createFixture(fixtureDef).setUserData(this);

        EdgeShape headShape = new EdgeShape();
        headShape.set(new Vector2(-2 / MarioBros.PIXEL_PER_METER, 6 / MarioBros.PIXEL_PER_METER), new Vector2(2 / MarioBros.PIXEL_PER_METER, 6 / MarioBros.PIXEL_PER_METER));
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fixtureDef.shape = headShape;
        fixtureDef.isSensor = true;
        b2dbody.createFixture(fixtureDef).setUserData(this);
        timeToDefineBigMario = false;

        marioIsDead = false;
    }

     public void update(float deltaTime) {
        if(marioIsBig)
            setPosition(b2dbody.getPosition().x - getWidth() / 2, b2dbody.getPosition().y - getHeight() / 2 - 6 / MarioBros.PIXEL_PER_METER);
        else
            setPosition(b2dbody.getPosition().x - getWidth() / 2, b2dbody.getPosition().y - getHeight() / 2);
        setRegion(getFrame(deltaTime));
        if(timeToDefineBigMario)
            defineBigMario();
        if(timeToRedefineMario)
            redefineMario();
     }


     public TextureRegion getFrame(float deltaTime) {
        currentState = getState();

        TextureRegion region;

        switch (currentState) {
            case DEAD:
                region = marioDead;
                break;
            case GROWING:
                region = (TextureRegion) growMario.getKeyFrame(stateTimer);
                if(growMario.isAnimationFinished(stateTimer))
                        runGrowAnimation = false;
                break;
            case JUMPING:
                region = marioIsBig ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                //If mario running we get mario run animation that is repeating
                region = marioIsBig? (TextureRegion) bigMarioRun.getKeyFrame(stateTimer, true) : (TextureRegion) marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
                default:
                    region = marioIsBig? bigMarioStand : marioStand;
                    break;
        }

        //Mario switching animation based in which vector he runs
        if((b2dbody.getLinearVelocity().x <0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2dbody.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        // if his state wasn't change we keep using that state
        stateTimer = currentState == previousState ? stateTimer + deltaTime : 0;
        previousState = currentState;
        return region;
     }

     public void grow() {
        runGrowAnimation = true;
        marioIsBig = true;
        timeToDefineBigMario= true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        game.getAssetManager().get("audio/sound/powerup.ogg", Sound.class).play();
     }

     public void redefineMario() {
        Vector2 currentPosition = b2dbody.getPosition();
        world.destroyBody(b2dbody);


         BodyDef bodyDef = new BodyDef();
         bodyDef.position.set(currentPosition);
         bodyDef.type = BodyDef.BodyType.DynamicBody;
         b2dbody = world.createBody(bodyDef);

         FixtureDef fixtureDef = new FixtureDef();
         CircleShape shape = new CircleShape();
         shape.setRadius(7 / MarioBros.PIXEL_PER_METER);

         fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
         fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                 MarioBros.COIN_BIT |
                 MarioBros.BRICK_BIT |
                 MarioBros.ENEMY_BIT |
                 MarioBros.OBJECT_BIT |
                 MarioBros.ENEMY_HEAD_BIT |
                 MarioBros.ITEM_BIT;

         fixtureDef.shape = shape;
         b2dbody.createFixture(fixtureDef).setUserData(this);

         EdgeShape headShape = new EdgeShape();
         headShape.set(new Vector2(-2 / MarioBros.PIXEL_PER_METER, 6 / MarioBros.PIXEL_PER_METER), new Vector2(2 / MarioBros.PIXEL_PER_METER, 6 / MarioBros.PIXEL_PER_METER));
         fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
         fixtureDef.shape = headShape;
         fixtureDef.isSensor = true;
         b2dbody.createFixture(fixtureDef).setUserData(this);

         timeToRedefineMario = false;
     }

     public void hit(Enemy enemy) {
        if(enemy instanceof Turtle && ((Turtle)enemy).getCurrentState() ==Turtle.State.STANDING_SHELL) {
            ((Turtle)enemy).kick(this.getX() <= enemy.getX()? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        } else {
            if (marioIsBig) {
                marioIsBig = false;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                game.getAssetManager().get("audio/sound/powerdown.ogg", Sound.class).play(1.0F);
            } else {
                //Mario dies and he's falling down through all objects
                game.getAssetManager().get("audio/sound/mariodie.ogg", Sound.class).play(1.0F);
                marioIsDead = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.NOTHING_BIT;
                // TODO check this out
                for (Fixture fixture : b2dbody.getFixtureList())
                    fixture.setFilterData(filter);
                b2dbody.applyLinearImpulse(new Vector2(0, 4F), b2dbody.getWorldCenter(), true);
            }
        }
     }

     public State getState() {
         if(marioIsDead) {
             return State.DEAD;
         }
         else if(runGrowAnimation)
             return State.GROWING;
        // If mario Y coordinates is growing, or mario previous state was jumping his state is jumping
        else if(b2dbody.getLinearVelocity().y > 0 || (b2dbody.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        // If mario wasn't jumping and hes Y coordinate is falling down, his state is falling
        else if(b2dbody.getLinearVelocity().y < 0)
            return State.FALLING;
        // if mario X coordinate growing or falling he's is running
        else if(b2dbody.getLinearVelocity().x != 0 && previousState != State.JUMPING)
            return State.RUNNING;
        // anything else means that mario is standing on the ground
        else if(b2dbody.getLinearVelocity().x == 0 && b2dbody.getLinearVelocity().y == 0)
            return State.STANDING;
        else
            return State.RUNNING;
     }

    public boolean isBig() {
        return marioIsBig;
    }

    public boolean marioIsDead() {
         return marioIsDead;
    }

    public float getStateTimer() { return stateTimer; }
}
