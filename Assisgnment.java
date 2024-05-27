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
        HashMap<String, HashMap<String, Integer>> jobsByPartition = new HashMap<>();
        HashMap<String, Integer> jobErrors = new HashMap<>();
        HashMap<String, Long> totalExecutionTime = new HashMap<>();
        HashMap<String, Long> jobStartTimes = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(" ");
                String timestamp = tokens[0].substring(1, tokens[0].length() - 1); // Removing the brackets
                String date = timestamp.substring(0, 10);

                if (line.contains("_slurm_rpc_submit_batch_job")) {
                    jobsCreated.merge(date, 1, Integer::sum);
                    String jobId = getJobId(line);
                    jobStartTimes.put(jobId, parseTimeToMillis(timestamp));
                } else if (line.contains("_job_complete:")) {
                    jobsEnded.merge(date, 1, Integer::sum);
                    String jobId = getJobId(line);
                    if (jobStartTimes.containsKey(jobId)) {
                        long startTime = jobStartTimes.get(jobId);
                        long endTime = parseTimeToMillis(timestamp);
                        long executionTime = endTime - startTime;
                        totalExecutionTime.merge(date, executionTime, Long::sum);
                    }
                } else if (line.contains("Partition=cpu-opteron") || line.contains("Partition=cpu-epyc") || line.contains("Partition=gpu")) {
                    String partition = tokens[tokens.length - 1].split("=")[1];
                    jobsByPartition.computeIfAbsent(date, k -> new HashMap<>()).merge(partition, 1, Integer::sum);
                } else if (line.contains("error: This association")) {
                    jobErrors.merge(date, 1, Integer::sum);
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

        TableColumn<JobData, Integer> opteronColumn = new TableColumn<>("Opteron Jobs");
        opteronColumn.setCellValueFactory(new PropertyValueFactory<>("opteronJobs"));

        TableColumn<JobData, Integer> epycColumn = new TableColumn<>("EPYC Jobs");
        epycColumn.setCellValueFactory(new PropertyValueFactory<>("epycJobs"));

        TableColumn<JobData, Integer> gpuColumn = new TableColumn<>("GPU Jobs");
        gpuColumn.setCellValueFactory(new PropertyValueFactory<>("gpuJobs"));

        TableColumn<JobData, Integer> errorColumn = new TableColumn<>("Errors");
        errorColumn.setCellValueFactory(new PropertyValueFactory<>("jobErrors"));

        TableColumn<JobData, Long> avgTimeColumn = new TableColumn<>("Avg Execution Time (ms)");
        avgTimeColumn.setCellValueFactory(new PropertyValueFactory<>("avgExecutionTime"));

        table.getColumns().add(dateColumn);
        table.getColumns().add(createdColumn);
        table.getColumns().add(endedColumn);
        table.getColumns().add(opteronColumn);
        table.getColumns().add(epycColumn);
        table.getColumns().add(gpuColumn);
        table.getColumns().add(errorColumn);
        table.getColumns().add(avgTimeColumn);

        // Populating the table with data
        for (String date : jobsCreated.keySet()) {
            int created = jobsCreated.getOrDefault(date, 0);
            int ended = jobsEnded.getOrDefault(date, 0);
            HashMap<String, Integer> partitionMap = jobsByPartition.getOrDefault(date, new HashMap<>());
            int opteronJobs = partitionMap.getOrDefault("cpu-opteron", 0);
            int epycJobs = partitionMap.getOrDefault("cpu-epyc", 0);
            int gpuJobs = partitionMap.getOrDefault("gpu", 0);
            int errors = jobErrors.getOrDefault(date, 0);
            long totalExecTime = totalExecutionTime.getOrDefault(date, 0L);
            int jobCounts = jobsEnded.getOrDefault(date, 0); // Using jobsEnded as job count for average calculation
            long avgExecTime = jobCounts > 0 ? Math.max(totalExecTime / jobCounts, 0) : 0;

            // Ensure GPU jobs are included and not displayed as 0 incorrectly
            table.getItems().add(new JobData(date, created, ended, opteronJobs, epycJobs, gpuJobs, errors, avgExecTime));
        }

        VBox vbox = new VBox(table);
        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Job Data");
        primaryStage.show();
    }

    private static long parseTimeToMillis(String timestamp) {
        String[] timeParts = timestamp.split("[T:.]");
        long hours = Long.parseLong(timeParts[1]) * 3600000;
        long minutes = Long.parseLong(timeParts[2]) * 60000;
        long seconds = Long.parseLong(timeParts[3]) * 1000;
        long millis = Long.parseLong(timeParts[4]);
        return hours + minutes + seconds + millis;
    }

    private static String getJobId(String line) {
        String[] tokens = line.split(" ");
        for (String token : tokens) {
            if (token.startsWith("JobId=")) {
                return token.split("=")[1];
            }
        }
        return null;
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

        public JobData(String date, int jobsCreated, int jobsEnded, int opteronJobs, int epycJobs, int gpuJobs, int jobErrors, long avgExecutionTime) {
            this.date = date;
            this.jobsCreated = jobsCreated;
            this.jobsEnded = jobsEnded;
            this.opteronJobs = opteronJobs;
            this.epycJobs = epycJobs;
            this.gpuJobs = gpuJobs;
            this.jobErrors = jobErrors;
            this.avgExecutionTime = avgExecutionTime;
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
    }
}
