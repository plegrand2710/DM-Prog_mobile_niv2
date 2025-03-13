package com.upf.bastionbreaker.model.physics;

import com.badlogic.gdx.physics.box2d.*;

public class CollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object dataA = fixtureA.getBody().getUserData();
        Object dataB = fixtureB.getBody().getUserData();
        System.out.println("Collision began between: " + dataA + " and " + dataB);

        // Vérifier si l'un des bodies est le Player et l'autre est le sol ("Floor")
        if ((dataA instanceof com.upf.bastionbreaker.model.entities.Player && "Floor".equals(dataB)) ||
            (dataB instanceof com.upf.bastionbreaker.model.entities.Player && "Floor".equals(dataA))) {
            com.upf.bastionbreaker.model.entities.Player player = (dataA instanceof com.upf.bastionbreaker.model.entities.Player)
                ? (com.upf.bastionbreaker.model.entities.Player) dataA
                : (com.upf.bastionbreaker.model.entities.Player) dataB;
            player.setOnGround(true);
            System.out.println("Player a touché le sol.");
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object dataA = fixtureA.getBody().getUserData();
        Object dataB = fixtureB.getBody().getUserData();
        System.out.println("Collision ended between: " + dataA + " and " + dataB);

        if ((dataA instanceof com.upf.bastionbreaker.model.entities.Player && "Floor".equals(dataB)) ||
            (dataB instanceof com.upf.bastionbreaker.model.entities.Player && "Floor".equals(dataA))) {
            com.upf.bastionbreaker.model.entities.Player player = (dataA instanceof com.upf.bastionbreaker.model.entities.Player)
                ? (com.upf.bastionbreaker.model.entities.Player) dataA
                : (com.upf.bastionbreaker.model.entities.Player) dataB;
            player.setOnGround(false);
            System.out.println("Player a quitté le sol.");
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Optionnel : ajuster le contact si nécessaire
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Optionnel : gérer l'impulsion de collision si nécessaire
    }
}
