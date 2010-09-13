/*
 * $Id: ByteArrayDataSource.java,v 1.8 2010-09-13 04:30:22 ball Exp $
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
 * @version $Revision: 1.8 $
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
            reset();
        } catch (Exception exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    /**
     * Method to reset the internal byte array and discard any input on any
     * open {@link #getOutputStream()}.
     */
    public void reset() {
        try {
            getOutputStream().close();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

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
 * Revision 1.7  2010/09/09 03:10:44  ball
 * Changed reset() implementation to call getOutputStream().close().
 *
 * Revision 1.6  2010/09/08 06:41:08  ball
 * Added size() method.
 *
 */
