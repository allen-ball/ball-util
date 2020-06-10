package ball.util.ant.taskdefs;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2020 Allen D. Ball
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ##########################################################################
 */
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskConfigurationChecker;
import lombok.ToString;

import static ball.beans.PropertyMethodEnum.getPropertyName;

/**
 * {@link TaskConfigurationChecker} implmentation to check
 * {@link AnnotatedAntTask} annotations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@ToString
public class AnnotatedAntTaskConfigurationChecker
             extends TaskConfigurationChecker {
    private final Task task;

    /**
     * Sole constructor.
     *
     * @param   task            The Ant {@link Task}.
     */
    public AnnotatedAntTaskConfigurationChecker(Task task) {
        super(task);

        this.task = task;

        HashSet<Class<?>> set = new HashSet<>();

        set.add(task.getClass());
        set.addAll(ClassUtils.getAllSuperclasses(task.getClass()));
        set.addAll(ClassUtils.getAllInterfaces(task.getClass()));

        for (Class<?> type : set) {
            ArrayList<AnnotatedElement> list = new ArrayList<>();

            Collections.addAll(list, type.getDeclaredFields());
            Collections.addAll(list, type.getDeclaredMethods());

            for (AnnotatedElement element : list) {
                for (Annotation annotation : element.getAnnotations()) {
                    AntTaskAttributeConstraint constraint =
                        annotation.annotationType()
                        .getAnnotation(AntTaskAttributeConstraint.class);

                    if (constraint != null) {
                        assertConfig(constraint, element);
                    }
                }
            }
        }
    }

    private void assertConfig(AntTaskAttributeConstraint constraint,
                              AnnotatedElement element) {
        try {
            String name = null;
            Object value = null;

            if (element instanceof Field) {
                name = ((Field) element).getName();
                value = FieldUtils.readField((Field) element, task, true);
            } else if (element instanceof Method) {
                name = getPropertyName((Method) element);
                value =
                    MethodUtils.invokeMethod(task, true,
                                             ((Method) element).getName(),
                                             new Object[] {  });
            } else {
                throw new IllegalStateException(String.valueOf(element));
            }

            constraint.value()
                .getDeclaredConstructor().newInstance()
                .check(task, this, name, value);
        } catch (Exception exception) {
            fail(exception.toString());
        }
    }
}
