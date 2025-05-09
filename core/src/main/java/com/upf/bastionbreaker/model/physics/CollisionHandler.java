package com.upf.bastionbreaker.model.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.entities.Hill;

public class CollisionHandler implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object dataA = fixtureA.getBody().getUserData();
        Object dataB = fixtureB.getBody().getUserData();
        System.out.println("Collision began between: " + dataA + " and " + dataB);

        // Gestion du sol (Floor)
        if ((dataA instanceof com.upf.bastionbreaker.model.entities.Player && "Floor".equals(dataB)) ||
            (dataB instanceof com.upf.bastionbreaker.model.entities.Player && "Floor".equals(dataA))) {
            com.upf.bastionbreaker.model.entities.Player player = (dataA instanceof com.upf.bastionbreaker.model.entities.Player)
                ? (com.upf.bastionbreaker.model.entities.Player) dataA
                : (com.upf.bastionbreaker.model.entities.Player) dataB;
            player.setOnGround(true);
            System.out.println("Player a touché le sol.");
        }

        // Gestion des Hill : considérer les Hill comme un sol sur lequel le joueur peut marcher
        if ((dataA instanceof com.upf.bastionbreaker.model.entities.Player && dataB instanceof Hill) ||
            (dataB instanceof com.upf.bastionbreaker.model.entities.Player && dataA instanceof Hill)) {
            com.upf.bastionbreaker.model.entities.Player player = (dataA instanceof com.upf.bastionbreaker.model.entities.Player)
                ? (com.upf.bastionbreaker.model.entities.Player) dataA
                : (com.upf.bastionbreaker.model.entities.Player) dataB;
            Hill hill = (dataA instanceof Hill) ? (Hill) dataA : (Hill) dataB;

            // Traiter la pente comme un sol : le joueur est considéré comme au sol
            player.setOnGround(true);

            // Ajuster la vitesse du joueur en fonction de la pente
            Vector2 vel = player.getBody().getLinearVelocity();
            // Par exemple, si le joueur monte (vitesse positive), appliquer le facteur climb ;
            // s'il descend (vitesse négative), appliquer le facteur slide.
            if (vel.x > 0) {
                vel.x *= hill.getClimb();
            } else if (vel.x < 0) {
                vel.x *= hill.getSlide();
            }
            player.getBody().setLinearVelocity(vel);
            System.out.println("Player sur une Hill: climb=" + hill.getClimb() + ", slide=" + hill.getSlide());
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object dataA = fixtureA.getBody().getUserData();
        Object dataB = fixtureB.getBody().getUserData();
        System.out.println("Collision ended between: " + dataA + " and " + dataB);

        // Lorsque le joueur quitte le sol, on désactive le flag onGround
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
