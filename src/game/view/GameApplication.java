package game.view;

import javafx.application.Application;
import javafx.stage.Stage;


public class GameApplication extends Application {

    private static Stage primaryStage;

    
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("DoorDasH: Scare vs Laugh Touchdown");
        stage.setResizable(false);
        showStartView();
        stage.show();
    }

    
    public static void showStartView() {
        StartView sv = new StartView(primaryStage);
        primaryStage.setScene(sv.getScene());
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void main(String[] args) {
        launch(args);
    }
}