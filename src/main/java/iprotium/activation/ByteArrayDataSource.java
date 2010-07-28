/*
 * $Id: ByteArrayDataSource.java,v 1.5 2010-07-28 04:43:42 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * {@link javax.activation.DataSource} implementation based on
 * {@link ByteArrayInputStream} and {@link ByteArrayInputStream}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class ByteArrayDataSource extends AbstractDataSource {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     * @see AbstractDataSource#AbstractDataSource()
     *
     * @param   name            Initial "Name" attribute value.
     */
    public ByteArrayDataSource(String name, String type) {
        super();

        setName(name);

        if (type != null) {
            setContentType(type);
        }

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
     * See {@link ByteArrayOutputStream#toByteArray()}.
     */
    public byte[] toByteArray() {
        return (out != null) ? out.toByteArray() : new byte[] { };
    }

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

    @Override
    public int hashCode() {
        int hash = 0;

        if (getName() != null) {
            hash ^= getName().hashCode();
        }

        if (getContentType() != null) {
            hash ^= getContentType().hashCode();
        }

        hash ^= ByteBuffer.wrap(toByteArray()).hashCode();

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        return (object instanceof ByteArrayDataSource
                && equals((ByteArrayDataSource) object));
    }

    private boolean equals(ByteArrayDataSource that) {
        return (this == that
                || (equals(this.getName(), that.getName())
                    && equals(this.getContentType(), that.getContentType())
                    && (this.hashCode() == that.hashCode())));
    }

    private <T> boolean equals(T left, T right) {
        return (left != null) ? left.equals(right) : (left == right);
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
