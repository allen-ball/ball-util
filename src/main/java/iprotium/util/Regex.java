/*
 * $Id: ConvertWith.java,v 1.2 2010/11/07 21:55:56 ball Exp $
 *
 * Copyright 2011 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to specify a field is a regular expression {@link String}.
 *
 * @see iprotium.annotation.processing.RegexProcessor
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface Regex {
}
