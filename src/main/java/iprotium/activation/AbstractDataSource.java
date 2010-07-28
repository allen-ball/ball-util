/*
 * $Id: AbstractDataSource.java,v 1.4 2010-07-28 04:41:29 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
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
 * @version $Revision: 1.4 $
 */
public class AbstractDataSource implements DataSource {

    /**
     * {@value #APPLICATION_OCTET_STREAM}
     */
    public static final String APPLICATION_OCTET_STREAM =
        "application/octet-stream";

    private String name = null;
    private String type = null;

    /**
     * Sole constructor.
     */
    protected AbstractDataSource() {
        setContentType(APPLICATION_OCTET_STREAM);
    }

    public String getName() { return name; }
    protected void setName(String name) { this.name = name; }

    public String getContentType() { return type; }
    protected void setContentType(String type) { this.type = type; }

    /**
     * @throws  UnsupportedOperationException
     *                          Unless overridden by subclass
     *                          implementation.
     */
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("getInputStream()");
    }

    /**
     * @throws  UnsupportedOperationException
     *                          Unless overridden by subclass
     *                          implementation.
     */
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("getOutputStream()");
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
}
/*
 * $Log: not supported by cvs2svn $
 */
