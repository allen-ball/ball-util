/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Interface indicating {@link Task} is annotated with {@link AntTask} and
 * related annotations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface AnnotatedAntTask extends AntTaskLogMethods {

    /**
     * Method to get {@link AntTask#value()}.
     *
     * @return  {@link AntTask#value()} if {@link.this} {@link Task} is
     *          annotated; {@code null} otherwise.
     */
    default String getAntTaskName() {
        AntTask annotation = getClass().getAnnotation(AntTask.class);

        return (annotation != null) ? annotation.value() : null;
    }

    /**
     * Default implementation for {@link Task} subclasses.  Check attributes
     * annotated with {@link AntTaskAttributeConstraint}.
     * See {@link AnnotatedAntTaskConfigurationChecker}.
     *
     * @throws  BuildException  If a
     *                          {@link AntTaskAttributeConstraint.Checker}
     *                          fails.
     */
    default void execute() throws BuildException {
        new AnnotatedAntTaskConfigurationChecker((Task) this).checkErrors();
    }
}
