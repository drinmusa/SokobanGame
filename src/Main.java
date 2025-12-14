import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {

    @Override
    public void start(Stage stage) {
        GameUI gameUI = new GameUI();

        Scene scene = new Scene(gameUI.getRoot(),800,600);
        stage.setTitle("Sokoban FX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
