package com.github.runiku.labelcut;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("main.fxml"));
        Parent root = (Parent) loader.load();
        Controller controller = (Controller) loader.getController();
        controller.setStage(primaryStage);
        List<String> args = getParameters().getRaw();
        if (args.size() > 0) {
            System.out.println(args.get(0));
            controller.setCurrentFile(args.get(0));
        }
        controller.init();

        primaryStage.setTitle("LabeCut");
        primaryStage.setScene(new Scene(root, 600, 450));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
