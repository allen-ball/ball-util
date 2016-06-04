/*
 * $Id$
 *
 * Copyright 2015, 2016 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;

/**
 * Abstract {@link.uri http://ant.apache.org/ Ant}
 * {@link org.apache.tools.ant.Task} to specify a type ({@link Class}).
 *
 * {@bean.info}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class TypeTask extends AbstractClasspathTask {
    private String type = null;

    /**
     * Sole constructor.
     */
    protected TypeTask() { super(); }

    @NotNull
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
