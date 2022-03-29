package com.capstone.autism_training.card;

public class CardModel {

    public int id;
    public byte[] image;
    public String caption;
    public String answer;
    public long nextPracticeTime;
    public int repetitions;
    public int interval;
    public double easiness;

    public CardModel(int id, byte[] image, String caption, String answer, long nextPracticeTime, int repetitions, int interval, double easiness) {
        this.id = id;
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = nextPracticeTime;
        this.repetitions = repetitions;
        this.interval = interval;
        this.easiness = easiness;
    }

    public CardModel(int id, byte[] image, String caption, String answer, long nextPracticeTime, int repetitions, int interval) {
        this.id = id;
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = nextPracticeTime;
        this.repetitions = repetitions;
        this.interval = interval;
        this.easiness = 2.5;
    }

    public CardModel(int id, byte[] image, String caption, String answer, long nextPracticeTime, int repetitions) {
        this.id = id;
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = nextPracticeTime;
        this.repetitions = repetitions;
        this.interval = 1;
        this.easiness = 2.5;
    }

    public CardModel(int id, byte[] image, String caption, String answer, long nextPracticeTime) {
        this.id = id;
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.nextPracticeTime = nextPracticeTime;
        this.repetitions = 0;
        this.interval = 1;
        this.easiness = 2.5;
    }

    public CardModel(int id, byte[] image, String caption, String answer) {
        this.id = id;
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
    }
}
