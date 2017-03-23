package club.elo.dao;

import club.elo.converter.ResultSetConverter;
import club.elo.pojo.EloEntry;
import lombok.AllArgsConstructor;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Methods for accessing the local DB.
 */
@AllArgsConstructor
public class EloDAO {

    private final ResultSetConverter rsConverter;
    // used for query number 5
    private static final String TEAMS_ON_DATE = "select * from ClubEloEntry E where '%s' >= E.startDate and '%s' <= E.endDate";
    private static final String LOW_ELO = "select min(E.elo) as lowElo from (%s) as E order by E.elo desc limit 32";
    // used for query number 7
    private static final String TEAM_WORST = "SELECT * FROM ClubEloEntry WHERE name='%s' ORDER BY elo ASC LIMIT 1";
    // used for query number 8
    private static final String TEAMS_ERA = "select * from ClubEloEntry C where C.startDate >= '%s' and C.endDate <= '%s'";
    private static final String AVG_ERA = "select T.country, T.name, avg(T.elo) as avgElo from (%s) as T group by T.country, T.name;";
    private static final String MIN_AVG = "select min(A.avgElo) as minAvg from (%s) as A order by A.avgElo desc limit 20";

    // Helper method for getTeamLowestRank
    private Set<EloEntry> getMinEloEntry(final Statement statement, final String clubname) {
        String sqlQuery = String.format(TEAM_WORST, clubname);
        try {
            ResultSet rs = statement.executeQuery(sqlQuery);
            return rsConverter.convertToPOJO(rs);
        } catch (Exception e) {
            throw new RuntimeException("Failure querying local database.", e);
        }
    }

    public int getTeamLowestRank(final Statement statement, final String clubName) {
        Optional<EloEntry> entry = getMinEloEntry(statement, clubName).stream().findFirst();

        if (entry.isPresent()) {
            String sqlQuery = String.format("SELECT 1 + count(*) as numBetterTeams from ClubEloEntry C where C.startDate <= '%s' and C.endDate >= '%s' and C.elo >= %s", entry.get().getStartDate(), entry.get().getEndDate(), entry.get().getElo());
            
            try {
                ResultSet rs = statement.executeQuery(sqlQuery);
                return rsConverter.convertToRank(rs);
            } catch (Exception e) {
                throw new RuntimeException("Failure querying local database.", e);
            }

        }
           
    }

    public Set<EloEntry> getBestAllTime(final Statement statement, final Integer limit) {
        String sqlQuery = String.format("SELECT * FROM ClubEloEntry ORDER BY elo DESC LIMIT %d", limit);
        try {
            ResultSet rs = statement.executeQuery(sqlQuery);
            return rsConverter.convertToPOJO(rs);
        } catch (Exception e) {
            throw new RuntimeException("Failure querying local database.", e);
        }
    }

    public Set<EloEntry> getBestForDate(final Statement statement, final Date date, final Optional<Integer> limit) {
        String sqlQuery = String.format("SELECT * FROM ClubEloEntry WHERE startDate<='%s' AND endDate>='%s' ORDER BY elo DESC", date, date);
        if (limit.isPresent()) {
            sqlQuery = sqlQuery.concat(String.format(" LIMIT %d", limit.get()));
        }
        try {
            ResultSet rs = statement.executeQuery(sqlQuery);
            return rsConverter.convertToPOJO(rs);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failure querying local database for date %s.", date), e);
        }
    }

    public Set<EloEntry> getMaxEloEntry(final Statement statement, final String clubName) {
        try {
            ResultSet rs = statement.executeQuery(String.format("SELECT * FROM ClubEloEntry WHERE name='%s' ORDER BY elo DESC LIMIT 1", clubName));
            return rsConverter.convertToPOJO(rs);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failure querying local database for %s.", clubName), e);
        }
    }

    public List<String> getLocalTeams(final Statement statement) {
        try {
            ResultSet rs = statement.executeQuery("SELECT DISTINCT name FROM ClubEloEntry ORDER BY name ASC");
            return rsConverter.convertToTeamNames(rs);
        } catch (Exception e) {
            throw new RuntimeException("Failure querying local database for local teams.", e);
        }
    }

    public Set<EloEntry> getLocalClubEntry(final Statement statement, final String clubName, final Optional<Integer> limit) {
        String sqlQuery = String.format("SELECT * FROM ClubEloEntry WHERE name='%s' ORDER BY startDate DESC", clubName);
        if (limit.isPresent()) {
            sqlQuery = sqlQuery.concat(String.format(" LIMIT %d", limit.get()));
        }
        try {
            ResultSet rs = statement.executeQuery(sqlQuery);
            return rsConverter.convertToPOJO(rs);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failure querying local database for %s.", clubName), e);
        }
    }

    public void addToLocalDatabase(final Statement statement, final EloEntry entry) {
        try {
            statement.addBatch(String.format("INSERT INTO ClubEloEntry (rank, name, country, level, elo, startDate," +
                            "endDate) VALUES ('%s', '%s', '%s', %s, %s, '%s', '%s')", entry.getRank(), entry.getClubName(),
                    entry.getCountry(), entry.getLevelOfPlay(), entry.getElo(), entry.getStartDate(),
                    entry.getEndDate()));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failure adding %s entry to database.", entry), e);
        }
    }

    public void removeFromLocalDatabase(final Statement statement, final EloEntry entry) {
        try {
            System.out.println("Removing " + entry);
            statement.addBatch(String.format("DELETE FROM ClubEloEntry WHERE name='%s' AND startDate='%s' AND " +
                            "endDate='%s'", entry.getClubName(), entry.getStartDate(), entry.getEndDate()));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Failure adding %s entry to database.", entry), e);
        }
    }

    public void executeBatch(final Statement statement) {
        try {
            statement.executeLargeBatch();
        } catch (Exception e) {
            throw new RuntimeException("Batch execution failed.", e);
        }
    }
}
