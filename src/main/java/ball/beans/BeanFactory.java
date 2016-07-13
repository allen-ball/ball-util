/*
 * $Id$
 *
 * Copyright 2012 - 2016 Allen D. Ball.  All rights reserved.
 */
package ball.beans;

import ball.util.Factory;
import java.beans.ConstructorProperties;

/**
 * {@link Factory} implementation specifically for constructing beans.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class BeanFactory<T> extends Factory<T> {
    private static final long serialVersionUID = 4025033835778193946L;

    /**
     * See {@link Factory#Factory(Class)}.
     *
     * @param   type            The {@link Class} of {@link Object} this
     *                          {@link Factory} will produce.
     */
    @ConstructorProperties({ "type" })
    public BeanFactory(Class<? extends T> type) { super(type); }
}
