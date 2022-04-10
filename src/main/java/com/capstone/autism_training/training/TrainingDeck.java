package com.capstone.autism_training.training;

import com.capstone.autism_training.card.CardModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class TrainingDeck {

    private final int SUBDECK_SIZE = 20;

    private ArrayList<CardModel> cards;
    private CardModel previousCard;

    public TrainingDeck() {
        cards = new ArrayList<>();
        previousCard = null;
    }

    public void addCard(CardModel card) {
        cards.add(card);
    }

    public CardModel getNextCard() {
        cards.sort(Comparator.comparing(cardModel -> cardModel.nextPracticeTime));
        ArrayList<CardModel> subDeck = (ArrayList<CardModel>) cards.subList(0, Math.min(SUBDECK_SIZE, cards.size()));

        Random rand = new Random();
        CardModel nextCard = subDeck.get(rand.nextInt(subDeck.size()));

        while (previousCard != null && nextCard == previousCard) {
            nextCard = subDeck.get(rand.nextInt(subDeck.size()));
        }
        previousCard = nextCard;
        return nextCard;
    }
}
