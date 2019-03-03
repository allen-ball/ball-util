/*
 * $Id$
 *
 * Copyright 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation;

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
    String[] lines();

    /**
     * {@link #CLASS} = {@value #CLASS}
     */
    public static final String CLASS = "class";

    /**
     * {@link #PACKAGE} = {@value #PACKAGE}
     */
    public static final String PACKAGE = "package";
}
