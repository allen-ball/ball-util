/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.activation;

import java.beans.ConstructorProperties;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link javax.activation.DataSource} implementation based on
 * {@link ByteArrayInputStream} and {@link ByteArrayOutputStream}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public class ByteArrayDataSource extends AbstractDataSource {
    private final ByteArrayOutputStreamImpl out =
        new ByteArrayOutputStreamImpl();

    /**
     * @param   name            Initial {@code "Name"} attribute value.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     */
    @ConstructorProperties({ "name", "contentType" })
    public ByteArrayDataSource(String name, String type) {
        super();

        setName(name);

        if (type != null) {
            setContentType(type);
        }
    }

    @Override
    public void clear() { out.reset(); }

    @Override
    public long length() { return out.size(); }

    @Override
    public InputStream getInputStream() throws IOException {
        return out.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        out.reset();

        return out;
    }

    private class ByteArrayOutputStreamImpl extends ByteArrayOutputStream {
        public ByteArrayOutputStreamImpl() { super(8 * 1024); }

        public ByteArrayInputStream getInputStream() {
            return new ByteArrayInputStream(buf, 0, count);
        }
    }
}
