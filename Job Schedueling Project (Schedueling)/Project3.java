/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Tony Mendoza
 */
public class Project3 {

    static LinkedList<Job> jobs = new LinkedList<>(); // stores the jobs we will be working on

    // The main program
    public static void main(String[] args) {
        // Add all the jobs, randomize the data
        for (int i = 0; i < 10; i++) {
            jobs.add(new Job(i));
            jobs.get(i).setArrivalTime((int) (Math.random() * 10));
            jobs.get(i).setBurstTime((int) (Math.random() * 20) + 1);
            jobs.get(i).setPriority((int) (Math.random() * 10) + 1);
        }

        Reset(); // Put jobs in order of arrival, and make sure everything is set to what they should be for hte program.

        // Print the jobs in their original way
        System.out.println("=>Jobs (Ordered by Arrival Time)");
        for (int i = 0; i < jobs.size(); i++) {
            System.out.println("\t=>" + jobs.get(i));
        }

        FCFS_Scheduleling(); // First Come, First Serve
        SJF_Scheduleling(true); // Shortest Job First

        SJF_Scheduleling(false); // Shortest Remaining Time First
        PCPU_Scheduleling(true); // Priority CPU Schedueling

        RR_Scheduleling(); // Round Robin
    }

    // Reset function used after jobs have been processed
    static void Reset() {
        // Sort the jobs by ID then by Arrival Time
        Collections.sort(jobs);
        Collections.sort(jobs, Job.ArrivalTimeComparator());

        // Set completion time to 0 and start time to -1 for every job
        for (int i = 0; i < jobs.size(); i++) {
            jobs.get(i).setCompletion(0);
            jobs.get(i).setStartTime(-1);
        }
    }

    // Work on the job that comes in first
    static void FCFS_Scheduleling() {
        System.out.println("=> First Come, First Serve Schedueling");

        Reset();

        float averageWaitTime = 0;
        float averageTurnAroundTime = 0;
        // Initialize the first job's the arrival time
        jobs.get(0).setStartTime(jobs.get(0).getArrivalTime());
        // For every job
        for (int i = 0; i < jobs.size(); i++) {
            // Complete the job
            // Add to the wait time and turn around time
            averageWaitTime += jobs.get(i).getStartTime() - jobs.get(i).getArrivalTime();
            averageTurnAroundTime += jobs.get(i).getBurstTime() + jobs.get(i).getStartTime() - jobs.get(i).getArrivalTime();
            // If we are not on the last job, then set the start time of the next job
            if (i + 1 < jobs.size()) {
                jobs.get(i + 1).setStartTime(jobs.get(i).getStartTime() + jobs.get(i).getBurstTime());
            }
        }
        // Calculate average wait time and turn around time
        averageWaitTime /= jobs.size();
        averageTurnAroundTime /= jobs.size();
        System.out.println("\t=>Average Wait Time = " + averageWaitTime);
        System.out.println("\t=>Average Turn-Around Time = " + averageTurnAroundTime);
    }

    // The shortest job is first
    // nonPreEmptive means a job will not be interrupted while processing for a shorter job
    static void SJF_Scheduleling(boolean nonPreEmptive) {
        if (nonPreEmptive) {
            System.out.println("=> Shortest Job First Schedueling");
        } else {
            System.out.println("=> Shortest Remaining Time First Schedueling");
        }

        Reset();

        float nextBurst = 0, alpha = .5f;
        float averageWaitTime = 0, averageTurnAroundTime = 0;
        float timer = 0;
        int i = 0, nextJob = -1;
        // For every job, or at least until we are out of jobs
        while (i < jobs.size()) {
            // If there is a job to process that has arrived
            if (jobs.get(i).getArrivalTime() <= timer) {
                // Then check every incompleted job as long as the schedueling is preemptive or a job has yet to be completed (for non-preemptive)
                for (int n = i; n < jobs.size() && (!nonPreEmptive || jobs.get(n).startTime == -1); n++) {
                    // If the job has yet to come, then stop checking for jobs
                    if (jobs.get(n).getArrivalTime() > timer) {
                        break;
                    }
                    // If there is a job that needs to be done
                    // Or if the next job's remainingtime is <= nextBurst (or is preemptive) and if this job's remaining time is less than the presumed job's remaining time
                    if (nextJob == -1 || (n > i + 1
                            && (jobs.get(n).getRemainingTime() <= nextBurst || !nonPreEmptive)
                            && jobs.get(n).getRemainingTime() < jobs.get(nextJob).getRemainingTime())) {
                        // Set the next presumed job
                        nextJob = n;
                    }
                }
                // If there is a next job, then add it to the front of the list and set a start time
                if (nextJob > -1) {
                    jobs.add(i, jobs.remove(nextJob));
                    // Set start time for the job if it hasn't started
                    if (jobs.get(i).getStartTime() == -1) {
                        jobs.get(i).setStartTime(timer);
                    }
                    nextJob = -1; // set to -1 for the next run
                }

                // Get a part of the job done
                jobs.get(i).incrememntCompletion(1);

                // If the job is finished
                if (jobs.get(i).getRemainingTime() == 0) {
                    // Add to the wait time and turn around time
                    averageWaitTime += jobs.get(i).getStartTime() - jobs.get(i).getArrivalTime();
                    averageTurnAroundTime += jobs.get(i).getBurstTime() + jobs.get(i).getStartTime() - jobs.get(i).getArrivalTime();
                    // Calculate next burst aka Tau sub n+1
                    nextBurst = (alpha * jobs.get(i).getBurstTime()) + ((1 - alpha) * nextBurst);
                    i++; // iterate to the next job
                }
            }
            // Increment the OS timer 
            timer++;
        }
        // Calculate average wait time and turn around time
        averageWaitTime /= jobs.size();
        averageTurnAroundTime /= jobs.size();
        System.out.println("\t=>Average Wait Time = " + averageWaitTime);
        System.out.println("\t=>Average Turn-Around Time = " + averageTurnAroundTime);
    }

