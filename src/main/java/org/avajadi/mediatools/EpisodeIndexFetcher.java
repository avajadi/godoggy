package org.avajadi.mediatools;
import com.rometools.fetcher.FetcherException;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.avajadi.mediatools.dao.EpisodeDAO;
import se.viktoria.util.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EpisodeIndexFetcher {

    private static final String URL_TEMPLATE_KEY = "feed.urlTemplate";
    private static final String USER_KEY = "feed.user";
    private static final String USERNAME_PLACEHOLDER = "{user}";
    private Configuration config;

    public EpisodeIndexFetcher( Configuration config ) throws MalformedURLException {
        this.config = config;
    }

    public void run() throws FetcherException, IOException, FeedException, SQLException, ClassNotFoundException {
        List<Episode> episodes = fetch();
        EpisodeDAO episodeDAO = new EpisodeDAO( config );
        episodeDAO.connect();
        episodes.stream().forEach( episode -> episodeDAO.save( episode ) );
        episodeDAO.close();
    }

    public List<Episode> fetch() throws FetcherException, IOException, FeedException {
        List<Episode> episodes = new ArrayList<>();

        CloseableHttpClient client = HttpClients.createMinimal();
        HttpUriRequest method = new HttpGet( buildURL() );
        CloseableHttpResponse response = client.execute( method );
        InputStream stream = response.getEntity().getContent();
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build( new XmlReader( stream ) );

        for ( SyndEntry entry : feed.getEntries() ) {
            try {
                Episode episode = Episode.fromNextEpisodeTitleAndDescription( entry.getTitle(), entry.getDescription().getValue() );
                episodes.add( episode );
            } catch ( Exception e ) {
                System.err.println( e.getMessage() );
            }
        }
        return episodes;
    }

    private String buildURL() throws MalformedURLException {
        String urlString = config.getString( URL_TEMPLATE_KEY ).replace( USERNAME_PLACEHOLDER, config.getString( USER_KEY ) );
        return urlString;
    }
}
