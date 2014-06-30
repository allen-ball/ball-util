/*
 * $Id$
 *
 * Copyright 2010 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import ball.io.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * {@link Properties} implementation that overrides
 * {@link #load(InputStream)} and {@link #store(OutputStream,String)}
 * methods to specify the {@link Charset} (as {@code UTF-8}).
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class PropertiesImpl extends Properties {
    private static final long serialVersionUID = -5422894536821798748L;

    /**
     * UTF-8
     */
    protected static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * See {@link Properties#Properties()}.
     */
    public PropertiesImpl() { this(null); }

    /**
     * See {@link Properties#Properties(Properties)}.
     */
    public PropertiesImpl(Properties defaults) { super(defaults); }

    /**
     * See {@link #PropertiesImpl(Properties)}.
     *
     * @param   defaults        The default {@link Properties}.
     * @param   file            The {@link File} to load
     *                          (may be {@code null}).
     */
    public PropertiesImpl(Properties defaults, File file) throws IOException {
        this(defaults);

        if (file != null) {
            InputStream in = null;

            try {
                in = new FileInputStream(file);
                load(in);
            } finally {
                IOUtil.close(in);
            }
        }
    }

    @Override
    public void load(InputStream in) throws IOException { load(this, in); }

    @Override
    public void store(OutputStream out, String comment) throws IOException {
        store(this, out, comment);
    }

    protected static void load(Properties properties,
                               InputStream in) throws IOException {
        properties.load(new InputStreamReader(in, CHARSET));
    }

    protected static void store(Properties properties,
                                OutputStream out,
                                String comment) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(out, CHARSET);

        properties.store(writer, comment);
        IOUtil.flush(writer);
    }
}
