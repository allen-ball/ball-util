/*
 * $Id: ByteArrayDataSource.java,v 1.3 2009-03-30 05:21:49 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * DataSource implementation based on ByteArrayInputStream and
 * ByteArrayInputStream.
 *
 * @see ByteArrayInputStream
 * @see ByteArrayOutputStream
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class ByteArrayDataSource extends AbstractDataSource {
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    /**
     * @see AbstractDataSource#AbstractDataSource(String,String)
     */
    public ByteArrayDataSource(String name, String type) { super(name, type); }

    /**
     * @see ByteArrayOutputStream#reset()
     */
    public void reset() { out.reset(); }

    /**
     * @see ByteArrayOutputStream#toByteArray()
     */
    public byte[] toByteArray() { return out.toByteArray(); }

    @Override
    public ByteArrayInputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(toByteArray());
    }

    @Override
    public ByteArrayOutputStream getOutputStream() throws IOException {
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
