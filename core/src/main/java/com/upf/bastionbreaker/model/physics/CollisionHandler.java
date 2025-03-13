package com.upf.bastionbreaker.model.physics;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object dataA = fixtureA.getBody().getUserData();
        Object dataB = fixtureB.getBody().getUserData();
        System.out.println("Collision began between: " + dataA + " and " + dataB);
        // Vous pouvez ajouter ici la logique pour empêcher le joueur de traverser un obstacle,
        // ou pour appliquer une force, etc.
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        Object dataA = fixtureA.getBody().getUserData();
        Object dataB = fixtureB.getBody().getUserData();
        System.out.println("Collision ended between: " + dataA + " and " + dataB);
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
