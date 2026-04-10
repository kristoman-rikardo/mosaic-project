package mosaicproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MosaicApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/mosaicproject/App.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1100, 800);
        primaryStage.setTitle("Mosaic Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        System.out.println("Starting application!");
        launch(args);
    }
}