package com.rayen.forumManagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class InappropriateLanguageFilterService {

    /**
     * Liste de mots interdits, séparés par des virgules dans application.properties.
     * Exemple : app.forum.profanity.words=mot1,mot2,mot3
     */
    @Value("${app.forum.profanity.words:}")
    private String forbiddenWordsRaw;

    /**
     * Score minimal à partir duquel un texte est considéré comme problématique.
     */
    @Value("${app.forum.profanity.threshold:1}")
    private int threshold;

    /**
     * Calcule un score simple en comptant le nombre de mots interdits trouvés
     * (recherche insensible à la casse, sur des sous-chaînes).
     */
    public int computeScore(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        List<String> words = getForbiddenWords();
        if (words.isEmpty()) {
            return 0;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        int score = 0;
        for (String w : words) {
            if (!w.isBlank() && lower.contains(w)) {
                score++;
            }
        }
        return score;
    }

    /**
     * Retourne true si le score dépasse ou atteint le seuil configuré.
     */
    public boolean isSuspicious(String text) {
        return computeScore(text) >= threshold;
    }

    private List<String> getForbiddenWords() {
        if (forbiddenWordsRaw == null || forbiddenWordsRaw.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(forbiddenWordsRaw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .toList();
    }
}

