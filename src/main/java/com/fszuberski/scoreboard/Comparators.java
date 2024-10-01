package com.fszuberski.scoreboard;

import com.fszuberski.scoreboard.domain.Match;

import java.util.Comparator;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

class Comparators {

    static final Comparator<Match> totalScoreComparator = comparingInt(Match::totalScore);

    static final Comparator<Match> startTimeComparator = comparing(Match::startTime);

    static final Comparator<Match> totalScoreAndStartTimeComparator = Stream
            .of(totalScoreComparator, startTimeComparator)
            .reduce(Comparator::thenComparing)
            .get();
}
