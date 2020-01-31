/*
 * $Id$
 *
 * Copyright 2014 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskConfigurationChecker;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AntTaskAttributeConstraint} annotation to indicate that an
 * attribute cannot be {@code null}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@AntTaskAttributeConstraint(NotNull.Checker.class)
@Documented
@Retention(RUNTIME)
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
public @interface NotNull {

    /**
     * {@link AntTaskAttributeConstraint.Checker}.
     */
    @NoArgsConstructor @ToString
    public static class Checker extends AntTaskAttributeConstraint.Checker {
        @Override
        protected void check(Task task, TaskConfigurationChecker checker,
                             String name, Object value) {
            checker.assertConfig(value != null,
                                 "`" + name + "' attribute must not be null");
        }
    }
}
