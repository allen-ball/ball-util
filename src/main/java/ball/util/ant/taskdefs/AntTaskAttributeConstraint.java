package ball.util.ant.taskdefs;
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
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
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
