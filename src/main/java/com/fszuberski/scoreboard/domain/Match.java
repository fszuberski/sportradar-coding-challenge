package com.fszuberski.scoreboard.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public record Match(UUID id, TeamScore homeTeamScore, TeamScore awayTeamScore, LocalDateTime startTime) {
    public Match {
        if (id == null) {
            throw new IllegalArgumentException("Match id cannot be null.");
        }

        if (homeTeamScore == null) {
            throw new IllegalArgumentException("HomeTeamScore cannot be null.");
        }

        if (awayTeamScore == null) {
            throw new IllegalArgumentException("AwayTeamScore cannot be null.");
        }

        if (startTime == null) {
            throw new IllegalArgumentException("StartTime cannot be null.");
        }
    }

    public Match(TeamScore homeTeamScore, TeamScore awayTeamScore) {
        this(UUID.randomUUID(), homeTeamScore, awayTeamScore, LocalDateTime.now());
    }

    public int totalScore() {
        return homeTeamScore().score() + awayTeamScore().score();
    }
}
