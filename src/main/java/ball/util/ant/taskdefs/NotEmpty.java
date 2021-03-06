package ball.util.ant.taskdefs;
/*-
 * ##########################################################################
 * Utilities
 * %%
 * Copyright (C) 2008 - 2022 Allen D. Ball
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
        protected void check(Task task, TaskConfigurationChecker checker, String name, Object value) {
            checker.assertConfig(! (((Collection) value).isEmpty()), "`" + name + "' attribute must not be empty");
        }
    }
}
