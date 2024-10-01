package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.Match;
import com.fszuberski.scoreboard.domain.TeamScore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComparatorsTest {

    @Nested
    public class TotalScoreComparator {

        @Test
        @DisplayName("should return equal given matches have equal total score")
        public void shouldReturnEqualGivenMatchesHaveEqualTotalScore() {
            var match1 = new Match(
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2)
            );

            var match2 = new Match(
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2)
            );

            assertEquals(0, Comparators.totalScoreComparator.compare(match1, match2));
        }

        @Test
        @DisplayName("should return greater than given Match1 total score is higher")
        public void shouldReturnGreaterThanGivenMatch1TotalScoreIsHigher() {
            var match1 = new Match(
                    new TeamScore("Team1", 5),
                    new TeamScore("Team2", 2)
            );

            var match2 = new Match(
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2)
            );

            assertTrue(Comparators.totalScoreComparator.compare(match1, match2) > 0);
        }

        @Test
        @DisplayName("should return less than given Match1 total score is lower")
        public void shouldReturnLessThanGivenMatch1TotalScoreIsLower() {
            var match1 = new Match(
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 1)
            );

            var match2 = new Match(
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2)
            );

            assertTrue(Comparators.totalScoreComparator.compare(match1, match2) < 0);
        }
    }

    @Nested
    public class StartTimeComparator {

        @Test
        @DisplayName("should return equal given Matches have equal start time")
        public void shouldReturnEqualGivenMatchesHaveEqualStartTime() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertEquals(0, Comparators.startTimeComparator.compare(match1, match2));
        }

        @Test
        @DisplayName("should return greater than given Match1 start time is later")
        public void shouldReturnGreaterThanGivenMatch1StartTimeIsLater() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now.plus(Duration.ofMinutes(5))
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.startTimeComparator.compare(match1, match2) > 0);
        }

        @Test
        @DisplayName("should return less than given Match1 start time is earlier")
        public void shouldReturnLessThanGivenMatch1StartTimeIsEarlier() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now.minus(Duration.ofMinutes(5))
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.startTimeComparator.compare(match1, match2) < 0);
        }
    }

    @Nested
    public class TotalScoreAndStartTimeComparator {

        @Test
        @DisplayName("should return equal given matches have equal total score and start time")
        public void shouldReturnEqualGivenMatchesHaveEqualTotalScoreAndStartTime() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertEquals(0, Comparators.totalScoreAndStartTimeComparator.compare(match1, match2));
        }

        @Test
        @DisplayName("should return less than given Matches have equal total score and Match1 start time is earlier")
        public void shouldReturnLessThanGivenMatchesHaveEqualTotalScoreAndMatch1StartTimeIsEarlier() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1",2),
                    new TeamScore("Team2", 1),
                    now.minus(Duration.ofMinutes(5))
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 1),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.totalScoreAndStartTimeComparator.compare(match1, match2) < 0);
        }

        @Test
        @DisplayName("should return greter than given Matches have equal total score and Match1 start time is later")
        public void shouldReturnGreaterThanGivenMatchesHaveEqualTotalScoreAndMatch1StartTimeIsLater() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1",2),
                    new TeamScore("Team2", 1),
                    now.plus(Duration.ofMinutes(5))
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 1),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.totalScoreAndStartTimeComparator.compare(match1, match2) > 0);
        }

        @Test
        @DisplayName("should return greater than given Match1 total score is higher and start times are equal")
        public void shouldReturnGreaterThanGivenMatch1TotalScoreIsHigherAndStartTimesAreEqual() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 5),
                    new TeamScore("Team2", 2),
                    now
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.totalScoreAndStartTimeComparator.compare(match1, match2) > 0);
        }

        @Test
        @DisplayName("should return greater than given Match1 total score is higher and start time is earlier")
        public void shouldReturnGreaterThanGivenMatch1TotalScoreIsHigherAndStartTimeIsEarlier() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 5),
                    new TeamScore("Team2", 2),
                    now.minus(Duration.ofMinutes(5))
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.totalScoreAndStartTimeComparator.compare(match1, match2) > 0);
        }

        @Test
        @DisplayName("should return greater than given match1 total score is higher and start time is later")
        public void shouldReturnGreaterThanGivenMatch1TotalScoreIsHigherAndStartTimeIsLater() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 5),
                    new TeamScore("Team2", 2),
                    now.plus(Duration.ofMinutes(5))
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.totalScoreAndStartTimeComparator.compare(match1, match2) > 0);
        }

        @Test
        @DisplayName("should return less than given Match1 total score is lower and start times are equal")
        public void shouldReturnLessThanGivenMatch1TotalScoreIsLowerAndStartTimesAreEqual() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 1),
                    now
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.totalScoreAndStartTimeComparator.compare(match1, match2) < 0);
        }

        @Test
        @DisplayName("should return less than given Match1 total score is lower and start time is earlier")
        public void shouldReturnLessThanGivenMatch1TotalScoreIsLowerAndStartTimeIsEarlier() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 1),
                    now.minus(Duration.ofMinutes(5))
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.totalScoreAndStartTimeComparator.compare(match1, match2) < 0);
        }

        @Test
        @DisplayName("should return less than given Match1 total score is lower and start time is later")
        public void shouldReturnLessThanGivenMatch1TotalScoreIsLowerAndStartTimeIsLater() {
            var now = LocalDateTime.now();

            var match1 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1",2),
                    new TeamScore("Team2", 1),
                    now.plus(Duration.ofMinutes(5))
            );

            var match2 = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 2),
                    now
            );

            assertTrue(Comparators.totalScoreAndStartTimeComparator.compare(match1, match2) < 0);
        }
    }
}