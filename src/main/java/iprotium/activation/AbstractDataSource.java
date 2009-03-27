/*
 * $Id: AbstractDataSource.java,v 1.1 2009-03-27 22:22:49 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.activation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

/**
 * Abstract base class for DataSource implementations.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class AbstractDataSource implements DataSource {
    private String name = null;
    private String type = null;

    /**
     * Sole constructor.
     *
     * @param   name            Initial "Name" Bean attribute value.
     * @param   type            Initial "ContentType" Bean attribute value.
     */
    protected AbstractDataSource(String name, String type) {
        setName(name);
        setContentType(type);
    }

    public String getName() { return name; }
    protected void setName(String name) { this.name = name; }

    public String getContentType() { return type; }
    protected void setContentType(String type) { this.type = type; }

    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("getInputStream()");
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("getOutputStream()");
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
