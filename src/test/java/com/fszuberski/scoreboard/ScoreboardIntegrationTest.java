package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.TeamScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.fszuberski.scoreboard.TestUtils.withInternalMapReference;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreboardIntegrationTest {

    private Scoreboard scoreboard;

    @BeforeEach
    public void beforeEach() {
        this.scoreboard = new Scoreboard();
    }

    @Nested
    public class StartMatch {

        @Test
        @DisplayName("should start new Matches")
        public void shouldStartNewMatches() {
            // when: multiple Matches are added to an empty Scoreboard
            var mexicoCanadaMatchId = scoreboard.startMatch("Mexico", "Canada");
            var spainBrazilMatchId = scoreboard.startMatch("Spain", "Brazil");
            var germanyFranceMatchId = scoreboard.startMatch("Germany", "France");
            var uruguayItalyMatchId = scoreboard.startMatch("Uruguay", "Italy");
            var argentinaAustraliaMatchId = scoreboard.startMatch("Argentina", "Australia");

            // then: the Scoreboard contains all added Matches
            withInternalMapReference(scoreboard, matchMap -> assertEquals(5, matchMap.size()));

            // and: all Matches have their state properly set
            verifyMatchState(mexicoCanadaMatchId, "Mexico", "Canada", 0, 0);
            verifyMatchState(spainBrazilMatchId, "Spain", "Brazil", 0, 0);
            verifyMatchState(germanyFranceMatchId, "Germany", "France", 0, 0);
            verifyMatchState(uruguayItalyMatchId, "Uruguay", "Italy", 0, 0);
            verifyMatchState(argentinaAustraliaMatchId, "Argentina", "Australia", 0, 0);
        }
    }

    private void verifyMatchState(UUID matchId, String homeTeamName, String awayTeamName, int homeTeamScore, int awayTeamScore) {
        withInternalMapReference(scoreboard, matchMap -> {
            var match = matchMap.get(matchId);
            assertNotNull(match);
            assertEquals(matchId, match.id());
            assertEquals(new TeamScore(homeTeamName, homeTeamScore), match.homeTeamScore());
            assertEquals(new TeamScore(awayTeamName, awayTeamScore), match.awayTeamScore());
            assertTrue(match.startTime().isBefore(LocalDateTime.now()));
        });
    }
}
