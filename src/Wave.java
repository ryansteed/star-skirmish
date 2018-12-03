import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class Wave {
    protected ArrayList<Alien> aliens;
    protected int number;

    Wave(int number) {
        this.aliens = new ArrayList<Alien>();
        this.number = number;
        addNewAliens(number);
    }

    private void addNewAliens(int n) {
        for (int i = 0; i < n; i++) {
            trySpawn(n);
        }
    }

    private void trySpawn(int n) {
        Alien alien = spawn(n);
        if (isValidSpawnPosition(alien)) {
            aliens.add(alien);
            return;
        }
        trySpawn(n);
    }

    private boolean isValidSpawnPosition(Alien spawned) {
        boolean intersection = false;
        for (Alien other : aliens) {
            if (spawned.intersects(other)) {
                intersection = true;
            }
        }
        return !intersection;
    }

    protected Iterator<Alien> iterAliens() {
        return aliens.iterator();
    }

    private Alien spawn(int n) {
        int initX = (int) (Math.random() * GameEngine.size.getWidth());
        int initY = -(int) (Math.random() * n * GameEngine.size.getHeight() / 10 + GameEngine.size.getHeight() / 10);
        Euclidean alienInit = new Euclidean(initX, initY);
        // TODO: use normal dist to choose tier level based on wave difficulty
        // https://stackoverflow.com/questions/6011943/java-normal-distribution
        return new Alien(alienInit, new Random().nextInt(3), GameEngine.prop);
    }

    protected boolean cleared() {
        return aliens.size() == 0;
    }

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