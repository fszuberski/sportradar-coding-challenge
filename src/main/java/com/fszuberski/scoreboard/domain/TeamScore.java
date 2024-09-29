package com.fszuberski.scoreboard.domain;

public record TeamScore(String teamName, int score) {
    public TeamScore {
        if (teamName == null || teamName.isBlank()) {
            throw new IllegalArgumentException("TeamName cannot be null or blank.");
        }

        if (score < 0) {
            throw new IllegalArgumentException("Score cannot be less than 0.");
        }
    }

    public TeamScore(String teamName) {
        this(teamName, 0);
    }
}
