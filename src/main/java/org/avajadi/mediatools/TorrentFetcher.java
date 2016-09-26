package org.avajadi.mediatools;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import se.viktoria.util.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

//TODO Add directly to deluge instead, via https://github.com/aegnor/deluge-rpc-java
public class TorrentFetcher {

    private Configuration config;

    public TorrentFetcher( Configuration config ) {
        this.config = config;
    }

    public byte[] get( URL torrentURL ) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        int CONNECTION_TIMEOUT = 80000;
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec( CookieSpecs.STANDARD )
                .setConnectionRequestTimeout( CONNECTION_TIMEOUT )
                .setConnectTimeout( CONNECTION_TIMEOUT )
                .setSocketTimeout( CONNECTION_TIMEOUT )
                .build();

        CookieStore cookieStore = new BasicCookieStore();
        for ( Map.Entry entry : config.rebase( "torrent.cookie" ).entrySet() ) {
            BasicClientCookie cookie = new BasicClientCookie( entry.getKey().toString(), entry.getValue().toString() );
            if( entry.getKey().toString().startsWith( "__" )) {
                cookie.setDomain( ".torrentday.com" );
            } else {
                cookie.setDomain( "www.torrentday.com" );
            }
            cookie.setSecure( false );
            cookie.setPath( "/" );
            cookie.setExpiryDate( Date.from( Instant.now().plus( 10, ChronoUnit.DAYS ) ) );
            cookieStore.addCookie( cookie );
        }

        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLHostnameVerifier( new NoopHostnameVerifier() )
                .setDefaultRequestConfig( requestConfig )
                .setDefaultCookieStore( cookieStore )
                .setUserAgent( "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36" )
                .build();

        HttpGet httpGet = new HttpGet( torrentURL.toString() );
        CloseableHttpResponse response = httpclient.execute( httpGet );

        HttpEntity entity = null;
        try {
            entity = response.getEntity();
            return EntityUtils.toByteArray( entity );
        } finally {
            EntityUtils.consume( entity );
        }
    }


}
