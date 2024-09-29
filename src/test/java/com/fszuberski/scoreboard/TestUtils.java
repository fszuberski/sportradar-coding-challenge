package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.Match;
import com.fszuberski.scoreboard.domain.TeamScore;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;

public class TestUtils {

    public static Match randomMatch() {
        return new Match(randomTeamScore(), randomTeamScore());
    }

    public static TeamScore randomTeamScore() {
        return new TeamScore(UUID.randomUUID().toString(), ThreadLocalRandom.current().nextInt(5));
    }

    /**
     * Enables obtaining access to the internal map reference of the {@link InMemoryMatchStore} class via reflection.
     * Uses the Execute-Around-Method pattern in order to:
     * - limit the scope of the internal map reference
     * - safely change the accessibility level of the internal map field to private once the method finishes its execution
     *
     * @param matchStore the {@link InMemoryMatchStore} from which the MatchMap instance should be obtained
     * @param block      the code block that should be executed in scope of the internal map reference
     */
    public static void withInternalMapReference(
            @SuppressWarnings("ClassEscapesDefinedScope") InMemoryMatchStore matchStore,
            Consumer<Map<UUID, Match>> block) {
        Field field = null;
        try {
            field = matchStore.getClass().getDeclaredField("matchMap");
            field.setAccessible(true);

            //noinspection unchecked
            block.accept((Map<UUID, Match>) field.get(matchStore));
        } catch (Exception e) {
            fail("Test logic failure; invalid use of reflection");
        } finally {
            if (field != null) {
                field.setAccessible(false);
            }
        }
    }
}