    // Complete jobs by priority. Less means first up
    static void PCPU_Scheduleling(boolean nonPreEmptive) {
        System.out.println("=> Priority CPU Scheduleing");

        Reset();

        float averageWaitTime = 0, averageTurnAroundTime = 0;
        float timer = 0;
        int i = 0, nextJob = -1;
        // For every job, or at least until we are out of jobs
        while (i < jobs.size()) {
            // If there is a job to process that has arrived
            if (jobs.get(i).getArrivalTime() <= timer) {
                // Then check every incompleted job as long as the schedueling is preemptive or a job has yet to be completed (for non-preemptive)
                // Check by priority
                for (int n = i; n < jobs.size() && ((!nonPreEmptive && jobs.get(n).getPriority() > 1) || jobs.get(n).getStartTime() == -1); n++) {
                    // If the job has yet to come, then stop checking for jobs
                    if (jobs.get(n).getArrivalTime() > timer) {
                        break;
                    }
                    // If there is a job that needs to be done
                    // Or if the next job's prioirty is "greater" the presumed job's priority
                    if (nextJob == -1 || (n > i + 1
                            && jobs.get(n).getPriority() < jobs.get(nextJob).getPriority())) {
                        // Set the presuemd job
                        nextJob = n;
                    }
                }
                // If there is a next job, then add it to the front of the list and set a start time
                if (nextJob > -1) {
                    jobs.add(i, jobs.remove(nextJob));
                    // Set start time for the job if it hasn't started
                    if (jobs.get(i).getStartTime() == -1) {
                        jobs.get(i).setStartTime(timer);
                    }
                    nextJob = -1; // set to -1 for the next run
                }

                // Get a part of the job done
                jobs.get(i).incrememntCompletion(1);

                // If the job is finished
                if (jobs.get(i).getCompletion() >= jobs.get(i).getBurstTime()) {
                    // Add to the wait time and turn around time
                    averageWaitTime += jobs.get(i).getStartTime() - jobs.get(i).getArrivalTime();
                    averageTurnAroundTime += jobs.get(i).getBurstTime() + jobs.get(i).getStartTime() - jobs.get(i).getArrivalTime();
                    i++; // iterate to the next job
                }
            }
            // Increment the OS timer 
            timer++;
        }
        averageWaitTime /= jobs.size();
        averageTurnAroundTime /= jobs.size();
        System.out.println("\t=>Average Wait Time = " + averageWaitTime);
        System.out.println("\t=>Average Turn-Around Time = " + averageTurnAroundTime);
    }

    // Round Robin. Everyone takes turns getting their jobs done
    static void RR_Scheduleling() {
        System.out.println("=> Round Robin");

        Reset();

        float averageWaitTime = 0;
        float averageTurnAroundTime = 0;
        float timer = 0;
        int i = 0;
        int timeQuantam = 3;
        // Set the arrival time of the first job
        jobs.get(i).setStartTime(jobs.get(i).getArrivalTime());
        // For every job
        while (i < jobs.size()) {
            // If there is a job to process that has arrived
            if (jobs.get(i).getArrivalTime() <= timer) {
                // Set the start time of the current job if it hasn't yet started
                if (jobs.get(i).getStartTime() == -1) {
                    jobs.get(i).setStartTime(timer);
                }
                // get as much of the job done as the time quantam needs and finish if need be
                for (int t = 0; t < timeQuantam && t < jobs.get(i).getBurstTime(); t++) {
                    jobs.get(i).incrememntCompletion(1);
                    timer++;
                }
                // If the job is finished
                if (jobs.get(i).getCompletion() >= jobs.get(i).getBurstTime()) {
                    // Add to the wait time and turn around time
                    averageWaitTime += jobs.get(i).getStartTime() - jobs.get(i).getArrivalTime();
                    averageTurnAroundTime += jobs.get(i).getBurstTime() + jobs.get(i).getStartTime() - jobs.get(i).getArrivalTime();
                    i++; // increment
                    continue; // continue through the loop
                }
                // For every available job
                for (int n = i + 1; n < jobs.size(); n++) {
                    // If the job has yet to come, then stop checking for jobs
                    if (jobs.get(n).getArrivalTime() > timer) {
                        // Add the job to the end of the available jobs for processing
                        jobs.add(n, jobs.remove(i));
                        break;
                    }
                }
            } else {
                timer++; // increment the OS clock
            }
        }
        // Calculate average wait time and turn around time
        averageWaitTime /= jobs.size();
        averageTurnAroundTime /= jobs.size();
        System.out.println("\t=>Average Wait Time = " + averageWaitTime);
        System.out.println("\t=>Average Turn-Around Time = " + averageTurnAroundTime);
    }
}
