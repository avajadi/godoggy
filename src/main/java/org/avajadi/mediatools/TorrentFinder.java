package org.avajadi.mediatools;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import se.viktoria.util.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TorrentFinder {

    private static final java.lang.String URL_TEMPLATE_KEY = "torrent.urlTemplate";
    private static final String EPISODE_PLACEHOLDER = "{episode}";
    private static final String SERIES_PLACEHOLDER = "{series}";
    private Configuration config;

    public TorrentFinder( Configuration config ) {
        this.config = config;
    }

    public List<Torrent> getTorrents( Episode episode ) throws IOException {
        Connection con = Jsoup.connect( buildURL( episode ) );
        for ( Map.Entry entry : config.rebase( "torrent.cookie" ).entrySet() ) {
            con = con.cookie( entry.getKey().toString(), entry.getValue().toString() );
        }

        Document doc = con.get();

        Elements torrentCells = doc.select( "tr.browse" );
        List<Torrent> torrents = new ArrayList<>();
        for ( Element element : torrentCells ) {
            Torrent torrent = Torrent.from( element, episode );
            torrents.add( torrent );
        }
        return torrents;
    }

    private String buildURL( Episode episode ) {
        return config.getString( URL_TEMPLATE_KEY ).replace( EPISODE_PLACEHOLDER, episode.id() ).replace( SERIES_PLACEHOLDER, episode.getSeries() );
    }

    public Torrent selectTorrent( List<Torrent> torrents ) {
        if( torrents.isEmpty() ) return null;

        return torrents.stream().sorted( ( o1, o2 ) -> {
            if ( o2.getSeeders() == o1.getSeeders() ) {
                return 0;
            }
            if ( o2.getSeeders() > o1.getSeeders() ) {
                return 1;
            }
            return -1;
        } ).findFirst().get();
    }

    public Torrent selectTorrent( Episode episode ) throws IOException {
        return selectTorrent( getTorrents( episode ) );
    }
}
