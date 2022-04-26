package com.capstone.autism_training.card;

public class CardModel {

    public long id;
    public byte[] image;
    public String caption;
    public String answer;
    public int repetitions;
    public int interval;
    public double easiness;
    public long nextPracticeTime;

    public CardModel(long id, byte[] image, String caption, String answer, int repetitions, int interval, double easiness, long nextPracticeTime) {
        this.id = id;
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.repetitions = repetitions;
        this.interval = interval;
        this.easiness = easiness;
        this.nextPracticeTime = nextPracticeTime;
    }

    public CardModel(long id, byte[] image, String caption, String answer) {
        this.id = id;
        this.image = image;
        this.caption = caption;
        this.answer = answer;
        this.repetitions = 0;
        this.interval = 0;
        this.easiness = 2.5;
        this.nextPracticeTime = System.currentTimeMillis();
    }

    public void update(int quality) {
        if (quality < 0 || quality > 5) {
            return ;
        }

        if (quality == 0) {
            repetitions = 0;
            interval = 0;
        }
        else if (quality < 3) {
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
