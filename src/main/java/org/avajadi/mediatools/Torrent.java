package org.avajadi.mediatools;
import org.jsoup.nodes.Element;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Torrent {

    private final long size;
    private final int seeders;
    private final URL url;
    private String name;
    private Episode episode;

    public Torrent( String name, long size, int seeders, URL url, Episode episode ) {
        this.name = name;
        this.size = size;
        this.seeders = seeders;
        this.url = url;
        this.episode = episode;
    }

    public static Torrent from( Element element, Episode episode ) throws MalformedURLException {
        Element sizeInfo = element.select( "td.sizeInfo > a" ).first();
        System.err.println( element );
        String[] sizeParts = sizeInfo.text().split( " " );
        Double size = Double.valueOf( sizeParts[0] );
        for( String prefix : new String[]{"K","M","G","T"} ) {
            size *= 1024;
            if( sizeParts[1].trim().startsWith( prefix ) ) {
                break;
            }
        }

        Element urlInfo = element.select( "a.index" ).first();
        URL url = new URL( "https://www.torrentday.com/" + urlInfo.attr( "href" ) );

        Element nameInfo = element.select( "a.torrentName" ).first();
        String name = nameInfo.text();

        Element seedersInfo = element.select( "td.seedersInfo" ).first();
        int seeders = Integer.valueOf( seedersInfo.text().replace( ",", "" ) );

        return new Torrent( name, size.longValue(), seeders, url, episode );
    }

    @Override
    public String toString() {
        return "Torrent{" +
                "size=" + size +
                ", seeders=" + seeders +
                ", url=" + url +
                ", name='" + name + '\'' +
                '}';
    }

    public int getSeeders() {
        return seeders;
    }

    public URL getURL() {
        return url;
    }
    public String filename() {
        return new File( url.getFile()).getName();
    }
}
