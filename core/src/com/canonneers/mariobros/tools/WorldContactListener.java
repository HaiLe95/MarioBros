package com.canonneers.mariobros.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.canonneers.mariobros.MarioBros;
import com.canonneers.mariobros.sprites.Enemies.Enemy;
import com.canonneers.mariobros.sprites.Items.Item;
import com.canonneers.mariobros.sprites.Mario;
import com.canonneers.mariobros.sprites.TileObjects.InteractiveTileObject;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;


        // checks if mario jumps on enemy's head and kills them
        switch (cDef) {
            case MarioBros.MARIO_HEAD_BIT | MarioBros.BRICK_BIT:
            case MarioBros.MARIO_HEAD_BIT | MarioBros.COIN_BIT:
                if(fixtureA.getFilterData().categoryBits ==  MarioBros.MARIO_HEAD_BIT)
                    ((InteractiveTileObject) fixtureB.getUserData()).onHeadHit((Mario) fixtureA.getUserData());
                else
                    ((InteractiveTileObject) fixtureA.getUserData()).onHeadHit((Mario) fixtureB.getUserData());
                break;
            case MarioBros.ENEMY_HEAD_BIT | MarioBros.MARIO_BIT:
                if(fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_HEAD_BIT)
                    ((Enemy)fixtureA.getUserData()).hitOnHead((Mario) fixtureB.getUserData());
                else
                    ((Enemy)fixtureB.getUserData()).hitOnHead((Mario) fixtureA.getUserData());
                break;
            case MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT :
                if(fixtureA.getFilterData().categoryBits == MarioBros.ENEMY_BIT)
                    ((Enemy)fixtureA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.MARIO_BIT | MarioBros.ENEMY_BIT:
                if(fixtureA.getFilterData().categoryBits ==  MarioBros.MARIO_BIT)
                    ((Mario) fixtureA.getUserData()).hit((Enemy)fixtureB.getUserData());
                else
                    ((Mario) fixtureB.getUserData()).hit((Enemy)fixtureA.getUserData());
                break;
            case MarioBros.ENEMY_BIT | MarioBros.ENEMY_BIT:
                ((Enemy)fixtureA.getUserData()).reverseVelocity(true, false);
                ((Enemy)fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.ITEM_BIT | MarioBros.OBJECT_BIT :
                if(fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item)fixtureA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixtureB.getUserData()).reverseVelocity(true, false);
                break;
            case MarioBros.ITEM_BIT | MarioBros.MARIO_BIT :
                if(fixtureA.getFilterData().categoryBits == MarioBros.ITEM_BIT)
                    ((Item)fixtureA.getUserData()).useItem((Mario)fixtureB.getUserData());
                else
                    ((Item)fixtureB.getUserData()).useItem((Mario)fixtureA.getUserData());
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
