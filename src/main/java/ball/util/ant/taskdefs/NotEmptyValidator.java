/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.util.Collection;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * {@link NotEmpty} {@link AntTaskAttributeValidator}
 *
 * @see Collection#isEmpty()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class NotEmptyValidator extends AntTaskAttributeValidator {

    /**
     * Sole constructor.
     */
    public NotEmptyValidator() { super(); }

    @Override
    public void validate(Task task,
                         String name, Object value) throws BuildException {
        if (((Collection) value).isEmpty()) {
            throw new BuildException("`" + name
                                     + "' attribute must not be empty");
        }
    }
}
