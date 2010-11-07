/*
 * $Id: ConvertWith.java,v 1.2 2010-11-07 21:55:56 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.beans;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to specify the {@link Converter} implementation for a method
 * parameter.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
@Documented
@Retention(RUNTIME)
@Target({ METHOD, PARAMETER })
public @interface ConvertWith {
    Class<? extends Converter<?>> value();
}
/*
 * $Log: not supported by cvs2svn $
 */
