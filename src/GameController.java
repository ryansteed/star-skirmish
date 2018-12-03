import javax.swing.SwingUtilities;

class GameController {
    private GameEngine engine;

    GameController() {
        this.engine = new GameEngine();
    }

    public void open() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                engine.createAndShowGUI();
            }
        });
    }
    public void start() {
        engine.start();
    }
}  
