package com.capstone.autism_training.deck;

import com.capstone.autism_training.card.Card;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class Deck {

    private final int SUBDECK_SIZE = 20;

    private ArrayList<Card> cards;
    private Card previousCard;

    public Deck() {
        cards = new ArrayList<>();
        previousCard = null;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public Card getNextCard() {
        cards.sort(Comparator.comparing(card -> card.nextPracticeTime));
        ArrayList<Card> subDeck = (ArrayList<Card>) cards.subList(0, Math.min(SUBDECK_SIZE, cards.size()));

        Random rand = new Random();
        Card card = subDeck.get(rand.nextInt(subDeck.size()));

        while (previousCard != null && card == previousCard) {
            card = subDeck.get(rand.nextInt(subDeck.size()));
        }
        previousCard = card;
        return card;
    }
}
