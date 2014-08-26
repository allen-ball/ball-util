/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation;

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
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
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

    @Documented
    @Retention(RUNTIME)
    @Target({ TYPE })
    @Attribute("Java-Bean")
    public @interface JavaBean {
    }

    @Documented
    @Retention(RUNTIME)
    @Target({ TYPE })
    @Attribute("Depends-On")
    public @interface DependsOn {
        String[] value();
    }

    @Documented
    @Retention(RUNTIME)
    @Target({ TYPE })
    @Attribute("Design-Time-Only")
    public @interface DesignTimeOnly {
        String[] value();
    }
}
