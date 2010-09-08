/*
 * $Id: ByteArrayDataSource.java,v 1.6 2010-09-08 06:41:08 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
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
 * @version $Revision: 1.6 $
 */
public class ByteArrayDataSource extends AbstractDataSource {
    private ByteArrayOutputStream out = null;

    /**
     * @see AbstractDataSource#AbstractDataSource()
     *
     * @param   name            Initial "Name" attribute value.
     * @param   type            Initial "ContentType" attribute value.
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
            getOutputStream().close();
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * See {@link ByteArrayOutputStream#reset()}.
     */
    public void reset() { out.reset(); }

    /**
     * See {@link ByteArrayOutputStream#size()}.
     */
    public int size() { return out.size(); }

    /**
     * See {@link ByteArrayOutputStream#toByteArray()}.
     */
    public byte[] toByteArray() { return out.toByteArray(); }

    @Override
    public ByteArrayInputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(toByteArray());
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
/*
 * $Log: not supported by cvs2svn $
 */
