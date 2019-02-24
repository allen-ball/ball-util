/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import lombok.NoArgsConstructor;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskConfigurationChecker;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static lombok.AccessLevel.PROTECTED;

/**
 * JSR303-inspired Ant Task attribute constraint annotation.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
public @interface AntTaskAttributeConstraint {
    Class<? extends AntTaskAttributeConstraint.Checker> value();

    /**
     * {@link #value()} base class
     */
    @NoArgsConstructor(access = PROTECTED)
    public static abstract class Checker {

        /**
         * Method to validate an attribute value.
         *
         * @param   task        The {@link Task} that owns and is validating
         *                      the attribute.
         * @param   checker     The {@link TaskConfigurationChecker} to
         *                      report violations.
         * @param   name        The name of the attribute.
         * @param   value       The attribute value.
         */
        protected abstract void check(Task task,
                                      TaskConfigurationChecker checker,
                                      String name, Object value);
    }
}
