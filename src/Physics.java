class Physics {
    protected Euclidean p;
    protected Euclidean v;
    protected Euclidean a;


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
    }
    
    protected Euclidean move() {
        return p;
    }
    protected int getX() {
        return p.x;
    }
    protected int getY() {
        return p.y;
    }
}