package org.avajadi.mediatools;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;

public class Episode {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern( "yyyy E MMM dd" );
    private float downloaded;
    private boolean fetched;
    private int season;
    private int number;
    private LocalDate airDate;
    private String series;

    public Episode( int season, int number, LocalDate airDate, String series, boolean fetched, float downloaded ) {
        this.season = season;
        this.number = number;
        this.airDate = airDate;
        this.series = series;
        this.fetched = fetched;
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return String.format( "%s %s", series, id() );
    }

    private Episode() {
    }

    public static Episode fromNextEpisodeTitleAndDescription( String title, String description ) {
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

    public boolean getFetched() {
        return fetched;
    }

    public float getDownloaded() {
        return downloaded;
    }
}
