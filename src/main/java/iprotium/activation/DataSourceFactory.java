/*
 * $Id$
 *
 * Copyright 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import iprotium.util.Factory;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.TreeMap;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;

/**
 * {@link DataSource} {@link Factory} abstract base class.  Provides
 * {@link #newDataSource(URI)} and {@link #newDataSource(URL)} methods.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class DataSourceFactory extends Factory<DataSource> {

    /**
     * Static method to get a default {@link DataSourceFactory} instance.
     *
     * @return  A {@link DataSourceFactory}.
     */
    public static DataSourceFactory getDefault() { return new DEFAULT(); }

    /**
     * Sole constructor.
     */
    protected DataSourceFactory() { super(DataSource.class); }

    /**
     * Method to create a {@link DataSource} from a {@link URI}.
     *
     * @param   uri             The {@link URI} which identifies the
     *                          {@link DataSource}.
     *
     * @return  The {@link DataSource}.
     */
    public abstract DataSource newDataSource(URI uri);

    /**
     * Method to create a {@link DataSource} from a {@link URL}.
     *
     * @param   url             The {@link URL} which identifies the
     *                          {@link DataSource}.
     *
     * @return  The {@link DataSource}.
     */
    public abstract DataSource newDataSource(URL url);

    /**
     * Default implementation of a {@link DataSourceFactory}.  Provided for
     * subclass implementations.  Produces {@link FileDataSource}s for the
     * {@code file} scheme, {@link ByteArrayDataSource}s for the
     * {@code memory} scheme, and {@link URLDataSource}s for all other
     * schemes.
     */
    protected static class DEFAULT extends DataSourceFactory {

        private TreeMap<String,DataSourceFactory> map =
            new TreeMap<String,DataSourceFactory>(String.CASE_INSENSITIVE_ORDER);

        /**
         * Sole constructor.
         */
        protected DEFAULT() {
            super();

            register(new FILE());
            register(new MEMORY());
        }

        /**
         * Method to register a {@link DataSourceFactory} for a scheme.  The
         * implementing class should have the simple name
         * ({@link Class#getSimpleName()}) corresponding to the scheme it is
         * implementing.
         */
        protected void register(DataSourceFactory factory) {
            map.put(factory.getClass().getSimpleName(), factory);
        }

        @Override
        public DataSource newDataSource(URI uri) {
            DataSource ds = null;
            String scheme = uri.getScheme();

            try {
                ds =
                    containsKey(scheme)
                        ? map.get(scheme).newDataSource(uri)
                        : newDataSource(uri.toURL());
            } catch (MalformedURLException exception) {
                throw new Error(exception);
            }

            return ds;
        }

        @Override
        public DataSource newDataSource(URL url) {
            DataSource ds = null;
            String scheme = url.getProtocol();

            ds =
                containsKey(scheme)
                    ? map.get(scheme).newDataSource(url)
                    : new URLDataSource(url);

            return ds;
        }
    }

    private static class FILE extends DataSourceFactory {
        private static final long serialVersionUID = 2729558371922376308L;

        public FILE() { super(); }

        @Override
        public DataSource newDataSource(URI uri) {
            return new FileDataSource(new File(uri));
        }

        @Override
        public DataSource newDataSource(URL url) {
            DataSource ds = null;

            try {
                ds = newDataSource(url.toURI());
            } catch (URISyntaxException exception) {
                throw new Error(exception);
            }

            return ds;
        }
    }

    private static class MEMORY extends DataSourceFactory {
        private static final long serialVersionUID = -1056917231748139088L;

        public MEMORY() { super(); }

        @Override
        public DataSource newDataSource(URI uri) {
            return new ByteArrayDataSource(uri.getSchemeSpecificPart(), null);
        }

        @Override
        public DataSource newDataSource(URL url) {
            DataSource ds = null;

            try {
                ds = newDataSource(url.toURI());
            } catch (URISyntaxException exception) {
                throw new Error(exception);
            }

            return ds;
        }
    }
}
