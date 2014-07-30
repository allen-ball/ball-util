/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * {@link NotNull} {@link AntTaskAttributeValidator}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class NotNullValidator extends AntTaskAttributeValidator {

    /**
     * Sole constructor.
     */
    public NotNullValidator() { super(); }

    @Override
    public void validate(Task task,
                         String name, Object value) throws BuildException {
        if (value == null) {
            throw new BuildException("`" + name
                                     + "' attribute must not be null");
        }
    }
}
