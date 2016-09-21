package org.avajadi.mediatools.executor;
import com.rometools.fetcher.FetcherException;
import com.rometools.rome.io.FeedException;
import org.avajadi.mediatools.EpisodeIndexFetcher;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class FindNewEpisodes extends ConfigurableExecutor {

    public static void main( String[] args ) throws IOException, FetcherException, FeedException, SQLException, ClassNotFoundException, NoSuchAlgorithmException, KeyManagementException {

        new FindNewEpisodes().run();

    }

    public void run() throws IOException, FetcherException, SQLException, FeedException, ClassNotFoundException {
        new EpisodeIndexFetcher( config ).run();
    }
}
