/*
 * $Id$
 *
 * Copyright 2014 - 2018 Allen D. Ball.  All rights reserved.
 */
package ball.util.ant.taskdefs;

import ball.util.SuperclassSet;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import static ball.util.BeanPropertyMethodEnum.getPropertyName;

/**
 * Interface indicating {@link Task} is annotated with {@link AntTask} and
 * related annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public interface AnnotatedTask {

    /**
     * {@link Delegate} class instance.
     *
     * @see Delegate
     */
    public static final Delegate DELEGATE = new Delegate();

    /**
     * Method to get {@link AntTask#value()}.
     *
     * @return  {@link AntTask#value()} if {@code this} {@link Task} is
     *          annotated; {@code null} otherwise.
     *
     * @see Delegate#getAntTaskName(Task)
     */
    public String getAntTaskName();

    /**
     * Method to get check attributes annotated with
     * {@link AntTaskAttributeConstraint}.
     *
     * @throws  BuildException  If a {@link AntTaskAttributeConstraint}
     *                          {@link AntTaskAttributeValidator} fails.
     *
     * @see Delegate#validate(Task)
     */
    public void validate() throws BuildException;

    /**
     * {@link AnnotatedTask} delegate helper class.
     */
    public static class Delegate {
        private Delegate() { }

        /**
         * See {@link AnnotatedTask#getAntTaskName()}.
         *
         * @param       task    The {@link Task}.
         *
         * @return      {@link AntTask#value()} if the {@link Task} is
         *              annotated; {@code null} otherwise.
         */
        public String getAntTaskName(Task task) {
            AntTask annotation = task.getClass().getAnnotation(AntTask.class);

            return (annotation != null) ? annotation.value() : null;
        }

        /**
         * See {@link AnnotatedTask#validate()}.
         *
         * @param       task    The {@link Task} to validate.
         *
         * @throws      BuildException
         *                      If a {@link AntTaskAttributeConstraint}
         *                      {@link AntTaskAttributeValidator} fails.
         */
        public void validate(Task task) throws BuildException {
            for (Class<?> type : new SuperclassSet(task.getClass())) {
                ArrayList<AnnotatedElement> list = new ArrayList<>();

                Collections.addAll(list, type.getDeclaredFields());
                Collections.addAll(list, type.getDeclaredMethods());

                for (AnnotatedElement element : list) {
                    validate(task, element, element.getAnnotations());
                }
            }
        }

        private void validate(Task task,
                              AnnotatedElement element,
                              Annotation... annotations) throws BuildException {
            for (Annotation annotation : annotations) {
                AntTaskAttributeConstraint constraint =
                    annotation.annotationType()
                    .getAnnotation(AntTaskAttributeConstraint.class);

                if (constraint != null) {
                    try {
                        String name = null;
                        Object value = null;

                        if (element instanceof Field) {
                            name = ((Field) element).getName();
                            value = ((Field) element).get(task);
                        } else if (element instanceof Method) {
                            name = getPropertyName((Method) element);
                            value = ((Method) element).invoke(task);
                        } else {
                            throw new IllegalStateException();
                        }

                        constraint
                            .value().newInstance()
                            .validate(task, name, value);
                    } catch (BuildException exception) {
                        throw exception;
                    } catch (RuntimeException exception) {
                        throw exception;
                    } catch (Exception exception) {
                        throw new RuntimeException(exception);
                    }
                }
            }
        }
    }
}
