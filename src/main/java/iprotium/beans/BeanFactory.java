/*
 * $Id$
 *
 * Copyright 2012, 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.beans;

import iprotium.util.Factory;
import java.beans.ConstructorProperties;

/**
 * {@link Factory} implementation specifically for constructing beans.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class BeanFactory<T> extends Factory<T> {
    private static final long serialVersionUID = 3136151663166173186L;

    /**
     * See {@link Factory#Factory(Class)}.
     */
    @ConstructorProperties({ "type" })
    public BeanFactory(Class<? extends T> type) { super(type); }
}
