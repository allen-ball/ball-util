package ball.annotation;
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

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link java.lang.annotation.Annotation}s to generate a JAR
 * {@link java.util.jar.Manifest META-INF/MANIFEST.MF}.
 *
 * @see ball.annotation.processing.ManifestProcessor
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class Manifest {
    private Manifest() { }

    /**
     * Attribute name
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ ANNOTATION_TYPE })
    public @interface Attribute {
        String value();
    }

    /**
     * Main-Class
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ TYPE })
    @Attribute("Main-Class")
    public @interface MainClass {
    }

    /**
     * {@link Package} section
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ PACKAGE })
    public @interface Section {
        boolean sealed() default true;
    }

    /**
     * Java-Bean
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ TYPE })
    @Attribute("Java-Bean")
    public @interface JavaBean {
    }

    /**
     * Depends-On
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ TYPE })
    @Attribute("Depends-On")
    public @interface DependsOn {
        String[] value();
    }

    /**
     * Design-Time-Only
     */
    @Documented
    @Retention(RUNTIME)
    @Target({ TYPE })
    @Attribute("Design-Time-Only")
    public @interface DesignTimeOnly {
        String[] value();
    }
}
