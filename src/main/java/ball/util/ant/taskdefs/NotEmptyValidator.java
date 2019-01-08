/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.util.Collection;
import lombok.NoArgsConstructor;
import lombok.ToString;
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
@NoArgsConstructor @ToString
public class NotEmptyValidator extends AntTaskAttributeValidator {
    @Override
    protected void validate(Task task,
                            String name, Object value) throws BuildException {
        if (((Collection) value).isEmpty()) {
            throw new BuildException("`" + name
                                     + "' attribute must not be empty");
        }
    }
}
