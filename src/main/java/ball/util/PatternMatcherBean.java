package ball.util;
/*-
 * ##########################################################################
 * Utilities
 * $Id$
 * $HeadURL$
 * %%
 * Copyright (C) 2008 - 2021 Allen D. Ball
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
import ball.annotation.MatcherGroup;
import ball.annotation.PatternRegex;
import java.lang.annotation.AnnotationFormatError;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import static ball.util.Converter.convertTo;

/**
 * Interface providing default methods for beans classes annotated with
 * {@link PatternRegex} and whose methods are annotated with
 * {@link MatcherGroup}.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface PatternMatcherBean {

    /**
     * Method to initialize fields and methods annotated with
     * {@link MatcherGroup} with the results of parsing the {@code sequence}
     * argument.
     *
     * @param   sequence        The {@link CharSequence} to parse.
     *
     * @throws  IllegalArgumentException
     *                          If the {@link CharSequence} does not match
     *                          the {@link PatternRegex#value()}.
     */
    default void initialize(CharSequence sequence) {
        Matcher matcher = matcher(sequence);

        if (! matcher.matches()) {
            throw new IllegalArgumentException("\"" + String.valueOf(sequence)
                                               + "\" does not match "
                                               + matcher.pattern().pattern());
        }

        try {
            List<Field> fields =
                FieldUtils.getFieldsListWithAnnotation(getClass(),
                                                       MatcherGroup.class);

            for (Field field : fields) {
                MatcherGroup group = field.getAnnotation(MatcherGroup.class);
                String string = matcher.group(group.value());
                Object value =
                    (string != null)
                        ? convertTo(string, field.getType())
                        : null;

                FieldUtils.writeField(field, this, value, true);
            }

            List<Method> methods =
                MethodUtils.getMethodsListWithAnnotation(getClass(),
                                                         MatcherGroup.class,
                                                         true, true);

            for (Method method : methods) {
                MatcherGroup group = method.getAnnotation(MatcherGroup.class);
                Object value =
                    convertTo(matcher.group(group.value()),
                              method.getParameterTypes()[0]);

                MethodUtils.invokeMethod(this, true,
                                         method.getName(),
                                         new Object[] { value },
                                         method.getParameterTypes());
            }
        } catch (IllegalAccessException exception) {
            exception.printStackTrace(System.err);
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new IllegalArgumentException(String.valueOf(matcher),
                                               exception);
        }
    }

    /**
     * Method to get the {@link Matcher} for the argument input.
     *
     * @param   sequence        The {@link CharSequence} to parse.
     *
     * @return  The {@link Matcher} for the {@link #pattern()} applied to
     *          the {@link CharSequence}.
     */
    default Matcher matcher(CharSequence sequence) {
        return pattern().matcher(sequence);
    }

    /**
     * Method to get the compiled {@link Pattern} for this annotated bean.
     *
     * @return  The {@link Pattern}.
     */
    default Pattern pattern() {
        Pattern pattern = null;
        PatternRegex annotation = getClass().getAnnotation(PatternRegex.class);

        try {
            pattern = Pattern.compile(annotation.value());
        } catch (Exception exception) {
            throw new AnnotationFormatError(annotation.toString(), exception);
        }

        return pattern;
    }
}
