/*
 * $Id$
 *
 * Copyright 2009 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.activation;

/**
 * Abstract base class for {@link javax.activation.DataSource}
 * implementations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AbstractDataSource implements DataSourceDefaultMethods {
    private String name = null;
    private String type = APPLICATION_OCTET_STREAM;

    /**
     * Sole constructor.
     */
    protected AbstractDataSource() { }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getContentType() { return type; }
    public void setContentType(String type) { this.type = type; }

    @Override
    public String toString() { return super.toString(); }
}
