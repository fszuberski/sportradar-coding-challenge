package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.Match;
import com.fszuberski.scoreboard.domain.TeamScore;

import java.util.UUID;

public class Scoreboard {

    private final MatchStore matchStore;

    /**
     * Default constructor for the {@link Scoreboard} class.
     * Uses the {@link InMemoryMatchStore InMemoryMatchStore} class
     * as the default implementation of the {@link MatchStore} interface.
     */
    public Scoreboard() {
        this(new InMemoryMatchStore());
    }

    /**
     * Canonical constructor for the {@link Scoreboard} class.
     *
     * @param matchStore a concrete implementation for the {@link MatchStore} interface. Cannot be null.
     */
    public Scoreboard(MatchStore matchStore) {
        if (matchStore == null) {
            throw new IllegalArgumentException("MatchStore cannot be null.");
        }
        this.matchStore = matchStore;
    }

    /**
     * Starts a new match with the initial score of 0 : 0 and saves it to the store.
     *
     * @param homeTeamName the home team name. Cannot be null or blank.
     * @param awayTeamName the away team name. Cannot be null or blank.
     */
    public UUID startMatch(String homeTeamName, String awayTeamName) {
        if (homeTeamName == null || homeTeamName.isBlank()) {
            throw new IllegalArgumentException("HomeTeamName cannot be null or blank.");
        }

        if (awayTeamName == null || awayTeamName.isBlank()) {
            throw new IllegalArgumentException("AwayTeamName cannot be null or blank.");
        }

        var match = new Match(
                new TeamScore(homeTeamName),
                new TeamScore(awayTeamName));

        matchStore.saveMatch(match);
        return match.id();
    }
}
