package com.upf.bastionbreaker.controller.input;

import com.upf.bastionbreaker.controller.gameplay.TransformationManager;
import com.upf.bastionbreaker.model.entities.Player;

public class PlayerController {
    /**
     * Gère les actions du joueur telles que le tir, le saut et la transformation.
     * Cette méthode peut être appelée depuis GameScreen pour traiter les entrées.
     *
     * @param player      Le joueur dont on doit traiter les actions.
     * @param delta       Le temps écoulé depuis la dernière frame.
     * @param jumpPressed Indique si l'action de saut est déclenchée.
     * @param modePressed Indique si l'action de changement de mode est déclenchée.
     * @param shootPressed Indique si l'action de tir est déclenchée.
     */
    public void processInput(Player player, float delta, boolean jumpPressed, boolean modePressed, boolean shootPressed) {
        if (jumpPressed) {
            player.jump();
        }
        if (modePressed) {
            // Utilise TransformationManager pour basculer entre Tank et Robot.
            TransformationManager.getInstance().transform(player);
            // Un léger ajustement de position pour éviter d'éventuels problèmes de collision.
            player.setPosition(player.getX(), player.getY() + 0.1f);
        }
        if (shootPressed) {
            // Implémenter ici la logique de tir
            System.out.println("Player tire !");
            // Par exemple, si vous avez une méthode shoot() dans Player, vous pourriez l'appeler ici.
            // player.shoot();
        }
    }
}
