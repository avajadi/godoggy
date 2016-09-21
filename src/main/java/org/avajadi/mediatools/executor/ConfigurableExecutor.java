package org.avajadi.mediatools.executor;
import se.viktoria.util.Configuration;

public abstract class ConfigurableExecutor {

    protected final Configuration config;

    private ConfigurableExecutor( Configuration config ) {
        this.config = config;
        config.reload();
    }

    public ConfigurableExecutor() {
        this( new Configuration( "godoggy.conf", "godoggy", a -> {
            return true;
        } ));
    }

}
