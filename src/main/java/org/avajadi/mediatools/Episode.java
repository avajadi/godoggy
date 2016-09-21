package org.avajadi.mediatools;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;

public class Episode {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern( "yyyy E MMM dd" );
    private int season;
    private int number;
    private LocalDate airDate;
    private String series;

    @Override
    public String toString() {
        return String.format( "%s %s", series, id() );
    }

    public Episode( int season, int number, LocalDate airDate, String series ) {
        this.season = season;
        this.number = number;
        this.airDate = airDate;
        this.series = series;
    }

    private Episode() {
    }

    public static Episode fromNextEpisodeTitleAndDescription( String title, String description ) {
        // SyndFeedImpl.entries[16].titleEx.value=Homeland - In 118 days
        // SyndFeedImpl.entries[16].description.value=6x01 | Airs on: Mon Jan 16
        String[] parts = title.split( " - " );
        //TODO Handle extra hyphen-space instances
        //TODO Handle missing parts
        Episode e = new Episode();
        e.setSeries( parts[0] );
        parts = description.split( " \\| " );
        String[] episodeParts = parts[0].split( "x" );
        e.setSeason( episodeParts[0] );
        e.setNumber( episodeParts[1] );
        e.setAirDate( parts[1] );
        return e;
    }

    public int getSeason() {
        return season;
    }

    private void setSeason( String season ) {
        this.season = Integer.valueOf( season );
    }

    public int getNumber() {
        return number;
    }

    private void setNumber( String number ) {
        this.number = Integer.valueOf( number );
    }

    public LocalDate getAirDate() {
        return airDate;
    }
    private void setAirDate( String airDate ) {
        System.out.println( "Setting airDate from " + airDate + " ::: " + airDate.replace( "Airs on: ", "2016 " ) );//DEBUG
        int year = Year.now().getValue();
        try {
            this.airDate = LocalDate.parse( airDate.replace( "Airs on: ", String.format("%d ", year) ), DATE_FORMAT );
        } catch ( Exception e ) {
            // Assuming this is because the date belongs to next year.
            year++;
            this.airDate = LocalDate.parse( airDate.replace( "Airs on: ", String.format("%d ", year) ), DATE_FORMAT );
        }
    }


    public String getSeries() {
        return series;
    }

    private void setSeries( String series ) {
        this.series = series;
    }

    public String id(){
        return String.format("S%02dE%02d", season, number);
    }

}
