package com.technophile.nuro.train;

import com.technophile.nuro.card.CardModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TrainDeck {

    private final int SUBDECK_SIZE = 20;

    private final ArrayList<CardModel> cards;
    private CardModel previousCard;

    public TrainDeck() {
        cards = new ArrayList<>();
        previousCard = null;
    }

    public void addCard(CardModel card) {
        cards.add(card);
    }

    public void removeCard(CardModel card) {
        cards.remove(card);
    }

    public void clearAllCards() {
        cards.clear();
        previousCard = null;
    }

    public int getSize() {
        return cards.size();
    }

    public CardModel getNextCard() {
        if (cards.size() == 0) {
            return null;
        }

        cards.sort(Comparator.comparing(cardModel -> -cardModel.nextPracticeTime));
        List<CardModel> subDeck = cards.subList(0, Math.min(SUBDECK_SIZE, cards.size()));

        Random rand = new Random();
        CardModel cardModel = subDeck.get(rand.nextInt(subDeck.size()));

        while (previousCard != null && cardModel == previousCard) {
            cardModel = subDeck.get(rand.nextInt(subDeck.size()));
        }
        previousCard = cardModel;
        return cardModel;
    }
}
