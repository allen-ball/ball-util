/*
 * $Id$
 *
 * Copyright 2010, 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import iprotium.io.IOUtil;
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
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class PropertiesImpl extends Properties {
    private static final long serialVersionUID = -8648911923690345842L;

    private static Charset CHARSET = Charset.forName("UTF-8");

    /**
     * See {@link Properties#Properties()}.
     */
    public PropertiesImpl() { this(null); }

    /**
     * See {@link Properties#Properties(Properties)}.
     */
    public PropertiesImpl(Properties defaults) { super(defaults); }

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
