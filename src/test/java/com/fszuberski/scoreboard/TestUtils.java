package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.TeamScore;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class TestUtils {

    public static TeamScore randomTeamScore() {
        return new TeamScore(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(5));
    }
}
