# SoccerElo
Databases project for CPE 365. Using data from clubelo.com, create more 
advanced/useful queries related to club/nation/league elo.

All date queries should be of the form DD/MM/YYYY.

List of Queries (Michael)
5) For Date X, return the top 32 teams
7) Return team X's lowest ever ranking
8) Return 20 most dominant teams over period of time (find avg elo and sort)

List of Queries (Brent)
13) For Team X, return biggest upset in its own history (max elo gain)
14) For Team X, return net difference in ELO for given month (should specify year as well)

SELECT *
FROM ClubEloEntry
WHERE clubName = 'X' AND startDate <= YYYY-MM+1-01 AND endDate >= YYYY-MM-01
ORDER BY endDate ASC
LIMIT 1;

SELECT *
FROM ClubEloEntry
WHERE clubName = 'X' AND startDate <= YYYY-MM+1-01 AND endDate >= YYYY-MM-01
ORDER BY endDate DESC
LIMIT 1;
SELECT (lates from view) - (earliest from view)

List of Queries (Dylan)
1) Receive team X's highest ever ranking
2) For Date X, return a list of all the teams and their ELO (highest ELO first)
3) Get a list of all the teams (alphabetical)
4) For (Team X1, Date X2) and (Team Y1, Date Y2), predict who will win the match (would require simulation, probably not)

List of Queries (Vihari)
9) Given Month and Year, return top 20 teams based off ELO gained over month
10) Given Year, return top 20 teams based off ELO gained over year (year starts
    in July 1st)
11) Find top 20 upsets in history (top 20 largest ELO gains over one game)
12) For Team X, return net difference in ELO for given year


NOTE: ROOM FOR EXPANSION
we can potentially use current elo to create new matches and predict game
outcomes based off factors such as home field advantage. Can also add a little
randomness to this so we can almost simulate games and not completely know
the outcome
