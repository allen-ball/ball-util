/*
 * $Id: AbstractDataSource.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
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
 * @version $Revision: 1.3 $
 */
public class AbstractDataSource implements DataSource {
    private String name = null;
    private String type = null;

    /**
     * Sole constructor.
     *
     * @param   name            Initial "Name" attribute value.
     * @param   type            Initial "ContentType" attribute value.
     */
    protected AbstractDataSource(String name, String type) {
        setName(name);
        setContentType(type);
    }

    public String getName() { return name; }
    protected void setName(String name) { this.name = name; }

    public String getContentType() { return type; }
    protected void setContentType(String type) { this.type = type; }

    /**
     * @throws  UnsupportedOperationException
     *                          Always.
     */
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("getInputStream()");
    }

    /**
     * @throws  UnsupportedOperationException
     *                          Always.
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
