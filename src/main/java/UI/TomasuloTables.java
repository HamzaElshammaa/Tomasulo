package UI;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.TomasuloEngine;

import java.util.List;

public class TomasuloTables extends Application {

    private int cycle = 0;
    private Label cycleLabel;

    @Override
    public void start(Stage primaryStage) {
        // Main container
        VBox container = new VBox(20);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-background-color: #f4f4f9;");

        // Title
        Label title = new Label("Tomasulo Algorithm - Tables");
        title.setFont(Font.font("Arial", 24));
        title.setTextFill(Color.web("#333"));

        // Cycle Controls
        HBox cycleControls = new HBox(10);
        cycleControls.setAlignment(Pos.CENTER);
        cycleLabel = new Label("Cycle: 0");
        cycleLabel.setFont(Font.font("Arial", 18));
        cycleLabel.setTextFill(Color.web("#333"));
        Button nextCycleButton = new Button("Next Cycle");
        nextCycleButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 10px; -fx-border-radius: 4px; -fx-background-radius: 4px;");
        nextCycleButton.setOnMouseEntered(e -> nextCycleButton.setStyle("-fx-background-color: #0056b3; -fx-text-fill: white;"));
        nextCycleButton.setOnMouseExited(e -> nextCycleButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white;"));
        nextCycleButton.setOnAction(e -> incrementCycle());
        cycleControls.getChildren().addAll(cycleLabel, nextCycleButton);

        // Tables Container
        VBox tablesContainer = new VBox(20);

        // Create tables
        tablesContainer.getChildren().add(createInstructionQueueTable());

        tablesContainer.getChildren().add(createReservationStationTable("ADD/SUB Reservation Stations", TomasuloEngine.additionUnitStations));
        tablesContainer.getChildren().add(createReservationStationTable("MUL/DIV Reservation Stations", TomasuloEngine.multiplicationUnitStations));
        tablesContainer.getChildren().add(createBufferTable("Load Buffers", TomasuloEngine.loadUnitBuffer));
        tablesContainer.getChildren().add(createBufferTable("Store Buffers", TomasuloEngine.storeUnitBuffer));

        // Assemble layout
        container.getChildren().addAll(title, cycleControls, tablesContainer);

        // Scene and stage
        Scene scene = new Scene(container, 1000, 800);
        primaryStage.setTitle("Tomasulo Algorithm Tables");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void incrementCycle() {
        cycle++;
        cycleLabel.setText("Cycle: " + cycle);

        // Update data in tables from TomasuloEngine
        refreshTables();
    }

    private VBox createInstructionQueueTable() {
        VBox section = new VBox(10);
        Label sectionTitle = new Label("Instruction Queue");
        sectionTitle.setFont(Font.font("Arial", 18));
        sectionTitle.setTextFill(Color.web("#555"));

        // Create TableView for Instruction Queue
        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Define columns for the Instruction Queue
        String[] columns = {"Instruction Number", "Instruction"};
        for (String columnTitle : columns) {
            TableColumn<String[], String> column = new TableColumn<>(columnTitle);
            column.setCellValueFactory(data -> {
                int index = table.getColumns().indexOf(column);
                return new SimpleStringProperty(data.getValue()[index]);
            });
            table.getColumns().add(column);
        }

        // Populate the table with instruction queue data
        refreshInstructionQueueTable(table);

        section.getChildren().addAll(sectionTitle, table);
        return section;
    }

    private void refreshInstructionQueueTable(TableView<String[]> table) {
        table.getItems().clear();

        // Fetch instructions from TomasuloEngine's InstructionQueue
        List<String[]> instructionQueueData = TomasuloEngine.getInstructionQueueData();

        // Add each instruction to the table
        for (String[] row : instructionQueueData) {
            table.getItems().add(row);
        }
    }




    private VBox createReservationStationTable(String title, Object stationManager) {
        VBox section = new VBox(10);
        Label sectionTitle = new Label(title);
        sectionTitle.setFont(Font.font("Arial", 18));
        sectionTitle.setTextFill(Color.web("#555"));

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] columns = {"Name", "Busy", "Operation", "Vj", "Vk", "Qj", "Qk"};
        for (String columnTitle : columns) {
            TableColumn<String[], String> column = new TableColumn<>(columnTitle);
            column.setCellValueFactory(data -> {
                int index = table.getColumns().indexOf(column);
                return new SimpleStringProperty(data.getValue()[index]);
            });
            table.getColumns().add(column);
        }

        // Fetch and populate data from TomasuloEngine
        refreshReservationStationTable(table, stationManager);

        section.getChildren().addAll(sectionTitle, table);
        return section;
    }

    private VBox createBufferTable(String title, Object bufferManager) {
        VBox section = new VBox(10);
        Label sectionTitle = new Label(title);
        sectionTitle.setFont(Font.font("Arial", 18));
        sectionTitle.setTextFill(Color.web("#555"));

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] columns = {"Buffer Name", "Busy", "Address"};
        for (String columnTitle : columns) {
            TableColumn<String[], String> column = new TableColumn<>(columnTitle);
            column.setCellValueFactory(data -> {
                int index = table.getColumns().indexOf(column);
                return new SimpleStringProperty(data.getValue()[index]);
            });
            table.getColumns().add(column);
        }

        // Fetch and populate data from TomasuloEngine
        refreshBufferTable(table, bufferManager);

        section.getChildren().addAll(sectionTitle, table);
        return section;
    }

    private void refreshTables() {
        // Implement table refreshing logic here, fetching updated data from TomasuloEngine
    }

    private void refreshSimulationTable(TableView<String[]> table) {
        // Fetch simulation data from TomasuloEngine and populate the table
    }

    private void refreshReservationStationTable(TableView<String[]> table, Object stationManager) {
        // Fetch reservation station data from TomasuloEngine and populate the table
    }

    private void refreshBufferTable(TableView<String[]> table, Object bufferManager) {
        // Fetch buffer data from TomasuloEngine and populate the table
    }

    public static void main(String[] args) {
        launch(args);
    }
}