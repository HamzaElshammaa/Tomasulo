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
import model.*;
import javafx.scene.Node;

import java.util.List;

public class TomasuloTables extends Application {

    private int cycle = 0;
    private Label cycleLabel;
    private VBox tablesContainer;

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

        // Tables Container (with ScrollPane)
        tablesContainer = new VBox(20);
        ScrollPane scrollPane = new ScrollPane(tablesContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: #f4f4f9;");

        // Create tables
        tablesContainer.getChildren().add(createInstructionQueueTable());
        tablesContainer.getChildren().add(createReservationStationTable("ADD/SUB Reservation Stations", TomasuloEngine.additionUnitStations));
        tablesContainer.getChildren().add(createReservationStationTable("MUL/DIV Reservation Stations", TomasuloEngine.multiplicationUnitStations));
        tablesContainer.getChildren().add(createBufferTable("Load Buffers", TomasuloEngine.loadUnitBuffer));
        tablesContainer.getChildren().add(createBufferTable("Store Buffers", TomasuloEngine.storeUnitBuffer));
        tablesContainer.getChildren().add(createRegisterFileTable("Floating Point Registers", TomasuloEngine.fp_registerFile));
        tablesContainer.getChildren().add(createRegisterFileTable("Integer Registers", TomasuloEngine.int_registerFile));

        // Assemble layout
        container.getChildren().addAll(title, cycleControls, scrollPane);

        // Scene and stage
        Scene scene = new Scene(container, 1200, 900); // Increased window size
        primaryStage.setTitle("Tomasulo Algorithm Tables");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void incrementCycle() {
        cycle++;
        cycleLabel.setText("Cycle: " + cycle);

        // Update data in tables from TomasuloEngine
        TomasuloEngine.runCycle();
        refreshTables();
    }

    private VBox createInstructionQueueTable() {
        VBox section = new VBox(10);
        Label sectionTitle = new Label("Instruction Queue");
        sectionTitle.setFont(Font.font("Arial", 18));
        sectionTitle.setTextFill(Color.web("#555"));

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] columns = {"Instruction Number", "Instruction"};
        for (String columnTitle : columns) {
            TableColumn<String[], String> column = new TableColumn<>(columnTitle);
            column.setCellValueFactory(data -> {
                int index = table.getColumns().indexOf(column);
                return new SimpleStringProperty(data.getValue()[index]);
            });
            table.getColumns().add(column);
        }

        refreshInstructionQueueTable(table);

        section.getChildren().addAll(sectionTitle, table);
        return section;
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

        refreshBufferTable(table, bufferManager);

        section.getChildren().addAll(sectionTitle, table);
        return section;
    }

    private VBox createRegisterFileTable(String title, RegisterFile registerFile) {
        VBox section = new VBox(10);
        Label sectionTitle = new Label(title);
        sectionTitle.setFont(Font.font("Arial", 18));
        sectionTitle.setTextFill(Color.web("#555"));

        TableView<String[]> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        String[] columns = {"Register", "Q", "Value"};
        for (String columnTitle : columns) {
            TableColumn<String[], String> column = new TableColumn<>(columnTitle);
            column.setCellValueFactory(data -> {
                int index = table.getColumns().indexOf(column);
                return new SimpleStringProperty(data.getValue()[index]);
            });
            table.getColumns().add(column);
        }

        refreshRegisterFileTable(table, registerFile);
        section.getChildren().addAll(sectionTitle, table);
        return section;
    }

    private void refreshTables() {
        for (Node node : tablesContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox section = (VBox) node;
                for (Node child : section.getChildren()) {
                    if (child instanceof TableView) {
                        TableView<String[]> table = (TableView<String[]>) child;
                        String title = ((Label) section.getChildren().get(0)).getText();

                        switch (title) {
                            case "Instruction Queue":
                                refreshInstructionQueueTable(table);
                                break;
                            case "ADD/SUB Reservation Stations":
                                refreshReservationStationTable(table, TomasuloEngine.additionUnitStations);
                                break;
                            case "MUL/DIV Reservation Stations":
                                refreshReservationStationTable(table, TomasuloEngine.multiplicationUnitStations);
                                break;
                            case "Load Buffers":
                                refreshBufferTable(table, TomasuloEngine.loadUnitBuffer);
                                break;
                            case "Store Buffers":
                                refreshBufferTable(table, TomasuloEngine.storeUnitBuffer);
                                break;
                            case "Floating Point Registers":
                                refreshRegisterFileTable(table, TomasuloEngine.fp_registerFile);
                                break;
                            case "Integer Registers":
                                refreshRegisterFileTable(table, TomasuloEngine.int_registerFile);
                                break;
                        }
                    }
                }
            }
        }
    }

    private void refreshInstructionQueueTable(TableView<String[]> table) {
        table.getItems().clear();
        List<String[]> instructionQueueData = TomasuloEngine.getInstructionQueueData();
        for (String[] row : instructionQueueData) {
            table.getItems().add(row);
        }
    }

    private void refreshReservationStationTable(TableView<String[]> table, Object stationManager) {
        table.getItems().clear(); // Clear the table before adding updated data

        ReservationStationManager manager = (ReservationStationManager) stationManager;
        ReservationStation[] stations = manager.getStations();

        for (ReservationStation rs : stations) {
            String[] row = {
                    rs.getTag() != null ? rs.getTag().toString() : "",
                    String.valueOf(rs.isBusy()),
                    rs.getOperation() != null ? rs.getOperation().toString() : "",
                    rs.getVj() != -1 ? String.valueOf(rs.getVj()) : "",
                    rs.getVk() != -1 ? String.valueOf(rs.getVk()) : "",
                    rs.getQj() != null ? rs.getQj().toString() : "",
                    rs.getQk() != null ? rs.getQk().toString() : ""
            };

            if (rs.isBusy() || rs.getOperation() != null || rs.getQj() != null || rs.getQk() != null) {
                table.getItems().add(row);
            }
        }
    }

    private void refreshBufferTable(TableView<String[]> table, Object bufferManager) {
        table.getItems().clear();
        BufferManager manager = (BufferManager) bufferManager;
        Buffer[] buffers = manager.getBuffers();
        for (Buffer buffer : buffers) {
            String[] row = {
                    buffer.getTag().toString(),
                    String.valueOf(buffer.isBusy()),
                    buffer.getAddressTag() != null ? buffer.getAddressTag().toString() : ""
            };
            table.getItems().add(row);
        }
    }

    private void refreshRegisterFileTable(TableView<String[]> table, RegisterFile registerFile) {
        table.getItems().clear();

        for (int i = 0; i < registerFile.getRegisters().length; i++) {
            Q value = registerFile.getRegisters()[i];
            String[] row = {
                    "R" + i,
                    value.type != Q.DataType.R ? value.type.name() : "",
                    String.valueOf(value.value)
            };
            table.getItems().add(row);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
