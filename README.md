# sportradar-coding-exercise

<!--toc:start-->

- [Candidate info](#candidate-info)
- [Assumptions](#assumptions)
- [Approach](#approach)
- [Example library usage](#example-library-usage)

<!--toc:end-->

### Candidate info

Filip Szuberski<br>
[Linkedin](https://www.linkedin.com/in/filip-szuberski/)<br>
[Github](https://github.com/fszuberski)<br>

### Assumptions

- the code / project is a simple library implementation
- the library doesn't automatically track if a `Match` should be considered as ended (e.g. `Matches` started multiple hours
ago are still considered 'ongoing' until manually finished by the library client)

### Approach

- following functional programming principles (utilising immutability, streams, internal iterators etc.) over OOP
- keeping classes and methods package-private opposed to having a more clean / complex directory structure in order
  to limit visibility of the library internals for library users
- simple validation only:
    - no custom exceptions - `IllegalArgumentException` thrown if any input parameter is invalid
    - exceptions are thrown as soon as any parameter is considered invalid; this could be changed to gather
      and return _all_ validation errors as an extension of the coding exercise
- no external dependencies outside of dependencies used for tests (JUnit, Mockito)
- no focus on thread safety in order to keep the implementation simple

### Example library usage

The `Scoreboard` class is used for manipulating and retrieving the scoreboard state.

1. Instantiating a `Scoreboard` object

   By default, the `Scoreboard` uses an in-memory store for maintaining `Match` state.
    ```java
    var scoreboard = new Scoreboard();
    ```

   An implementation of the `MatchStore` interface can be passed to the `Scoreboard` constructor in order to change
   the way the `Matches` are stored.
   ```java
   var s3Scoreboard = new Scoreboard(new S3MatchStore());
   ```

2. Starting new `Matches`

   `startMatch` starts a new match with the initial score of 0 : 0. Returns a

   Parameters:
    - homeTeamName - the home team name. Cannot be null or blank.
    - awayTeamName - the away team name. Cannot be null or blank.

   ```java
   scoreboard.startMatch("Argentina", "Australia");
   ```

3. Updating a Match score

   `updateMatchScore` updates the score of an existing match.

   Parameters:
    - matchId - the UUID of an existing match that should be updated. Cannot be null.
    - homeTeamScore - a new absolute value of the home team score. The new score cannot be lower than the previous
      score.
    - awayTeamScore - a new absolute value of the away team score. The new score cannot be lower than the previous
      score.

    ```java
    scoreboard.updateMatchScore(UUID.fromString("31ee5e57-5243-450d-b924-418766fa2d4c"), 2, 1);
    ```
4. Finishing matches

   `finishMatch` finishes an existing match. Has no effect if a match with the passed `matchId` is not ongoing.

   Parameters:
    - matchId - the id of the match that should be finished. Cannot be null.

    ```java
    scoreboard.finishMatch(UUID.fromString("31ee5e57-5243-450d-b924-418766fa2d4c"));
    ```
5. Get summary of ongoing matches

   `getOngoingMatches` returns a summary of matches in progress ordered by their total score. The matches with the
   same total score are returned ordered by the most recently started match in the scoreboard.

   ```java
   var ongoingMatches = scoreboard.getOngoingMatches();
   ```

