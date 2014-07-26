/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.annotation.AntTask;
import org.apache.tools.ant.Task;

/**
 * Interface indicating {@link Task} is annotated with {@link AntTask} and
 * related annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface AnnotatedTask {

    /**
     * {@link Implementation} class instance.
     *
     * @see Implementation
     */
    public final Implementation IMPL = new Implementation();

    /**
     * Method to get {@link AntTask#value()}.
     *
     * @return  {@link AntTask#value()} if {@code this} {@link Task} is
     *          annotated; {@code null} otherwise.
     *
     * @see Implementation#getAntTaskName(Class)
     */
    public String getAntTaskName();

    /**
     * {@link AnnotatedTask} helper class.
     */
    public static class Implementation {
        private Implementation() { }

        /**
         * See {@link AnnotatedTask#getAntTaskName()}.
         *
         * @param       type    The implementing {@link Class}.
         *
         * @return      {@link AntTask#value()} if the {@link Class} is
         *          annotated; {@code null} otherwise.
         */
        public String getAntTaskName(Class<? extends Task> type) {
            AntTask annotation = type.getAnnotation(AntTask.class);

            return (annotation != null) ? annotation.value() : null;
        }
    }
}
