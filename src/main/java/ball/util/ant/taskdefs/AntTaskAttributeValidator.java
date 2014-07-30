/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * {@link AntTaskAttributeConstraint} validator base class.

 * @see AntTaskAttributeConstraint#value()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class AntTaskAttributeValidator {

    /**
     * Sole constructor.
     */
    protected AntTaskAttributeValidator() { }

    /**
     * Method to validate an attribute value.
     *
     * @param   task            The {@link Task} that owns and is validating
     *                          the attribute.
     * @param   name            The name of the attribute.
     * @param   value           The attribute value.
     *
     * @throws  BuildException  If the validation fails.
     */
    public abstract void validate(Task task,
                                  String name,
                                  Object value) throws BuildException;
}
