/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * A collection of enemies to challenge the player.
 */
class Wave {
    protected ArrayList<Alien> aliens;
    protected int number;
    protected int speed;
    protected static int[] tiers = { 30, 60, 90 };

    /**
     * Create a new wave of a certain size and with a certain base speed.
     * @param number The number of aliens in the wave.
     * @param speed The base speed of the wave (individual aliens will move faster by individual increments).
     */
    Wave(int number, int speed) {
        this.aliens = new ArrayList<Alien>();
        this.number = number;
        this.speed = speed;
        addNewAliens(number);
    }

    /**
     * Spawn `n` new aliens.
     * @param n The number of aliens to spawn.
     */
    private void addNewAliens(int n) {
        for (int i = 0; i < n; i++) {
            trySpawn(n);
        }
    }

    /**
     * Try different spawn positions until the alien can be spawned, then spawn one.
     * @param n The number of aliens in total being spawned - needed to choose spaced
     * out positions.
     */
    private void trySpawn(int n) {
        Alien alien = spawn(n);
        if (isValidSpawnPosition(alien)) {
            aliens.add(alien);
            return;
        }
        trySpawn(n);
    }

    /**
     * Check if this spawn position is in bounds and not intersecting another enemy.
     * @param spawned The alien to be spawned.
     * @return Whether or not the spawn position is valid.
     */
    private boolean isValidSpawnPosition(Alien spawned) {
        boolean intersection = false;
        for (Alien other : aliens) {
            if (spawned.intersects(other)) {
                intersection = true;
            }
        }
        return !intersection;
    }

    /**
     * A special iterator to run through the aliens in a wave.
     * Using an iterator allows efficient deletion when checking collisions or assessing
     * whether an alien has cleared.
     * @return
     */
    protected Iterator<Alien> iterAliens() {
        return aliens.iterator();
    }

    /**
     * Spawn the alien with a random tier (large, medium, small) at a random position based on the total number being spawned.
     * @param n The number of aliens being spawned in total.
     * @return The new Alien.
     */
    private Alien spawn(int n) {
        int tier = new Random().nextInt(3);
        int initX = (int) (Math.random() * (GameEngine.size.getWidth() - tiers[tier]));
        int initY = -2 * (int) (Math.random() * n * GameEngine.size.getHeight() / 10 + GameEngine.size.getHeight() / 10);
        Euclidean alienInit = new Euclidean(initX, initY);
        return new Alien(alienInit, tier, speed, GameEngine.prop);
    }

    /**
     * @return Whether all aliens in this wave have been cleared.
     */
    protected boolean cleared() {
        return aliens.size() == 0;
    }

    /**
     * Check to see if any aliens have been cleared and remove them.
     * @return The score accumulated from those cleared aliens. Collided aliens do not count.
     */
    protected int update() {
        int score = 0;
        // To permit removal in place
        // https://www.geeksforgeeks.org/remove-element-arraylist-java/
        Iterator<Alien> iter = iterAliens();
        while (iter.hasNext()) {
            Alien current = iter.next();
            // if alien past screen, delete and add points
            if (current.engine.getY() > GameEngine.yBound) {
                score += current.value;
                // System.out.println("AlienAttack:164> Removing alien due to out of bounds");
                iter.remove();
            }
        }
        return score;
    }
}