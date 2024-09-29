package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.Match;
import com.fszuberski.scoreboard.domain.TeamScore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.fszuberski.scoreboard.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryMatchStoreTest {

    private InMemoryMatchStore matchStore;

    @BeforeEach
    public void beforeEach() {
        this.matchStore = new InMemoryMatchStore();
    }

    @Nested
    public class GetMatch {

        @Test
        @DisplayName("should return empty optional given the MatchStore is empty")
        public void shouldReturnEmptyOptionalGivenMatchStoreIsEmpty() {
            // given: the MatchStore is empty
            withInternalMapReference(
                    matchStore,
                    matchMap -> assertTrue(matchMap.isEmpty()));

            // when: the MatchStore is queried using a matchId that does not exist within the store
            var result = matchStore.getMatch(UUID.randomUUID());

            // then: the result is an empty optional
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should return empty optional given match with passed id does not exist in the MatchStore")
        public void shouldReturnEmptyOptionalGivenMatchWithPassedIdDoesNotExistInTheMatchStore() {
            // given: the MatchStore contains multiple matches
            fillMatchStoreWithRandomData();

            // when: the MatchStore is queried using a matchId that does not exist within the store
            var result = matchStore.getMatch(UUID.randomUUID());

            // then: the result is an empty optional
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("should return an optional containing a specific match given match with passed id exists in the MatchStore")
        public void shouldReturnAnOptionalContainingASpecificMatchGivenMatchWithPassedIdExistsInTheMatchStore() {
            // given: the MatchStore contains multiple matches
            fillMatchStoreWithRandomData();

            var expectedId = UUID.randomUUID();
            var expectedMatch = new Match(
                    expectedId,
                    new TeamScore(UUID.randomUUID().toString()),
                    new TeamScore(UUID.randomUUID().toString()),
                    LocalDateTime.now()
            );
            withInternalMapReference(
                    matchStore,
                    matchMap -> matchMap.put(expectedId, expectedMatch));

            // when: the matchStore is queried using an id that exists in the MatchStore
            var result = matchStore.getMatch(expectedId);

            // then: the result is an optional that contains the expected match
            assertNotNull(result);
            assertTrue(result.isPresent());
            assertEquals(expectedMatch, result.get());
        }
    }

    @Nested
    public class GetAllMatches {

        @Test
        @DisplayName("should return an empty list given no matches exist in the MatchStore")
        public void shouldReturnAnEmptyListGivenNoMatchesExistInTheMatchStore() {
            // given: the MatchStore is empty
            withInternalMapReference(
                    matchStore,
                    matchMap -> assertTrue(matchMap.isEmpty()));

            // when: the MatchStore is queried for all matches
            var result = matchStore.getAllMatches();

            // when: the result is an empty list
            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 10})
        @DisplayName("should return a list containing all elements of the MatchStore")
        public void shouldReturnAListContainingAllElementsOfTheMatchStore(int numberOfElements) {
            // given: the MatchStore contains {numberOfElements} elements
            fillMatchStoreWithRandomData(numberOfElements);

            // when: the MatchStore is queried for all matches
            var result = matchStore.getAllMatches();

            // when: the result is a list containing {numberOfElements}
            assertNotNull(result);
            assertEquals(numberOfElements, result.size());
        }
    }

    @Nested
    public class SaveMatch {

        @Test
        @DisplayName("should save the passed Match to the MatchStore")
        public void shouldSaveThePassedMatchToTheMatchStore() {
            // given: a Match
            var expectedMatch = randomMatch();

            // when: saveMatch is invoked with the Match as a parameter
            matchStore.saveMatch(expectedMatch);

            // then: the passed Match exists in the MatchStore
            withInternalMapReference(
                    matchStore,
                    matchMap -> {
                        assertTrue(matchMap.containsKey(expectedMatch.id()));
                        assertEquals(expectedMatch, matchMap.get(expectedMatch.id()));
                    });
        }

        @Test
        @DisplayName("should throw exception given the Match already exists in the MatchStore")
        public void shouldThrowExceptionGivenMatchAlreadyExistsInTheMatchStore() {
            // given: a Match exists in the MatchStore
            var match = randomMatch();
            withInternalMapReference(
                    matchStore,
                    matchMap -> matchMap.put(match.id(), match));

            // when: saveMatch is invoked with the Match as a parameter
            Executable executable = () -> matchStore.saveMatch(match);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals(
                    String.format("Cannot save new match with id='%s'; a match with this id already exists.", match.id()),
                    result.getMessage());
        }
    }

    @Nested
    public class UpdateMatch {

        @Test
        @DisplayName("should update Match given Match exists in the MatchStore")
        public void shouldUpdateMatchGivenMatchExistsInTheMatchStore() {
            // given: a Match exists in the MatchStore
            var originalMatch = randomMatch();
            withInternalMapReference(
                    matchStore,
                    matchMap -> {
                        matchMap.put(originalMatch.id(), originalMatch);
                        // additional confirmation that the Match is properly saved
                        assertTrue(matchMap.containsKey(originalMatch.id()));
                    });

            // when: updateMatch is invoked with the existing Match id
            var updatedMatch = new Match(
                    originalMatch.id(),
                    randomTeamScore(),
                    randomTeamScore(),
                    LocalDateTime.now()
            );
            matchStore.updateMatch(originalMatch.id(), updatedMatch);

            // then: the MatchStore contains the updated Match
            assertEquals(originalMatch.id(), updatedMatch.id());
            withInternalMapReference(
                    matchStore,
                    matchMap -> {
                        assertTrue(matchMap.containsKey(updatedMatch.id()));
                        assertEquals(updatedMatch, matchMap.get(updatedMatch.id()));
                        assertNotEquals(originalMatch, matchMap.get(updatedMatch.id()));
                    });
        }

        @Test
        @DisplayName("should throw exception given the Match does not exist in the MatchStore")
        public void shouldThrowExceptionGivenTheMatchDoesNotExistInTheMatchStore() {
            // given: the MatchStore contains multiple matches
            fillMatchStoreWithRandomData();

            // updateMatch is invoked for a non-existing Match
            var updatedMatch = new Match(
                    UUID.randomUUID(),
                    randomTeamScore(),
                    randomTeamScore(),
                    LocalDateTime.now()
            );
            Executable executable = () -> matchStore.updateMatch(updatedMatch.id(), updatedMatch);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals(
                    String.format("Cannot update match with id='%s'; a match with this id does not exist.", updatedMatch.id()),
                    result.getMessage());
        }

        @Test
        @DisplayName("should throw exception given the MatchStore is empty")
        public void shouldThrowExceptionGivenTheMatchStoreIsEmpty() {
            // given: the MatchStore is empty
            withInternalMapReference(
                    matchStore,
                    matchMap -> assertTrue(matchMap.isEmpty()));

            // when: updateMatch is invoked for a non-existing Match
            var updatedMatch = new Match(
                    UUID.randomUUID(),
                    randomTeamScore(),
                    randomTeamScore(),
                    LocalDateTime.now()
            );
            Executable executable = () -> matchStore.updateMatch(updatedMatch.id(), updatedMatch);

            // then: an IllegalArgumentException is thrown
            var result = assertThrows(IllegalArgumentException.class, executable);
            assertEquals(
                    String.format("Cannot update match with id='%s'; a match with this id does not exist.", updatedMatch.id()),
                    result.getMessage());

            // and: the MatchStore has not changed
            withInternalMapReference(
                    matchStore,
                    matchMap -> assertTrue(matchMap.isEmpty()));
        }
    }

    @Nested
    public class RemoveMatch {

        @Test
        @DisplayName("should remove Match given Match exists in the MatchStore")
        public void shouldRemoveMatchGivenMatchExistsInTheMatchStore() {
            // given: a Match exists in the MatchStore
            var match = randomMatch();
            withInternalMapReference(
                    matchStore,
                    matchMap -> {
                        matchMap.put(match.id(), match);
                        // additional confirmation that the Match is properly saved
                        assertTrue(matchMap.containsKey(match.id()));
                    });

            // when: removeMatch is invoked with the Match id
            matchStore.removeMatch(match.id());

            // then: the Match no longer exists in the MatchStore
            withInternalMapReference(
                    matchStore,
                    matchMap -> assertFalse(matchMap.containsKey(match.id())));
        }

        @Test
        @DisplayName("should have no effect given Match does not exist in the MatchStore")
        public void shouldHaveNoEffectGivenMatchDoesNotExistInTheMatchStore() {
            // given: the MatchStore is empty
            withInternalMapReference(
                    matchStore,
                    matchMap -> assertTrue(matchMap.isEmpty()));

            // when: removeMatch is invoked with the Match id
            var matchId = UUID.randomUUID();
            matchStore.removeMatch(matchId);

            // then: the Match doesn't exist in the MatchStore
            withInternalMapReference(
                    matchStore,
                    matchMap -> {
                        assertFalse(matchMap.containsKey(matchId));
                        assertTrue(matchMap.isEmpty());
                    });
        }
    }

    private void fillMatchStoreWithRandomData() {
        fillMatchStoreWithRandomData(25);
    }

    private void fillMatchStoreWithRandomData(int numberOfMatches) {
        var matchList = IntStream
                .range(0, numberOfMatches)
                .mapToObj(ignored -> randomMatch())
                .toList();

        withInternalMapReference(
                matchStore,
                matchMap -> {
                    for (Match match : matchList) {
                        matchMap.put(match.id(), match);
                    }
                });
    }

}