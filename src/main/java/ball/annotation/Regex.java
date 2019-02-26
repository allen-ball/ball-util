/*
 * $Id$
 *
 * Copyright 2011 - 2019 Allen D. Ball.  All rights reserved.
 */
package ball.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to specify a field constant value is a well-formed regular
 * expression {@link String} which may be passed to
 * {@link java.util.regex.Pattern#compile(String)} without throwing
 * {@link java.util.regex.PatternSyntaxException}.
 *
 * @see ball.annotation.processing.RegexProcessor
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, LOCAL_VARIABLE })
public @interface Regex {
}
