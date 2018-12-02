import java.lang.Math;

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
    protected Physics(Euclidean p, Euclidean v, Euclidean a) {
        this.p = p;
        this.v = v;
        this.a = a;
        maxSpeed = null;
    }
    protected void setMaxSpeed(int speed) {
        maxSpeed = speed;
    } 
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