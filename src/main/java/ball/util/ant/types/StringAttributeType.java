/*
 * $Id$
 *
 * Copyright 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.types;

import ball.util.ant.taskdefs.NotNull;
import java.beans.ConstructorProperties;

/**
 * Class to provide a {@link String} name-value (attribute) for
 * {@link.uri http://ant.apache.org/ Ant} {@link org.apache.tools.ant.Task}
 * implementations.
 *
 * {@bean-info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class StringAttributeType extends StringValueType {
    private String name = null;

    /**
     * @param   name            The attribute name.
     */
    @ConstructorProperties({ "name" })
    public StringAttributeType(String name) {
        super();

        this.name = name;
    }

    /**
     * No-argument constructor.
     */
    public StringAttributeType() { this(null); }

    @NotNull
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() { return getName() + "=" + getValue(); }
}
