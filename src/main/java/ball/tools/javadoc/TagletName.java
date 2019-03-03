/*
 * $Id$
 *
 * Copyright 2013, 2014 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link java.lang.annotation.Annotation} to mark
 * {@link com.sun.tools.doclets.internal.toolkit.taglets.Taglet}s
 * with their name.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE })
public @interface TagletName {
    String value();
}
