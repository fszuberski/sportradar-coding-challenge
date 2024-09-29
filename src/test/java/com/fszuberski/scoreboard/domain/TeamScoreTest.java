package com.fszuberski.scoreboard.domain;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TeamScoreTest {

    @Nested
    public class CanonicalConstructor {

        @ParameterizedTest
        @MethodSource("invalidConstructorParameters")
        public void shouldThrowExceptionGivenInvalidConstructorParameters(
                String teamName,
                int score,
                String exceptionMessage
        ) {
            // when: an invalid parameter is passed to the TeamScore constructor
            Executable executable = () -> new TeamScore(teamName, score);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals(exceptionMessage, result.getMessage());
        }

        private static Stream<Arguments> invalidConstructorParameters() {
            return Stream.of(
                    Arguments.of(
                            null,
                            0,
                            "TeamName cannot be null or blank."),
                    Arguments.of(
                            "",
                            0,
                            "TeamName cannot be null or blank."),
                    Arguments.of(
                            " ",
                            0,
                            "TeamName cannot be null or blank."),
                    Arguments.of(
                            "\n\t\r ",
                            0,
                            "TeamName cannot be null or blank."),
                    Arguments.of(
                            "Poland",
                            -1,
                            "Score cannot be less than 0."),
                    Arguments.of(
                            "Poland",
                            Integer.MIN_VALUE,
                            "Score cannot be less than 0.")
            );
        }
    }

    @Nested
    public class ShortenedConstructor {

        @ParameterizedTest
        @MethodSource("invalidConstructorParameters")
        public void shouldThrowExceptionGivenInvalidConstructorParameters(
                String teamName,
                String exceptionMessage
        ) {
            // when: an invalid parameter is passed to the TeamScore constructor
            Executable executable = () -> new TeamScore(teamName);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals(exceptionMessage, result.getMessage());
        }

        private static Stream<Arguments> invalidConstructorParameters() {
            return Stream.of(
                    Arguments.of(
                            null,
                            "TeamName cannot be null or blank."),
                    Arguments.of(
                            "",
                            "TeamName cannot be null or blank."),
                    Arguments.of(
                            " ",
                            "TeamName cannot be null or blank."),
                    Arguments.of(
                            "\n\t\r ",
                            "TeamName cannot be null or blank.")
            );
        }

    }
}