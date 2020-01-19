/*
 * $Id$
 *
 * Copyright 2014 - 2020 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
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
 * attribute cannot be an empty {@link java.util.Collection}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@NotNull
@AntTaskAttributeConstraint(NotEmpty.Checker.class)
@Documented
@Retention(RUNTIME)
@Target({ ANNOTATION_TYPE, FIELD, METHOD })
public @interface NotEmpty {

    /**
     * {@link AntTaskAttributeConstraint.Checker}.
     */
    @NoArgsConstructor @ToString
    public static class Checker extends AntTaskAttributeConstraint.Checker {
        @Override
        protected void check(Task task, TaskConfigurationChecker checker,
                             String name, Object value) {
            checker.assertConfig(! (((Collection) value).isEmpty()),
                                 "`" + name + "' attribute must not be empty");
        }
    }
}
