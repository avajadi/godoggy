package org.avajadi.mediatools.dao;
import org.avajadi.mediatools.Episode;
import se.viktoria.util.Configuration;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EpisodeDAO {

    private static final String URL_TEMPLATE_KEY = "database.urlTemplate";
    private static final String DATABASE_PLACEHOLDER = "{database}";
    private static final String DATABASE_NAME_KEY = "database.name";
    private static final String DATABASE_USER_KEY = "database.user";
    private static final String DATABASE_PASSWORD_KEY = "database.password";
    private final Configuration config;
    private Connection connection = null;

    public EpisodeDAO( Configuration config ) {
        this.config = config;
        buildJDBCURL();
    }

    private String buildJDBCURL() {
        return config.getString( URL_TEMPLATE_KEY ).replace( DATABASE_PLACEHOLDER, config.getString( DATABASE_NAME_KEY ) );
    }

    public void save( Episode episode ) {
        try {
            PreparedStatement statement = connection.prepareStatement( "INSERT IGNORE INTO Episode( series, season, number, airDate, fetched, downloaded ) VALUES (?,?,?,?,?,?)" );
            statement.setString( 1, episode.getSeries() );
            statement.setInt( 2, episode.getSeason() );
            statement.setInt( 3, episode.getNumber() );
            statement.setDate( 4, Date.valueOf( episode.getAirDate() ) );
            statement.setBoolean(5, episode.getFetched());
            statement.setFloat( 6, episode.getDownloaded() );
            statement.execute();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public void markAsFetched( Episode episode ) {
        try {
            PreparedStatement statement = connection.prepareStatement( "UPDATE Episode SET fetched=true WHERE series=? AND season=? AND number=?");
            statement.setString( 1, episode.getSeries() );
            statement.setInt( 2, episode.getSeason() );
            statement.setInt( 3, episode.getNumber() );
            statement.execute();
        } catch ( SQLException e ) {
            e.printStackTrace();
        }
    }

    public List<Episode> findTodaysEpisodes() throws PersistanceException {
        try {
            Statement statement = connection.createStatement();
            statement.execute( "SELECT * FROM Episode WHERE airDate<=DATE(NOW()) AND fetched=false" );
            ResultSet resultSet = statement.getResultSet();
            List<Episode> episodes = new ArrayList<>();
            while ( resultSet.next() ) {
                Episode episode = buildEpisode( resultSet );
                episodes.add( episode );
            }
            resultSet.close();
            System.out.println( "Found " + episodes + " episodes for today" );
            return episodes;
        } catch ( SQLException e ) {
            throw new PersistanceException( e );
        }
    }

    private Episode buildEpisode( ResultSet resultSet ) throws SQLException {
        return new Episode( resultSet.getInt( "season" ), resultSet.getInt( "number" ), resultSet.getDate( "airDate" ).toLocalDate(), resultSet.getString( "series" ), resultSet.getBoolean("fetched"), resultSet.getFloat("downloaded") );
    }

    public void connect() throws ClassNotFoundException, SQLException {

        try {
            Class.forName( "com.mysql.jdbc.Driver" );
        } catch ( ClassNotFoundException e ) {
            throw (e);
        }

        Connection connection;

        try {
            connection = DriverManager
                    .getConnection( buildJDBCURL(), config.getString( DATABASE_USER_KEY ), config.getString( DATABASE_PASSWORD_KEY ) );

        } catch ( SQLException e ) {
            throw (e);
        }

        if ( connection != null ) {
        } else {
            throw (new SQLException( "Failed to make connection!" ));
        }

        this.connection = connection;
    }

    public void close() throws SQLException {
        if ( connection != null ) {
            connection.close();
            connection = null;
        }
    }
}
