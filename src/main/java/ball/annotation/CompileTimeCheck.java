package ball.annotation;
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
/* import javax.tools.Diagnostic; */

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
/* import static javax.tools.Diagnostic.Kind.WARNING; */

/**
 * {@link java.lang.annotation.Annotation} to indicate the initializer
 * should be checked at compile-time.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface CompileTimeCheck {
    /* Diagnostic.Kind value() default WARNING; */
}
