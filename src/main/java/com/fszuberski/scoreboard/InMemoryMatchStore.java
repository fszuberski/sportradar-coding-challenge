package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.Match;

import java.util.*;

final class InMemoryMatchStore implements MatchStore {
    private final Map<UUID, Match> matchMap;

    public InMemoryMatchStore() {
        this.matchMap = new HashMap<>();
    }

    @Override
    public Optional<Match> getMatch(UUID id) {
        return Optional.ofNullable(matchMap.get(id));
    }

    @Override
    public List<Match> getAllMatches() {
        return matchMap
                .values()
                .stream()
                .toList();
    }

    @Override
    public void saveMatch(Match match) {
        if (matchMap.containsKey(match.id())) {
            throw new IllegalArgumentException(
                    String.format("Cannot save new match with id='%s'; a match with this id already exists.", match.id()));
        }
        matchMap.put(match.id(), match);
    }

    @Override
    public void updateMatch(UUID id, Match match) {
        if (!matchMap.containsKey(id)) {
            throw new IllegalArgumentException(
                    String.format("Cannot update match with id='%s'; a match with this id does not exist.", match.id()));
        }

        matchMap.put(id, match);
    }

    @Override
    public void removeMatch(UUID id) {
        matchMap.remove(id);
    }
}
