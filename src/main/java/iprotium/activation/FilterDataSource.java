/*
 * $Id$
 *
 * Copyright 2010 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import iprotium.util.BeanUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

/**
 * Abstract {@link DataSource} base class that wraps another
 * {@link DataSource}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public abstract class FilterDataSource extends AbstractDataSource {
    private final DataSource ds;

    /**
     * @param   ds              The filtered {@link DataSource}.
     */
    protected FilterDataSource(DataSource ds) {
        super();

        if (ds != null) {
            this.ds = ds;
        } else {
            throw new NullPointerException("ds");
        }
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
            BeanUtil.set(getDataSource(), "name", name);
        } catch (Exception exception) {
            throw new UnsupportedOperationException(exception);
        }
    }

    @Override
    public String getContentType() { return getDataSource().getContentType(); }

    @Override
    public void setContentType(String type) {
        try {
            BeanUtil.set(getDataSource(), "contentType", type);
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
