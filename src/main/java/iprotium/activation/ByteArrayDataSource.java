/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * {@link javax.activation.DataSource} implementation based on
 * {@link ByteArrayInputStream} and {@link ByteArrayInputStream}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class ByteArrayDataSource extends AbstractDataSource {
    private ByteArrayOutputStream out = null;

    /**
     * @param   name            Initial {@code "Name"} attribute value.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     */
    public ByteArrayDataSource(String name, String type) {
        super();

        setName(name);

        if (type != null) {
            setContentType(type);
        }
    }

    {
        try {
            clear();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * See {@link ByteArrayOutputStream#size()}.
     */
    public int size() { return out.size(); }

    @Override
    public long length() { return size(); }

    @Override
    public ByteArrayInputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayOutputStream getOutputStream() throws IOException {
        ByteArrayOutputStream out = null;

        synchronized (this) {
            out = new ByteArrayOutputStream(8 * 1024);
            this.out = out;
        }

        return out;
    }
}
