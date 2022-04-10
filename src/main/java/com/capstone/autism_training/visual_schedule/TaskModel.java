package com.capstone.autism_training.visual_schedule;

public class TaskModel {

    public int id;
    public String name;
    public byte[] image;
    public String instruction;
    public long start_time;
    public long duration;

    public TaskModel(int id, String name, byte[] image, String instruction, long start_time, long duration) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.instruction = instruction;
        this.start_time = start_time;
        this.duration = duration;
    }
}
