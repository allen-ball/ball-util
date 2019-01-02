/*
 * $Id$
 *
 * Copyright 2009 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import javax.activation.DataSource;
import org.apache.commons.io.IOUtils;

/**
 * Abstract base class for {@link DataSource} implementations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractDataSource implements DataSource {

    /**
     * {@link.rfc 2045} {@value #CONTENT_TYPE}
     */
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * {@link.rfc 2045} {@value #APPLICATION_OCTET_STREAM}
     */
    public static final String APPLICATION_OCTET_STREAM =
        "application/octet-stream";

    /**
     * {@link.rfc 3023} {@value #APPLICATION_XML}
     */
    public static final String APPLICATION_XML = "application/xml";

    /**
     * {@link.rfc 2045} {@value #TEXT_PLAIN}
     */
    public static final String TEXT_PLAIN = "text/plain";

    /**
     * {@link.rfc 2045} {@value #TEXT_HTML}
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
        try {
            getOutputStream().close();
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
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
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public InputStream getInputStream(Class<?>... types) throws IOException {
        return wrap(getInputStream(), types);
    }

    /**
     * Method to "wrap" the {@link OutputStream} returned by
     * {@link #getOutputStream()}into {@link OutputStream} instances.
     *
     * @param   types           The {@link OutputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link OutputStream}.
     *
     * @throws  IOException     If an I/O error occurs.
     */
    public OutputStream getOutputStream(Class<?>... types) throws IOException {
        return wrap(getOutputStream(), types);
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
        try (InputStream in = getInputStream()) {
            IOUtils.copy(in, out);
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

    @Override
    public String toString() { return super.toString(); }

    /**
     * Method to "wrap" an {@link InputStream} into {@link InputStream}
     * instances.
     *
     * @param   in              The {@link InputStream}.
     * @param   types           The {@link InputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link InputStream}.
     *
     * @throws  IOException     If any of the wrapping streams throw an
     *                          {@link IOException}.
     */
    protected static InputStream wrap(InputStream in,
                                      Class<?>... types) throws IOException {
        try {
            for (Class<?> type : types) {
                in =
                    type.asSubclass(InputStream.class)
                    .getConstructor(InputStream.class)
                    .newInstance(in);
            }
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new IllegalArgumentException(exception);
            }
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }

        return in;
    }

    /**
     * Method to "wrap" an {@link OutputStream} into {@link OutputStream}
     * instances.
     *
     * @param   out             The {@link OutputStream}.
     * @param   types           The {@link OutputStream} implementation
     *                          {@link Class}es.
     *
     * @return  The "wrapped" {@link OutputStream}.
     *
     * @throws  IOException     If any of the wrapping streams throw an
     *                          {@link IOException}.
     */
    protected static OutputStream wrap(OutputStream out,
                                       Class<?>... types) throws IOException {
        try {
            for (Class<?> type : types) {
                out =
                    type.asSubclass(OutputStream.class)
                    .getConstructor(OutputStream.class)
                    .newInstance(out);
            }
        } catch (InvocationTargetException exception) {
            Throwable cause = exception.getCause();

            if (cause instanceof IOException) {
                throw (IOException) cause;
            } else if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else if (cause instanceof Error) {
                throw (Error) cause;
            } else {
                throw new IllegalArgumentException(exception);
            }
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }

        return out;
    }
}
