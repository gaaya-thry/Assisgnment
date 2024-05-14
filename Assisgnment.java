/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package assisgnment;

/**
 *
 * @author DELL
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Assignment extends Application {

    private TableView<JobData> table = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String filePath = "C:\\Users\\DELL\\Downloads\\extracted_log";
        HashMap<String, Integer> jobsCreated = new HashMap<>();
        HashMap<String, Integer> jobsEnded = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("slurm_rpc_submit_batch_job")) {
                    String[] tokens = line.split(" ");
                    String timestamp = tokens[0];
                    jobsCreated.merge(timestamp.substring(0, 10), 1, Integer::sum);
                } else if (line.contains("_job_complete:")) {
                    String[] tokens = line.split(" ");
                    String timestamp = tokens[0];
                    jobsEnded.merge(timestamp.substring(0, 10), 1, Integer::sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Setting up the table columns
        TableColumn<JobData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<JobData, Integer> createdColumn = new TableColumn<>("Jobs Created");
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("jobsCreated"));

        TableColumn<JobData, Integer> endedColumn = new TableColumn<>("Jobs Ended");
        endedColumn.setCellValueFactory(new PropertyValueFactory<>("jobsEnded"));

        table.getColumns().add(dateColumn);
        table.getColumns().add(createdColumn);
        table.getColumns().add(endedColumn);

        // Populating the table with data
        for (String date : jobsCreated.keySet()) {
            int created = jobsCreated.getOrDefault(date, 0);
            int ended = jobsEnded.getOrDefault(date, 0);
            table.getItems().add(new JobData(date, created, ended));
        }

        VBox vbox = new VBox(table);
        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Job Data");
        primaryStage.show();
    }

    public static class JobData {
        private final String date;
        private final int jobsCreated;
        private final int jobsEnded;

        public JobData(String date, int jobsCreated, int jobsEnded) {
            this.date = date;
            this.jobsCreated = jobsCreated;
            this.jobsEnded = jobsEnded;
        }

        public String getDate() {
            return date;
        }

        public int getJobsCreated() {
            return jobsCreated;
        }

        public int getJobsEnded() {
            return jobsEnded;
        }
    }
}
