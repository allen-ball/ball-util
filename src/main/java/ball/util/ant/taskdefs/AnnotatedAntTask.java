/*
 * $Id$
 *
 * Copyright 2014 - 2019 Allen D. Ball.  All rights reserved.
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

/**
 * Interface indicating {@link Task} is annotated with {@link AntTask} and
 * related annotations.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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
     * Default implementation for {@link Task} subclasses.
     */
    default void execute() throws BuildException { validate(); }

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
                            AntTaskAttributeValidator validator =
                                constraint.value().newInstance();

                            if (element instanceof Field) {
                                validator
                                    .validate((Task) this, (Field) element);
                            } else if (element instanceof Method) {
                                validator
                                    .validate((Task) this, (Method) element);
                            } else {
                                throw new IllegalStateException();
                            }
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
