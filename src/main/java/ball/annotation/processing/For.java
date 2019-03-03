/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.annotation.processing;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link AbstractAnnotationProcessor} {@link Annotation} to specify
 * {@link Annotation}s.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
public @interface For {
    Class<? extends Annotation>[] value();
}
