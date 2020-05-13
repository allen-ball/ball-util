package ball.annotation;
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
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link java.lang.annotation.Annotation} to provide resource file
 * fragments.  {@link #path() path} specifies the resource file path and
 * {@link #lines() lines} specifies the file fragment lines.  The
 * {@link ball.annotation.processing.ResourceFileProcessor} uses
 * {@link ball.text.ParameterizedMessageFormat} to replace the
 * {@value #CLASS} named parameter with the annotated class name and the
 * {@value #PACKAGE} named parameter with the annotated class' package name
 * for both the annotation {@link #path() path} and {@link #lines() lines}
 * members.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
public @interface ResourceFile {
    String path();
    String[] lines() default { };

    /**
     * {@link #CLASS} = {@value #CLASS}
     */
    public static final String CLASS = "class";

    /**
     * {@link #PACKAGE} = {@value #PACKAGE}
     */
    public static final String PACKAGE = "package";
}
