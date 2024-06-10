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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Assignment extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<JobData> tableView = new TableView<>();
        PieChart pieChart = new PieChart();

        TableColumn<JobData, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<JobData, Integer> createdColumn = new TableColumn<>("Jobs Created");
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("jobsCreated"));

        TableColumn<JobData, Integer> endedColumn = new TableColumn<>("Jobs Ended");
        endedColumn.setCellValueFactory(new PropertyValueFactory<>("jobsEnded"));

        TableColumn<JobData, Integer> opteronColumn = new TableColumn<>("Opteron Jobs");
        opteronColumn.setCellValueFactory(new PropertyValueFactory<>("opteronJobs"));

        TableColumn<JobData, Integer> epycColumn = new TableColumn<>("EPYC Jobs");
        epycColumn.setCellValueFactory(new PropertyValueFactory<>("epycJobs"));

        TableColumn<JobData, Integer> gpuColumn = new TableColumn<>("GPU Jobs");
        gpuColumn.setCellValueFactory(new PropertyValueFactory<>("gpuJobs"));

        TableColumn<JobData, Integer> errorColumn = new TableColumn<>("Job Errors");
        errorColumn.setCellValueFactory(new PropertyValueFactory<>("jobErrors"));

        TableColumn<JobData, Long> avgExecutionTimeColumn = new TableColumn<>("Avg Execution Time");
        avgExecutionTimeColumn.setCellValueFactory(new PropertyValueFactory<>("avgExecutionTime"));

        TableColumn<JobData, Double> successRateColumn = new TableColumn<>("Job Success Rate");
        successRateColumn.setCellValueFactory(new PropertyValueFactory<>("jobSuccessRate"));

        TableColumn<JobData, List<String>> userErrorMessagesColumn = new TableColumn<>("User Errors");
        userErrorMessagesColumn.setCellValueFactory(new PropertyValueFactory<>("userErrorMessages"));

        tableView.getColumns().addAll(dateColumn, createdColumn, endedColumn, opteronColumn, epycColumn, gpuColumn, errorColumn, avgExecutionTimeColumn, successRateColumn, userErrorMessagesColumn);

        Map<String, JobCounter.JobCounts> jobCountsByDate = JobCounter.countJobsByDate("C:\\Users\\DELL\\Downloads\\extracted_log");
        ObservableList<JobData> jobDataList = FXCollections.observableArrayList();

        for (Map.Entry<String, JobCounter.JobCounts> entry : jobCountsByDate.entrySet()) {
            String date = entry.getKey();
            JobCounter.JobCounts counts = entry.getValue();
            List<String> userErrorMessages = counts.getUserErrorMessages() != null ? counts.getUserErrorMessages() : new ArrayList<>();

            double successRate = (counts.getJobsCreated() > 0) ? ((double) counts.getJobsEnded() / counts.getJobsCreated()) * 100 : 0;

            JobData jobData = new JobData(date, counts.getJobsCreated(), counts.getJobsEnded(), counts.getOpteronJobs(),
                    counts.getEpycJobs(), counts.getGpuJobs(), counts.getJobErrors(),
                    counts.getAvgExecutionTime(), successRate, userErrorMessages);

            jobDataList.add(jobData);
        }

        tableView.setItems(jobDataList);

        int totalJobsCreated = 0;
        int totalJobsEnded = 0;
        int totalOpteronJobs = 0;
        int totalEpycJobs = 0;
        int totalGpuJobs = 0;
        int totalJobErrors = 0;

        for (JobData jobData : jobDataList) {
            totalJobsCreated += jobData.getJobsCreated();
            totalJobsEnded += jobData.getJobsEnded();
            totalOpteronJobs += jobData.getOpteronJobs();
            totalEpycJobs += jobData.getEpycJobs();
            totalGpuJobs += jobData.getGpuJobs();
            totalJobErrors += jobData.getJobErrors();
        }

        pieChart.getData().addAll(
                new PieChart.Data("Jobs Created", totalJobsCreated),
                new PieChart.Data("Jobs Ended", totalJobsEnded),
                new PieChart.Data("Opteron Jobs", totalOpteronJobs),
                new PieChart.Data("EPYC Jobs", totalEpycJobs),
                new PieChart.Data("GPU Jobs", totalGpuJobs),
                new PieChart.Data("Job Errors", totalJobErrors)
        );

        BorderPane root = new BorderPane();
        VBox vbox = new VBox(tableView, pieChart);
        root.setCenter(vbox);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Job Data Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

