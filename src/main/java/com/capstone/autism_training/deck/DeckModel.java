package com.capstone.autism_training.deck;

public class DeckModel {

    public long id;
    public byte[] image;
    public String name;
    public String description;

    public DeckModel(long id, byte[] image, String name, String description) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.description = description;
    }
}
