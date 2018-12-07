/**
 * @author Ryan Steed
 * @version 1.0
 * @since 2018-12-06
 */

package com.rbsteed.app;

import java.lang.Math;

/**
 * A physics engine for calculating object position at a given iteration.
 */
class Physics {
    protected Euclidean p;
    protected Euclidean v;
    protected Euclidean a;
    private Integer maxSpeed;

    // constructor chaining
    protected Physics(Euclidean p) {
        this(p, new Euclidean(0, 0));
    }
    protected Physics(Euclidean p, Euclidean v) {
        this(p, v, new Euclidean(0, 0));
    }
    /**
     * Create a new engine with a starting position, velocity, and acceleration.
     * @param p Position.
     * @param v Velocity.
     * @param a Acceleration.
     */
    protected Physics(Euclidean p, Euclidean v, Euclidean a) {
        this.p = p;
        this.v = v;
        this.a = a;
        maxSpeed = null;
    }
    protected void setMaxSpeed(Integer speed) {
        maxSpeed = speed;
    } 
    /**
     * Iterate the object position, velocity, and acceleration with a Eulerian parametric calculation.
     * @return The new position.
     */
    protected Euclidean move() {
        // velocity change only if under max object speed
        if (maxSpeed == null || maxSpeed > Math.sqrt(Math.pow((v.x + a.x), 2) + Math.pow((v.y + a.y), 2))) {
            v.x += a.x;
            v.y += a.y;
        }
        // position cahnge
        p.x += v.x;
        p.y += v.y;
        return p;
    }
    protected int getX() {
        return p.x;
    }
    protected int getY() {
        return p.y;
    }
}

/**
 * A convenient container class for any physics data with components. In our 2d (Euclidean) environment,
 * position, velocity, and acceleration all have x and y components.
 * @see Physics
 */
class Euclidean {
    protected int x;
    protected int y;

    Euclidean(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}