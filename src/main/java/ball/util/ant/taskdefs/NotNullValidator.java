/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * {@link NotNull} {@link AntTaskAttributeValidator}
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor @ToString
public class NotNullValidator extends AntTaskAttributeValidator {
    @Override
    protected void validate(Task task,
                            String name, Object value) throws BuildException {
        if (value == null) {
            throw new BuildException("`" + name
                                     + "' attribute must not be null");
        }
    }
}
