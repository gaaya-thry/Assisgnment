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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Map;

public class Assignment extends Application {

    private TableView<JobData> table = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        String filePath = "C:\\Users\\DELL\\Downloads\\extracted_log";
        Map<String, JobCounter.JobCounts> jobCountsByDate = JobCounter.countJobsByDate(filePath);

        // Setting up the table columns (same as before)
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

        TableColumn<JobData, Integer> errorColumn = new TableColumn<>("Errors");
        errorColumn.setCellValueFactory(new PropertyValueFactory<>("jobErrors"));
        
        TableColumn<JobData, Integer> errorJobsColumn = new TableColumn<>("Job Errors");
        errorJobsColumn.setCellValueFactory(new PropertyValueFactory<>("jobErrors"));

        TableColumn<JobData, Integer> errorUsersColumn = new TableColumn<>("User Errors");
        errorUsersColumn.setCellValueFactory(new PropertyValueFactory<>("userErrors"));
 
        TableColumn<JobData, Long> avgTimeColumn = new TableColumn<>("Avg Execution Time (ms)");
        avgTimeColumn.setCellValueFactory(new PropertyValueFactory<>("avgExecutionTime"));

        table.getColumns().add(dateColumn);
        table.getColumns().add(createdColumn);
        table.getColumns().add(endedColumn);
        table.getColumns().add(opteronColumn);
        table.getColumns().add(epycColumn);
        table.getColumns().add(gpuColumn);
        table.getColumns().add(errorColumn);
        table.getColumns().add(errorJobsColumn);
        table.getColumns().add(errorUsersColumn);
        table.getColumns().add(avgTimeColumn);
        

        // Populating the table with data
        for (String date : jobCountsByDate.keySet()) {
            JobCounter.JobCounts counts = jobCountsByDate.get(date);
            table.getItems().add(new JobData(date, counts.getJobsCreated(), counts.getJobsEnded(), 
                                             counts.getOpteronJobs(), counts.getEpycJobs(), counts.getGpuJobs(), 
                                             counts.getJobErrors(), counts.getAvgExecutionTime()));
        }
        // Creating the charts
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> jobsChart = new BarChart<>(xAxis, yAxis);
        jobsChart.setTitle("Jobs Overview");

        XYChart.Series<String, Number> createdSeries = new XYChart.Series<>();
        createdSeries.setName("Jobs Created");

        XYChart.Series<String, Number> endedSeries = new XYChart.Series<>();
        endedSeries.setName("Jobs Ended");

        XYChart.Series<String, Number> opteronSeries = new XYChart.Series<>();
        opteronSeries.setName("Opteron Jobs");

        XYChart.Series<String, Number> epycSeries = new XYChart.Series<>();
        epycSeries.setName("EPYC Jobs");

        XYChart.Series<String, Number> gpuSeries = new XYChart.Series<>();
        gpuSeries.setName("GPU Jobs");

        for (String date : jobCountsByDate.keySet()) {
            JobCounter.JobCounts counts = jobCountsByDate.get(date);
            createdSeries.getData().add(new XYChart.Data<>(date, counts.getJobsCreated()));
            endedSeries.getData().add(new XYChart.Data<>(date, counts.getJobsEnded()));
            opteronSeries.getData().add(new XYChart.Data<>(date, counts.getOpteronJobs()));
            epycSeries.getData().add(new XYChart.Data<>(date, counts.getEpycJobs()));
            gpuSeries.getData().add(new XYChart.Data<>(date, counts.getGpuJobs()));
        }

        jobsChart.getData().addAll(createdSeries, endedSeries, opteronSeries, epycSeries, gpuSeries);

        // Creating labels for overall totals
        Label overallTotalsLabel = new Label("Overall Totals:");
        Label totalCreatedLabel = new Label("Total Jobs Created: " + getTotalJobsCreated(jobCountsByDate));
        Label totalEndedLabel = new Label("Total Jobs Ended: " + getTotalJobsEnded(jobCountsByDate));
        Label totalOpteronLabel = new Label("Total Opteron Jobs: " + getTotalOpteronJobs(jobCountsByDate));
        Label totalEpycLabel = new Label("Total EPYC Jobs: " + getTotalEpycJobs(jobCountsByDate));
        Label totalGpuLabel = new Label("Total GPU Jobs: " + getTotalGpuJobs(jobCountsByDate));

        // Adding labels to the layout
        VBox totalsBox = new VBox(overallTotalsLabel, totalCreatedLabel, totalEndedLabel, totalOpteronLabel, totalEpycLabel, totalGpuLabel);

        // Adding table and charts to the layout
        VBox vbox = new VBox(table, jobsChart);
        HBox root = new HBox(vbox, totalsBox);
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Job Data");
        primaryStage.show();
    }

    // Methods to calculate overall totals
    private int getTotalJobsCreated(Map<String, JobCounter.JobCounts> jobCountsByDate) {
        int total = 0;
        for (JobCounter.JobCounts counts : jobCountsByDate.values()) {
            total += counts.getJobsCreated();
        }
        return total;
    }

    private int getTotalJobsEnded(Map<String, JobCounter.JobCounts> jobCountsByDate) {
        int total = 0;
        for (JobCounter.JobCounts counts : jobCountsByDate.values()) {
            total += counts.getJobsEnded();
        }
        return total;
    }

    private int getTotalOpteronJobs(Map<String, JobCounter.JobCounts> jobCountsByDate) {
        int total = 0;
        for (JobCounter.JobCounts counts : jobCountsByDate.values()) {
            total += counts.getOpteronJobs();
        }
        return total;
    }

    private int getTotalEpycJobs(Map<String, JobCounter.JobCounts> jobCountsByDate) {
        int total = 0;
        for (JobCounter.JobCounts counts : jobCountsByDate.values()) {
            total += counts.getEpycJobs();
        }
        return total;
    }

    private int getTotalGpuJobs(Map<String, JobCounter.JobCounts> jobCountsByDate) {
        int total = 0;
        for (JobCounter.JobCounts counts : jobCountsByDate.values()) {
            total += counts.getGpuJobs();
        }
        return total;
    }
    

    public static class JobData {
        private final String date;
        private final int jobsCreated;
        private final int jobsEnded;
        private final int opteronJobs;
        private final int epycJobs;
        private final int gpuJobs;
        private final int jobErrors;
        private final long avgExecutionTime;
        private final int userErrors;

        public JobData(String date, int jobsCreated, int jobsEnded, int opteronJobs, int epycJobs, int gpuJobs, int jobErrors, long avgExecutionTime) {
            this.date = date;
            this.jobsCreated = jobsCreated;
            this.jobsEnded = jobsEnded;
            this.opteronJobs = opteronJobs;
            this.epycJobs = epycJobs;
            this.gpuJobs = gpuJobs;
            this.jobErrors = jobErrors;
            this.avgExecutionTime = avgExecutionTime;
            this.userErrors = 0; // Assuming default value for userErrors
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

        public int getOpteronJobs() {
            return opteronJobs;
        }

        public int getEpycJobs() {
            return epycJobs;
        }

        public int getGpuJobs() {
            return gpuJobs;
        }

        public int getJobErrors() {
            return jobErrors;
        }

        public long getAvgExecutionTime() {
            return avgExecutionTime;
        }
        
        public int getUserErrors() {
            return userErrors;
        }
    }
}

