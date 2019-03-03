/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.tools.javadoc;

import com.sun.tools.doclets.Taglet;

/**
 * Interface indicating {@link Taglet} is annotated with {@link TagletName}
 * and related annotations.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public interface AnnotatedTaglet extends Taglet {

    /**
     * Method to get {@link TagletName#value()}.
     *
     * @return  {@link TagletName#value()} if {@link.this} {@link Taglet} is
     *          annotated; {@code null} otherwise.
     */
    @Override
    default String getName() {
        return getClass().getAnnotation(TagletName.class).value();
    }
}
