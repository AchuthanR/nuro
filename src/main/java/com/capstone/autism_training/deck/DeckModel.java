package com.capstone.autism_training.deck;

public class DeckModel {

    public int id;
    public byte[] image;
    public String name;
    public String description;

    public DeckModel(int id, byte[] image, String name, String description) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
    }
}
