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
public interface AnnotatedAntTask {

    /**
     * Method to get {@link AntTask#value()}.
     *
     * @return  {@link AntTask#value()} if {@code this} {@link Task} is
     *          annotated; {@code null} otherwise.
     */
    default String getAntTaskName() {
        AntTask annotation = getClass().getAnnotation(AntTask.class);

        return (annotation != null) ? annotation.value() : null;
    }

    /**
     * Method to get check attributes annotated with
     * {@link AntTaskAttributeConstraint}.
     *
     * @throws  BuildException  If a {@link AntTaskAttributeConstraint}
     *                          {@link AntTaskAttributeValidator} fails.
     */
    default void validate() throws BuildException {
        for (Class<?> type : new SuperclassSet(getClass())) {
            ArrayList<AnnotatedElement> list = new ArrayList<>();

            Collections.addAll(list, type.getDeclaredFields());
            Collections.addAll(list, type.getDeclaredMethods());

            for (AnnotatedElement element : list) {
                for (Annotation annotation : element.getAnnotations()) {
                    AntTaskAttributeConstraint constraint =
                        annotation.annotationType()
                        .getAnnotation(AntTaskAttributeConstraint.class);

                    if (constraint != null) {
                        try {
                            String name = null;
                            Object value = null;

                            if (element instanceof Field) {
                                name = ((Field) element).getName();
                                value = ((Field) element).get((Task) this);
                            } else if (element instanceof Method) {
                                name = getPropertyName((Method) element);
                                value = ((Method) element).invoke((Task) this);
                            } else {
                                throw new IllegalStateException();
                            }

                            constraint
                                .value().newInstance()
                                .validate((Task) this, name, value);
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
}
