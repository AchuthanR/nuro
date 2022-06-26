package com.technophile.nuro.schedule;

public class TaskModel {

    public long id;
    public String name;
    public byte[] image;
    public String instruction;
    public long start_time;
    public long duration;
    public boolean completed;
    public long current_end_time;

    public TaskModel() {

    }

    public TaskModel(long id, String name, byte[] image, String instruction, long start_time, long duration, boolean completed, long current_end_time) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.instruction = instruction;
        this.start_time = start_time;
        this.duration = duration;
        this.completed = completed;
        this.current_end_time = current_end_time;
    }
}
