package org.avajadi.mediatools.executor;
import org.avajadi.mediatools.Episode;
import org.avajadi.mediatools.Torrent;
import org.avajadi.mediatools.TorrentFetcher;
import org.avajadi.mediatools.TorrentFinder;
import org.avajadi.mediatools.dao.EpisodeDAO;
import org.avajadi.mediatools.dao.PersistanceException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

public class GetTorrents extends ConfigurableExecutor {

    private static final String TORRENT_SPOOL_KEY = "torrent.spooldirectory";

    public void run() throws NoSuchAlgorithmException, IOException, KeyManagementException, SQLException, ClassNotFoundException, PersistanceException {
        EpisodeDAO episodeDAO = new EpisodeDAO( config );
        episodeDAO.connect();
        List<Episode> episodes = episodeDAO.findTodaysEpisodes();
        TorrentFinder torrentFinder = new TorrentFinder( config );
        TorrentFetcher tf = new TorrentFetcher( config );
        for( Episode episode : episodes ) {
            Torrent torrent = torrentFinder.selectTorrent( episode );
            byte[] torrentData = tf.get( torrent.getURL() );
            File file = new File( config.getString(TORRENT_SPOOL_KEY), torrent.filename() );
            OutputStream out = new FileOutputStream( file );
            out.write( torrentData );
            out.close();
            episodeDAO.markAsFetched( episode );
        }
        episodeDAO.close();
    }

    public static void main( String[] args ) throws NoSuchAlgorithmException, IOException, KeyManagementException, SQLException, PersistanceException, ClassNotFoundException {
        new GetTorrents().run();
    }
}
