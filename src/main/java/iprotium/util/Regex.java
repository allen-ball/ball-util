/*
 * $Id$
 *
 * Copyright 2011, 2012 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to specify a field constant value is a well-formed regular
 * expression {@link String}.
 *
 * @see iprotium.annotation.processing.RegexProcessor
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, LOCAL_VARIABLE })
public @interface Regex {
}
