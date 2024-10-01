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
import static org.junit.jupiter.api.Assumptions.assumeTrue;

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

    @Nested
    public class UpdateMatchScore {

        @Test
        public void shouldUpdateExistingMatches() {
            // given: multiple Matches are added to an empty Scoreboard
            var mexicoCanadaMatchId = scoreboard.startMatch("Mexico", "Canada");
            var spainBrazilMatchId = scoreboard.startMatch("Spain", "Brazil");
            var germanyFranceMatchId = scoreboard.startMatch("Germany", "France");
            var uruguayItalyMatchId = scoreboard.startMatch("Uruguay", "Italy");
            var argentinaAustraliaMatchId = scoreboard.startMatch("Argentina", "Australia");

            // when: Matches have their scores updated
            scoreboard.updateMatchScore(mexicoCanadaMatchId, 0, 5);
            scoreboard.updateMatchScore(spainBrazilMatchId, 10, 2);
            scoreboard.updateMatchScore(germanyFranceMatchId, 2, 2);
            scoreboard.updateMatchScore(uruguayItalyMatchId, 6, 6);
            scoreboard.updateMatchScore(argentinaAustraliaMatchId, 3, 1);

            // then: all Matches have their state properly set
            verifyMatchState(mexicoCanadaMatchId, "Mexico", "Canada", 0, 5);
            verifyMatchState(spainBrazilMatchId, "Spain", "Brazil", 10, 2);
            verifyMatchState(germanyFranceMatchId, "Germany", "France", 2, 2);
            verifyMatchState(uruguayItalyMatchId, "Uruguay", "Italy", 6, 6);
            verifyMatchState(argentinaAustraliaMatchId, "Argentina", "Australia", 3, 1);
        }
    }

    @Nested
    public class FinishMatch {

        @Test
        @DisplayName("should finish existing matches")
        public void shouldFinishExistingMatches() {
            // given: multiple Matches are added to an empty Scoreboard
            var mexicoCanadaMatchId = scoreboard.startMatch("Mexico", "Canada");
            var spainBrazilMatchId = scoreboard.startMatch("Spain", "Brazil");
            var germanyFranceMatchId = scoreboard.startMatch("Germany", "France");
            var uruguayItalyMatchId = scoreboard.startMatch("Uruguay", "Italy");
            var argentinaAustraliaMatchId = scoreboard.startMatch("Argentina", "Australia");

            withInternalMapReference(scoreboard, matchMapBeforeRemoval ->
                    assertEquals(5, matchMapBeforeRemoval.size()));

            // when: existing Scoreboard Matches are finished
            scoreboard.finishMatch(mexicoCanadaMatchId);
            scoreboard.finishMatch(spainBrazilMatchId);
            scoreboard.finishMatch(germanyFranceMatchId);
            scoreboard.finishMatch(uruguayItalyMatchId);
            scoreboard.finishMatch(argentinaAustraliaMatchId);

            // then: the Scoreboard is empty
            withInternalMapReference(scoreboard, matchMapAfterRemoval ->
                    assertEquals(0, matchMapAfterRemoval.size()));
        }
    }

    @Nested
    public class GetOngoingMatches {

        @Test
        @DisplayName("should return all matches ordered by their total score and start time")
        public void shouldReturnAllMatchesOrderedByTheirTotalScoreAndStartTime() {
            // given: multiple Matches are added to an empty Scoreboard
            assumeTrue(scoreboard.getOngoingMatches().isEmpty());
            var mexicoCanadaMatchId = scoreboard.startMatch("Mexico", "Canada");
            var spainBrazilMatchId = scoreboard.startMatch("Spain", "Brazil");
            var germanyFranceMatchId = scoreboard.startMatch("Germany", "France");
            var uruguayItalyMatchId = scoreboard.startMatch("Uruguay", "Italy");
            var argentinaAustraliaMatchId = scoreboard.startMatch("Argentina", "Australia");

            // and: Matches have their scores updated
            scoreboard.updateMatchScore(mexicoCanadaMatchId, 0, 5);
            scoreboard.updateMatchScore(spainBrazilMatchId, 10, 2);
            scoreboard.updateMatchScore(germanyFranceMatchId, 2, 2);
            scoreboard.updateMatchScore(uruguayItalyMatchId, 6, 6);
            scoreboard.updateMatchScore(argentinaAustraliaMatchId, 3, 1);

            // when: all ongoing matches are retrieved from the Scoreboard
            var result = scoreboard.getOngoingMatches();

            // then: all matches are returned
            assertEquals(5, result.size());

            // and: all matches are ordered by their total score and start time
            verifyMatchState(result.get(0).id(), "Uruguay", "Italy", 6, 6);
            verifyMatchState(result.get(1).id(), "Spain", "Brazil", 10, 2);
            verifyMatchState(result.get(2).id(), "Mexico", "Canada", 0, 5);
            verifyMatchState(result.get(3).id(), "Argentina", "Australia", 3, 1);
            verifyMatchState(result.get(4).id(), "Germany", "France", 2, 2);
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
