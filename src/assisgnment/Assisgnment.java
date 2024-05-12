/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package assisgnment;

/**
 *
 * @author DELL
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Assisgnment {
    public static void main(String[] args) {
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

        // Display results in a table format
        System.out.println("Date\tJobs Created\tJobs Ended");
        for (String date : jobsCreated.keySet()) {
            int created = jobsCreated.getOrDefault(date, 0);
            int ended = jobsEnded.getOrDefault(date, 0);
            System.out.println(date + "\t" + created + "\t\t" + ended);
        }
    }
}

