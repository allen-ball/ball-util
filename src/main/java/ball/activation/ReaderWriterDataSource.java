/*
 * $Id$
 *
 * Copyright 2009 - 2014 Allen D. Ball.  All rights reserved.
 */
package ball.activation;

import ball.io.IOUtil;
import java.beans.ConstructorProperties;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static ball.util.StringUtil.NIL;

/**
 * {@link javax.activation.DataSource} implementation that provides a
 * {@link BufferedReader} wrapping the {@link javax.activation.DataSource}
 * {@link InputStream} and a {@link PrintWriter} wrapping the
 * {@link javax.activation.DataSource} {@link OutputStream}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class ReaderWriterDataSource extends FilterDataSource
                                    implements Iterable<String> {
    protected static final Charset CHARSET = Charset.forName("UTF-8");

    private final Charset charset;

    /**
     * @param   name            Initial {@code "Name"} attribute value.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     */
    @ConstructorProperties({ "name", "contentType" })
    public ReaderWriterDataSource(String name, String type) {
        this(name, type, null);
    }

    /**
     * @param   name            Initial {@code "Name"} attribute value.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     * @param   charset         The {@link Charset} used to encode the
     *                          {@link OutputStream}.
     */
    @ConstructorProperties({ "name", "contentType", "charset" })
    public ReaderWriterDataSource(String name, String type, Charset charset) {
        this(name, type, charset, null);
    }

    /**
     * @param   name            Initial {@code "Name"} attribute value.
     * @param   type            Initial {@code "ContentType"} attribute
     *                          value.
     * @param   charset         The {@link Charset} used to encode the
     *                          {@link OutputStream}.
     */
    @ConstructorProperties({ "name", "contentType", "charset", NIL })
    public ReaderWriterDataSource(String name, String type,
                                  Charset charset, String content) {
        super(new ByteArrayDataSource(name, type));

        this.charset = (charset != null) ? charset : CHARSET;

        if (content != null) {
            Reader reader = null;
            Writer writer = null;

            try {
                reader = new StringReader(content);
                writer = getWriter();

                IOUtil.copy(reader, getWriter());
            } catch (IOException exception) {
                throw new ExceptionInInitializerError(exception);
            } finally {
                IOUtil.close(reader);
                IOUtil.close(writer);
            }
        }
    }

    /**
     * Private no-argument constructor (for JAXB annotated subclasses).
     */
    private ReaderWriterDataSource() { this(null, null); }

    /**
     * Method to get the {@link Charset} used to create the
     * {@link BufferedReader} and {@link PrintWriter}.
     *
     * @return  The Charset.
     */
    public Charset getCharset() { return charset; }

    /**
     * Method to return a new {@link Reader} to read the underlying
     * {@link InputStream}.
     *
     * @see #getInputStream()
     *
     * @return  A {@link Reader} wrapping the
     *          {@link javax.activation.DataSource} {@link InputStream}.
     */
    public Reader getReader() throws IOException {
        return new InputStreamReader(getInputStream(), getCharset());
    }

    /**
     * Method to return a new {@link Writer} to write to the underlying
     * {@link OutputStream}.
     *
     * @see #getOutputStream()
     *
     * @return  A {@link Writer} wrapping the
     *          {@link javax.activation.DataSource} {@link OutputStream}.
     */
    public Writer getWriter() throws IOException {
        return new OutputStreamWriter(getOutputStream(), getCharset());
    }

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
        return new BufferedReader(getReader());
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
        return new PrintWriter(getWriter(), true);
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
    @Override
    public Iterator<String> iterator() { return new IteratorImpl(); }

    @Override
    public String toString() {
        String string = null;
        Reader reader = null;
        StringWriter writer = null;

        try {
            reader = getReader();
            writer = new StringWriter();
            IOUtil.copy(reader, writer);
            string = writer.toString();
        } catch (IOException exception) {
            string = super.toString();
        } finally {
            IOUtil.close(reader);
            IOUtil.close(writer);
        }

        return string;
    }

    /**
     * Convenience method to get the name of a {@link Charset}.
     *
     * @param   charset         The {@link Charset}.
     *
     * @return  The name of the {@link Charset} if non-{@code null};
     *          {@code null} otherwise.
     *
     * @see Charset#name()
     */
    protected static String nameOf(Charset charset) {
        return (charset != null) ? charset.name() : null;
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

        @Override
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

        @Override
        public String next() {
            hasNext();

            String line = this.line;

            this.line = null;

            if (line == null) {
                throw new NoSuchElementException();
            }

            return line;
        }

        @Override
        public void remove() { throw new UnsupportedOperationException(); }
    }
}
