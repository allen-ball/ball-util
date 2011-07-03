/*
 * $Id$
 *
 * Copyright 2009 - 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import iprotium.io.IOUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

/**
 * Abstract base class for {@link DataSource} implementations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
public class AbstractDataSource implements DataSource {

    /**
     * <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
     * {@value #CONTENT_TYPE}
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
     * {@value #APPLICATION_OCTET_STREAM}
     */
    public static final String APPLICATION_OCTET_STREAM =
        "application/octet-stream";

    /**
     * <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
     * {@value #TEXT_PLAIN}
     */
    public static final String TEXT_PLAIN = "text/plain";

    /**
     * <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>
     * {@value #TEXT_HTML}
     */
    public static final String TEXT_HTML = "text/html";

    private String name = null;
    private String type = APPLICATION_OCTET_STREAM;

    /**
     * Sole constructor.
     */
    protected AbstractDataSource() { }

    /**
     * Method to clear the {@link DataSource} and discard any input on any
     * open {@link #getOutputStream()}.
     *
     * @throws  UnsupportedOperationException
     *                          If {@link #getOutputStream()} throws
     *                          {@link UnsupportedOperationException}.
     */
    public void clear() {
        OutputStream out = null;

        try {
            out = getOutputStream();
            out.close();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        } finally {
            IOUtil.close(out);
        }
    }

    /**
     * Method to get the number of bytes stored in {@code this}
     * {@link DataSource}.  Default implementation returns {@code -1}.
     *
     * @return  The number of bytes stored in {@code this}
     *          {@link DataSource}; {@code -1} if the count is unknown.
     */
    public long length() { return -1; }

    /**
     * Method to "wrap" the {@link InputStream} returned by
     * {@link #getInputStream()}into {@link InputStream} instances.
     *
     * @param   types           The {@link InputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link InputStream}.
     */
    public InputStream getInputStream(Class<?>... types) throws IOException {
        return IOUtil.wrap(getInputStream(), types);
    }

    /**
     * Method to "wrap" the {@link OutputStream} returned by
     * {@link #getOutputStream()}into {@link OutputStream} instances.
     *
     * @param   types           The {@link OutputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link OutputStream}.
     */
    public OutputStream getOutputStream(Class<?>... types) throws IOException {
        return IOUtil.wrap(getOutputStream(), types);
    }

    /**
     * Method to write the contents of this {@link DataSource} to an
     * {@link OutputStream}.
     *
     * @see #getInputStream()
     *
     * @param   out             The target {@link OutputStream}.
     *
     * @throws  IOException     If a problem is encountered opening or
     *                          reading the {@link InputStream} or writing
     *                          to the {@link OutputStream}.
     */
    public void writeTo(OutputStream out) throws IOException {
        InputStream in = null;

        try {
            in = getInputStream();
            IOUtil.copy(in, out);
        } finally {
            try {
                IOUtil.close(in);
            } finally {
                in = null;
            }
        }
    }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getContentType() { return type; }
    public void setContentType(String type) { this.type = type; }

    /**
     * @throws  UnsupportedOperationException
     *                          Unless overridden by subclass
     *                          implementation.
     */
    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws  UnsupportedOperationException
     *                          Unless overridden by subclass
     *                          implementation.
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }
}
