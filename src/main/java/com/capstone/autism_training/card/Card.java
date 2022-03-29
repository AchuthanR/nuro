package com.capstone.autism_training.card;

import java.util.List;

public class Card {

    public List<byte[]> image;
    public List<String> caption;
    public List<String> answer;
    public long nextPracticeTime;
    public int repetitions;
    public int interval;
    public double easiness;

    public Card(List<byte[]> image, List<String> caption, List<String> answer, long nextPracticeTime, int repetitions, int interval, double easiness) {
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = nextPracticeTime;
        this.repetitions = repetitions;
        this.interval = interval;
        this.easiness = easiness;
    }

    public Card(List<byte[]> image, List<String> caption, List<String> answer, long nextPracticeTime, int repetitions, int interval) {
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = nextPracticeTime;
        this.repetitions = repetitions;
        this.interval = interval;
        this.easiness = 2.5;
    }

    public Card(List<byte[]> image, List<String> caption, List<String> answer, long nextPracticeTime, int repetitions) {
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = nextPracticeTime;
        this.repetitions = repetitions;
        this.interval = 1;
        this.easiness = 2.5;
    }

    public Card(List<byte[]> image, List<String> caption, List<String> answer, long nextPracticeTime) {
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = nextPracticeTime;
        this.repetitions = 0;
        this.interval = 1;
        this.easiness = 2.5;
    }

    public Card(List<byte[]> image, List<String> caption, List<String> answer) {
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = System.currentTimeMillis();
        this.repetitions = 0;
        this.interval = 1;
        this.easiness = 2.5;
    }

    public void update(int quality) {
        if (quality < 0 || quality > 5) {
            return ;
        }

        if (quality < 3) {
            repetitions = 0;
            interval = 1;
        }
        else {
            if (repetitions <= 1) {
                interval = 1;
            } else if (repetitions == 2) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * easiness);
            }
            repetitions += 1;
        }

        easiness = Math.max(1.3, easiness + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02));

        nextPracticeTime = System.currentTimeMillis() + 60L * 60 * 24 * 1000 * interval;
        // Store everything in the database
    }
}
