package UI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.TomasuloEngine;

public class TomasuloInput extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #f4f4f9;");

        Label title = new Label("Tomasulo Algorithm - Input");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.web("#333"));

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);

        // Input fields
        TextField cacheSizeField = addFormRow(form, 0, "Cache Size (Bytes):", "10");
        TextField blockSizeField = addFormRow(form, 1, "Block Size (Bytes):", "10");
        TextField addLatencyField = addFormRow(form, 2, "Add/Sub Latency:", "1");
        TextField mulLatencyField = addFormRow(form, 3, "Mul/Div Latency:", "1");
        TextField loadLatencyField = addFormRow(form, 4, "Load Latency:", "1");
        TextField storeLatencyField = addFormRow(form, 5, "Store Latency:", "1");
        TextField addStationSizeField = addFormRow(form, 6, "Add/Sub RS Size:", "3");
        TextField mulStationSizeField = addFormRow(form, 7, "Mul/Div RS Size:", "3");
        TextField loadStationSizeField = addFormRow(form, 8, "Load Buffer Size:", "3");
        TextField storeStationSizeField = addFormRow(form, 9, "Store Buffer Size:", "3");

        Button submitButton = new Button("Submit");
        submitButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;");
        submitButton.setOnAction(e -> {
            // Initialize the model.TomasuloEngine with inputs
            TomasuloEngine.blockSize = Integer.parseInt(blockSizeField.getText());
            TomasuloEngine.cacheSize = Integer.parseInt(cacheSizeField.getText());
            TomasuloEngine.additionUnitLatency = Integer.parseInt(addLatencyField.getText());
            TomasuloEngine.multiplicationUnitLatency = Integer.parseInt(mulLatencyField.getText());
            TomasuloEngine.loadUnitLatency = Integer.parseInt(loadLatencyField.getText());
            TomasuloEngine.storeUnitLatency = Integer.parseInt(storeLatencyField.getText());
            TomasuloEngine.additionUnitSize = Integer.parseInt(addStationSizeField.getText());
            TomasuloEngine.multiplicationUnitSize = Integer.parseInt(mulStationSizeField.getText());
            TomasuloEngine.loadUnitSize = Integer.parseInt(loadStationSizeField.getText());
            TomasuloEngine.storeUnitSize = Integer.parseInt(storeStationSizeField.getText());

            // Start the TomasuloTables screen
            TomasuloEngine.init();


            try {
                new TomasuloTables().start(new Stage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Close the input window
            primaryStage.close();
        });

        container.getChildren().addAll(title, form, submitButton);

        Scene scene = new Scene(container, 800, 600);
        primaryStage.setTitle("Tomasulo Algorithm Input");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField addFormRow(GridPane form, int row, String labelText, String defaultValue) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", 14));
        form.add(label, 0, row);

        TextField textField = new TextField(defaultValue);
        textField.setPrefWidth(300);
        form.add(textField, 1, row);

        return textField;
    }

    public static void main(String[] args) {
        launch(args);
    }
}