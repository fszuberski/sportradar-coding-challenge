package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.Match;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MatchStore {

    Optional<Match> getMatch(UUID id);

    List<Match> getAllMatches();

    void saveMatch(Match match);

    void updateMatch(UUID id, Match match);

    void removeMatch(UUID id);
}
