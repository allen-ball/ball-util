/*
 * $Id: PrintWriterDataSource.java,v 1.1 2009-03-27 22:22:49 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * DataSource implementation that provides a PrintWriter interface.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class PrintWriterDataSource extends ByteArrayDataSource {
    private Charset charset = null;

    /**
     * @see ByteArrayDataSource#ByteArrayDataSource(String,String)
     */
    public PrintWriterDataSource(String name, String type) {
        super(name, type);

        setCharset(CHARSET);
    }

    public Charset getCharset() { return charset; }

    public void setCharset(Charset charset) {
        if (charset != null) {
            this.charset = charset;
        } else {
            throw new IllegalArgumentException("charset=" + nameOf(charset));
        }
    }

    protected String nameOf(Charset charset) {
        return (charset != null) ? charset.name() : null;
    }

    /**
     * Method to return a PrintWriter to write to the underlying
     * OutputStream.
     *
     * @see #getOutputStream
     *
     * @return  A PrintWriter.
     */
    public PrintWriter getPrintWriter() throws IOException {
        return new PrintWriterImpl(getOutputStream(), getCharset());
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

    private class PrintWriterImpl extends PrintWriter {
        public PrintWriterImpl(OutputStream out, Charset charset) {
            super(new OutputStreamWriter(out, charset), true);
        }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
