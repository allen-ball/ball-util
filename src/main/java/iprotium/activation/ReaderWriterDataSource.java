/*
 * $Id: ReaderWriterDataSource.java,v 1.2 2009-03-29 13:48:46 ball Exp $
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

/**
 * DataSource implementation that provides a BufferedReader wrapping the
 * InputStream and a PrintWriter wrapping the OutputStream.
 *
 * @see BufferedReader
 * @see PrintWriter
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class ReaderWriterDataSource extends ByteArrayDataSource {
    protected static final Charset CHARSET = Charset.forName("UTF-8");

    private final Charset charset;

    /**
     * @see ByteArrayDataSource#ByteArrayDataSource(String,String)
     */
    public ReaderWriterDataSource(String name, String type) {
        this(name, type, CHARSET);
    }

    /**
     * @see ByteArrayDataSource#ByteArrayDataSource(String,String)
     *
     * @param   charset         The Charset used to encode the
     *                          OutputStream.
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
     * Method to get the Charset used to create the BufferedReader and
     * PrintWriter.
     *
     * @return  The Charset.
     */
    public Charset getCharset() { return charset; }

    /**
     * Method to return a new BufferedReader to read the underlying
     * InputStream.
     *
     * @see #getInputStream()
     *
     * @return  A BufferedReader wrapping the DataSource InputStream.
     */
    public BufferedReader getBufferedReader() throws IOException {
        return new BufferedReaderImpl();
    }

    /**
     * Method to return a new PrintWriter to write to the underlying
     * OutputStream.
     *
     * @see #getOutputStream()
     *
     * @return  A PrintWriter wrapping the DataSource OutputStream.
     */
    public PrintWriter getPrintWriter() throws IOException {
        return new PrintWriterImpl();
    }

    /**
     * Method to write the contents of this DataSource to a PrintWriter.
     *
     * @see #getBufferedReader()
     *
     * @param   writer          The target PrintWriter.
     *
     * @throws  IOException     If a problem is encountered opening or
     *                          reading the BufferedReader or writing to the
     *                          PrintWriter.
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

    @Override
    public String toString() {
        String string = null;

        try {
            string = toString(nameOf(getCharset()));
        } catch (UnsupportedEncodingException exception) {
            string = super.toString();
        }

        return string;
    }

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
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2009/03/29 13:08:08  ball
 * Initial writing (renamed from PrintWriterDataSource).
 * Added getBufferedReader() and writeTo(PrintWriter) methods.
 *
 */
