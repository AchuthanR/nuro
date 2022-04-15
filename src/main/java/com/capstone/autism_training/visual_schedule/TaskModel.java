package com.capstone.autism_training.visual_schedule;

public class TaskModel {

    public long id;
    public String name;
    public byte[] image;
    public String instruction;
    public long start_time;
    public long duration;
    public boolean completed;

    public TaskModel(long id, String name, byte[] image, String instruction, long start_time, long duration, boolean completed) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.instruction = instruction;
        this.start_time = start_time;
        this.duration = duration;
        this.completed = completed;
    }
}
