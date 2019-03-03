/*
 * $Id$
 *
 * Copyright 2010 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.activation;

import java.beans.ConstructorProperties;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import javax.activation.DataSource;

/**
 * Abstract {@link DataSource} base class that wraps another
 * {@link DataSource}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class FilterDataSource extends AbstractDataSource {
    private final DataSource ds;

    /**
     * @param   ds              The filtered {@link DataSource}.
     */
    @ConstructorProperties({ "dataSource" })
    protected FilterDataSource(DataSource ds) {
        super();

        this.ds = Objects.requireNonNull(ds);
    }

    /**
     * Private no-argument constructor (for JAXB annotated subclasses).
     */
    private FilterDataSource() { this(new ByteArrayDataSource(null, null)); }

    /**
     * Method to get the filtered {@link DataSource}.
     *
     * @return  The filtered {@link DataSource}.
     */
    protected DataSource getDataSource() { return ds; }

    @Override
    public String getName() { return getDataSource().getName(); }

    @Override
    public void setName(String name) {
        try {
            DataSource ds = getDataSource();

            ds.getClass()
                .getMethod("setName", String.class)
                .invoke(ds, name);
        } catch (Exception exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    @Override
    public String getContentType() { return getDataSource().getContentType(); }

    @Override
    public void setContentType(String type) {
        try {
            DataSource ds = getDataSource();

            ds.getClass()
                .getMethod("setContentType", String.class)
                .invoke(ds, type);
        } catch (Exception exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getDataSource().getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return getDataSource().getOutputStream();
    }
}
