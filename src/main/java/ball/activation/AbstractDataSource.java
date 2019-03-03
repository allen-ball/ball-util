/*
 * $Id$
 *
 * Copyright 2009 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.activation;

import lombok.NoArgsConstructor;
import lombok.ToString;

import static lombok.AccessLevel.PROTECTED;

/**
 * Abstract base class for {@link javax.activation.DataSource}
 * implementations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED) @ToString
public abstract class AbstractDataSource implements DataSourceDefaultMethods {
    private String name = null;
    private String type = APPLICATION_OCTET_STREAM;

    @Override public String getName() { return name; }
    @Override public void setName(String name) { this.name = name; }

    @Override public String getContentType() { return type; }
    @Override public void setContentType(String type) { this.type = type; }
}
