/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import static ball.beans.PropertyMethodEnum.getPropertyName;
import static lombok.AccessLevel.PROTECTED;

/**
 * {@link AntTaskAttributeConstraint} validator base class.
 *
 * @see AntTaskAttributeConstraint#value()
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@NoArgsConstructor(access = PROTECTED)
public abstract class AntTaskAttributeValidator {

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
    protected abstract void validate(Task task,
                                     String name,
                                     Object value) throws BuildException;

    /**
     * Method to validate an annotated {@link Field}.
     *
     * @param   task            The {@link Task} that owns and is validating
     *                          the attribute.
     * @param   field           The {@link Field}.
     *
     * @throws  BuildException  If the validation fails.
     */
    public void validate(Task task, Field field) throws BuildException {
        try {
            validate(task,
                     field.getName(),
                     FieldUtils.readField(field, task, true));
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }
    }

    /**
     * Method to validate an annotated {@link Method}.
     *
     * @param   task            The {@link Task} that owns and is validating
     *                          the attribute.
     * @param   method          The {@link Method}.
     *
     * @throws  BuildException  If the validation fails.
     */
    public void validate(Task task, Method method) throws BuildException {
        try {
            validate(task,
                     getPropertyName(method),
                     MethodUtils.invokeMethod(this, true,
                                              method.getName(),
                                              new Object[] {  }));
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new BuildException(exception);
        }
    }
}
