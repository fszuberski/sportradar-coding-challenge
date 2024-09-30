package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.Match;
import com.fszuberski.scoreboard.domain.TeamScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.fszuberski.scoreboard.TestUtils.withInternalMatchStoreReference;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ScoreboardTest {

    private Scoreboard scoreboard;
    private MatchStore matchStoreMock;

    @BeforeEach
    public void beforeEach() {
        this.matchStoreMock = mock(MatchStore.class);
        this.scoreboard = new Scoreboard(matchStoreMock);
    }

    @Nested
    public class CanonicalConstructor {

        @Test
        @DisplayName("should throw exception given MatchStore is null")
        public void shouldThrowExceptionGivenMatchStoreIsNull() {
            // when: the Scoreboard is initialized using the canonical constructor with a null MatchStore
            @SuppressWarnings("DataFlowIssue")
            Executable executable = () -> scoreboard = new Scoreboard(null);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("MatchStore cannot be null.", result.getMessage());
        }

        @Test
        @DisplayName("should be initialized with the passed MatchStore implementation dependency")
        public void shouldBeInitializedWithThePassedMatchStoreImplementationDependency() {
            // given: a MatchStore implementation
            var expectedMatchStore = mock(MatchStore.class);

            // when: the Scoreboard is initialized using the canonical constructor
            scoreboard = new Scoreboard(expectedMatchStore);

            // then: the Scoreboard uses the passed MatchStore implementation
            withInternalMatchStoreReference(
                    scoreboard,
                    matchStore -> assertEquals(expectedMatchStore, matchStore));
        }
    }

    @Nested
    public class EmptyConstructor {

        @Test
        @DisplayName("should be initialized with an InMemoryMatchStore dependency")
        public void shouldBeInitializedWithAnInMemoryMatchStoreDependency() {
            // when: the Scoreboard is initialized using the empty constructor
            scoreboard = new Scoreboard();

            // then: the chosen implementation of the MatchStore should be the InMemoryMatchStore
            withInternalMatchStoreReference(
                    scoreboard,
                    matchStore -> assertInstanceOf(InMemoryMatchStore.class, matchStore));
        }
    }

    @Nested
    public class StartMatch {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should throw exception given home team name is null or blank")
        public void shouldThrowExceptionGivenHomeTeamNameIsNullOrBlank(String homeTeamName) {
            // when: startMatch is invoked with a null or empty homeTeamName parameter
            Executable executable = () -> scoreboard.startMatch(homeTeamName, "AwayTeamName");

            // then: an InvalidArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("HomeTeamName cannot be null or blank.", result.getMessage());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should throw exception given away team name is null or blank")
        public void shouldThrowExceptionGivenAwayTeamNameIsNullOrBlank(String awayTeamName) {
            // when: startMatch is invoked with a null or empty homeTeamName parameter
            Executable executable = () -> scoreboard.startMatch("HomeTeamName", awayTeamName);

            // then: an InvalidArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("AwayTeamName cannot be null or blank.", result.getMessage());
        }

        @Test
        @DisplayName("should save a new Match in the MatchStore")
        public void shouldSaveANewMatchInTheMatchStore() {
            // when: startMatch is invoked
            var homeTeamName = "HomeTeamName";
            var awayTeamName = "AwayTeamName";
            scoreboard.startMatch(homeTeamName, awayTeamName);

            // then: a new Match is saved in the MatchStore
            var captor = ArgumentCaptor.forClass(Match.class);
            verify(matchStoreMock, times(1)).saveMatch(captor.capture());

            // and: the new Match has properly set team names
            assertEquals(homeTeamName, captor.getValue().homeTeamScore().teamName());
            assertEquals(awayTeamName, captor.getValue().awayTeamScore().teamName());

            // and: the new Match starts with the score of 0:0
            assertEquals(0, captor.getValue().homeTeamScore().score());
            assertEquals(0, captor.getValue().awayTeamScore().score());
        }
    }

    @Nested
    public class UpdateMatchScore {

        @Test
        @DisplayName("should throw exception given null MatchId")
        public void shouldThrowExceptionGivenNullMatchId() {
            // when: updateMatchScore is invoked with a null MatchId
            Executable executable = () -> scoreboard.updateMatchScore(null, 0, 0);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("MatchId cannot be null.", result.getMessage());
        }

        @Test
        @DisplayName("should throw exception given no Match is in progress with the passed MatchId")
        public void shouldThrowExceptionGivenNoMatchIsInProgressWithThePassedMatchId() {
            // given: a MatchId that doesn't exist in the MatchStore
            var matchId = UUID.randomUUID();
            when(matchStoreMock.getMatch(eq(matchId))).thenReturn(Optional.empty());

            // when: updateMatchScore is invoked with the MatchId
            Executable executable = () -> scoreboard.updateMatchScore(matchId, 0, 0);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals(String.format("Match with id='%s' is not currently in progress.", matchId), result.getMessage());
        }

        @Test
        @DisplayName("should throw exception given passed home team score is lower than the existing home team score")
        public void shouldThrowExceptionGivenPassedHomeTeamScoreIsLowerThanTheExistingHomeTeamScore() {
            // given: a Match exists in the MatchStore
            var match = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 3),
                    LocalDateTime.now()
            );
            when(matchStoreMock.getMatch(eq(match.id()))).thenReturn(Optional.of(match));

            // when: updateMatchScore is invoked with the MatchId of the existing Match
            Executable executable = () -> scoreboard.updateMatchScore(match.id(), 1, 3);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("New score cannot be lower than the previous score.", result.getMessage());
        }

        @Test
        @DisplayName("should throw exception given passed away team score is lower than the existing away team score")
        public void shouldThrowExceptionGivenPassedAwayTeamScoreIsLowerThanTheExistingAwayTeamScore() {
            // given: a Match exists in the MatchStore
            var match = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 3),
                    LocalDateTime.now()
            );
            when(matchStoreMock.getMatch(eq(match.id()))).thenReturn(Optional.of(match));

            // when: updateMatchScore is invoked with the MatchId of the existing Match
            Executable executable = () -> scoreboard.updateMatchScore(match.id(), 2, 1);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals("New score cannot be lower than the previous score.", result.getMessage());
        }

        @Test
        @DisplayName("should update scores in the MatchStore")
        public void shouldUpdateScoresInTheMatchStore() {
            // given: a Match exists in the MatchStore
            var originalMatch = new Match(
                    UUID.randomUUID(),
                    new TeamScore("Team1", 2),
                    new TeamScore("Team2", 3),
                    LocalDateTime.now()
            );
            when(matchStoreMock.getMatch(eq(originalMatch.id()))).thenReturn(Optional.of(originalMatch));

            // when: updateMatchScore is invoked with the MatchId of the existing Match
            scoreboard.updateMatchScore(originalMatch.id(), 3, 5);

            // then: the scores are updated in the MatchStore
            var expectedUpdatedMatch = new Match(
                    originalMatch.id(),
                    new TeamScore(originalMatch.homeTeamScore().teamName(), 3),
                    new TeamScore(originalMatch.awayTeamScore().teamName(), 5),
                    originalMatch.startTime()
            );
            verify(matchStoreMock, times(1)).updateMatch(eq(originalMatch.id()), eq(expectedUpdatedMatch));
        }
    }
}
