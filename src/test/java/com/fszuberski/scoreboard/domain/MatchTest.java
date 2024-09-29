package com.fszuberski.scoreboard.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static com.fszuberski.scoreboard.TestUtils.randomTeamScore;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MatchTest {

    @Nested
    public class CanonicalConstructor {

        @ParameterizedTest
        @MethodSource("invalidConstructorParameters")
        @DisplayName("should throw exception given invalid constructor parameters")
        public void shouldThrowExceptionGivenInvalidConstructorParameters(
                UUID id,
                TeamScore homeTeamScore,
                TeamScore awayTeamScore,
                LocalDateTime startTime,
                String exceptionMessage
        ) {
            // when: an invalid parameter is passed to the Match constructor
            Executable executable = () -> new Match(id, homeTeamScore, awayTeamScore, startTime);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals(exceptionMessage, result.getMessage());
        }

        private static Stream<Arguments> invalidConstructorParameters() {
            return Stream.of(
                    Arguments.of(
                            null,
                            randomTeamScore(),
                            randomTeamScore(),
                            LocalDateTime.now(),
                            "Match id cannot be null."),
                    Arguments.of(
                            UUID.randomUUID(),
                            null,
                            randomTeamScore(),
                            LocalDateTime.now(),
                            "HomeTeamScore cannot be null."),
                    Arguments.of(
                            UUID.randomUUID(),
                            randomTeamScore(),
                            null,
                            LocalDateTime.now(),
                            "AwayTeamScore cannot be null."),
                    Arguments.of(
                            UUID.randomUUID(),
                            randomTeamScore(),
                            randomTeamScore(),
                            null,
                            "StartTime cannot be null.")
            );
        }

    }

    @Nested
    public class ShortenedConstructor {

        @ParameterizedTest
        @MethodSource("invalidConstructorParameters")
        @DisplayName("should throw exception given invalid constructor parameters")
        public void shouldThrowExceptionGivenInvalidConstructorParameters(
                TeamScore homeTeamScore,
                TeamScore awayTeamScore,
                String exceptionMessage
        ) {
            // when: an invalid parameter is passed to the Match constructor
            Executable executable = () -> new Match(homeTeamScore, awayTeamScore);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals(exceptionMessage, result.getMessage());
        }

        private static Stream<Arguments> invalidConstructorParameters() {
            return Stream.of(
                    Arguments.of(
                            null,
                            randomTeamScore(),
                            "HomeTeamScore cannot be null."),
                    Arguments.of(
                            randomTeamScore(),
                            null,
                            "AwayTeamScore cannot be null.")
            );
        }
    }

    @Nested
    public class TotalScore {

        @ParameterizedTest
        @MethodSource("scoreParameters")
        @DisplayName("should return total score as a sum of home team and away team scores")
        public void shouldReturnTotalScoreAsASumOfHomeTeamAndAwayTeamScores(
                int homeTeamScore,
                int awayTeamScore,
                int expectedTotalScore) {
            // given: a Match with individual scores of {homeTeamScore} and {awayTeamScore}
            var match = new Match(
                    new TeamScore(UUID.randomUUID().toString(), homeTeamScore),
                    new TeamScore(UUID.randomUUID().toString(), awayTeamScore)
            );

            // when: totalScore() is invoked
            var result = match.totalScore();

            // then: a total score of {expectedTotalScore} is returned
            assertEquals(expectedTotalScore, result);

        }

        private static Stream<Arguments> scoreParameters() {
            return Stream.of(
                    // homeTeamScore, awayTeamScore, expectedTotalScore
                    Arguments.of(0, 0, 0),
                    Arguments.of(1, 0, 1),
                    Arguments.of(0, 1, 1),
                    Arguments.of(1, 2, 3),
                    Arguments.of(5, 4, 9),
                    Arguments.of(3, 3, 6)
            );
        }
    }

}