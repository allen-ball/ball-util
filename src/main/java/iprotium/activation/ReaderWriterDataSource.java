/*
 * $Id: ReaderWriterDataSource.java,v 1.5 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import iprotium.io.IOUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * {@link javax.activation.DataSource} implementation that provides a
 * {@link BufferedReader} wrapping the {@link InputStream} and a
 * {@link PrintWriter} wrapping the {@link OutputStream}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class ReaderWriterDataSource extends ByteArrayDataSource
                                    implements Iterable<String> {
    protected static final Charset CHARSET = Charset.forName("UTF-8");

    private final Charset charset;

    /**
     * @see ByteArrayDataSource#ByteArrayDataSource(String,String)
     *
     * @param   name            Initial "Name" attribute value.
     * @param   type            Initial "ContentType" attribute value.
     */
    public ReaderWriterDataSource(String name, String type) {
        this(name, type, CHARSET);
    }

    /**
     * @see ByteArrayDataSource#ByteArrayDataSource(String,String)
     *
     * @param   name            Initial "Name" attribute value.
     * @param   type            Initial "ContentType" attribute value.
     * @param   charset         The {@link Charset} used to encode the
     *                          {@link OutputStream}.
     */
    public ReaderWriterDataSource(String name, String type, Charset charset) {
        super(name, type);

        if (charset != null) {
            this.charset = charset;
        } else {
            throw new IllegalArgumentException("charset=" + nameOf(charset));
        }
    }

    /**
     * Method to get the {@link Charset} used to create the
     * {@link BufferedReader} and {@link PrintWriter}.
     *
     * @return  The Charset.
     */
    public Charset getCharset() { return charset; }

    /**
     * Method to return a new {@link BufferedReader} to read the underlying
     * {@link InputStream}.
     *
     * @see #getInputStream()
     *
     * @return  A {@link BufferedReader} wrapping the
     *          {@link javax.activation.DataSource} {@link InputStream}.
     */
    public BufferedReader getBufferedReader() throws IOException {
        return new BufferedReaderImpl();
    }

    /**
     * Method to return a new {@link PrintWriter} to write to the underlying
     * {@link OutputStream}.
     *
     * @see #getOutputStream()
     *
     * @return  A {@link PrintWriter} wrapping the
     *          {@link javax.activation.DataSource} {@link OutputStream}.
     */
    public PrintWriter getPrintWriter() throws IOException {
        return new PrintWriterImpl();
    }

    /**
     * Method to write the contents of this
     * {@link javax.activation.DataSource} to a {@link PrintWriter}.
     *
     * @see #getBufferedReader()
     *
     * @param   writer          The target {@link PrintWriter}.
     *
     * @throws  IOException     If a problem is encountered opening or
     *                          reading the {@link BufferedReader} or
     *                          writing to the {@link PrintWriter}.
     */
    public void writeTo(PrintWriter writer) throws IOException {
        BufferedReader reader = null;

        try {
            reader = getBufferedReader();

            IOUtil.copy(reader, writer);
        } finally {
            try {
                IOUtil.close(reader);
            } finally {
                reader = null;
            }
        }
    }

    /**
     * Method to return an {@link Iterator} to access the lines of this
     * {@link javax.activation.DataSource}.
     *
     * @see #getBufferedReader()
     *
     * @return  An {@link Iterator} to access the lines of the report.
     */
    public Iterator<String> iterator() { return new IteratorImpl(); }

    @Override
    public String toString() {
        String string = null;

        try {
            string = new String(toByteArray(), nameOf(getCharset()));
        } catch (UnsupportedEncodingException exception) {
            string = super.toString();
        }

        return string;
    }

    /**
     * Convenience method to get the name of a {@link Charset}.
     *
     * @param   charset         The {@link Charset}.
     *
     * @return  The name of the {@link Charset} if non-<code>null</code>;
     *          <code>null</code> otherwise.
     *
     * @see Charset#name()
     */
    protected static String nameOf(Charset charset) {
        return (charset != null) ? charset.name() : null;
    }

    private class BufferedReaderImpl extends BufferedReader {
        public BufferedReaderImpl() throws IOException {
            super(new InputStreamReader(getInputStream(), getCharset()));
        }
    }

    private class PrintWriterImpl extends PrintWriter {
        public PrintWriterImpl() throws IOException {
            super(new OutputStreamWriter(getOutputStream(), getCharset()),
                  true);
        }
    }

    private class IteratorImpl implements Iterator<String> {
        private final BufferedReader reader;
        private transient String line = null;

        public IteratorImpl() {
            try {
                reader = getBufferedReader();
            } catch (IOException exception) {
                throw new ExceptionInInitializerError(exception);
            }
        }

        public boolean hasNext() {
            if (line == null) {
                try {
                    line = reader.readLine();
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }

            return (line != null);
        }

        public String next() {
            hasNext();

            String line = this.line;

            this.line = null;

            if (line == null) {
                throw new NoSuchElementException();
            }

            return line;
        }

        public void remove() {
            throw new UnsupportedOperationException("remove()");
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
