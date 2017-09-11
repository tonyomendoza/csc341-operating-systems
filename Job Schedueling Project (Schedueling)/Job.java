package project3;

import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Tony Mendoza
 */
public class Job implements Comparable {

    int ID;
    float arrivalTime;
    float burstTime;
    int priority;
    float completion;
    float startTime;

    public Job(int ID) {
        this.ID = ID;
        completion = 0;
        startTime = -1;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public float getArrivalTime() {
        return arrivalTime;
    }
    
    public float getRemainingTime(){
        return burstTime - completion;
    }

    public void setArrivalTime(float arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public float getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(float burstTime) {
        this.burstTime = burstTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public float getStartTime(){
        return startTime;
    }
    
    public void setStartTime(float startTime){
        this.startTime = startTime;
    }

    @Override
    public int compareTo(Object t) {
        Job j = (Job) t;
        if (this.getID() > j.getID()) {
            return 1;
        } else if (this.getID() < j.getID()) {
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Job{" + "ID=" + ID + ", arrivalTime=" + arrivalTime + ", burstTime=" + burstTime + ", priority=" + priority + '}';
    }

    public float getCompletion() {
        return completion;
    }
    
    public void setCompletion(float value) {
        completion = value;
    }

    public void incrememntCompletion(float c) {
        if (completion < burstTime) {
            completion += c;
        }
    }

    static Comparator<Job> PriorityComparator() {
        return new Comparator<Job>() {
            @Override
            public int compare(Job a, Job b) {
                if (a.getPriority() > b.getPriority()) {
                    return 1;
                } else if (a.getPriority() < b.getPriority()) {
                    return -1;
                }
                return 0;
            }
        };
    }

    static Comparator<Job> ArrivalTimeComparator() {
        return (Job a, Job b) -> {
            if (a.getArrivalTime() > b.getArrivalTime()) {
                return 1;
            } else if (a.getArrivalTime() < b.getArrivalTime()) {
                return -1;
            }
            return 0;
        };
    }
}
