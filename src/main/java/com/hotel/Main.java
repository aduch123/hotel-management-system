package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Hotel Management System - Starting...");

        // Load login screen using file path (proven to work)
        File loginFile = new File("src/main/resources/fxml/login.fxml");
        System.out.println("Loading: " + loginFile.getAbsolutePath());

        Parent root = FXMLLoader.load(loginFile.toURI().toURL());
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Hotel Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();

        System.out.println("Application started successfully!");
    }

    public static void main(String[] args) {
        launch(args);
    }
}