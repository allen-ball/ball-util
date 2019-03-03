/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link Spliterator} abstract base class that dispatches to
 * {@link Spliterator}s supplied through an {@link Iterator} of
 * {@link Spliterator} {@link Supplier}s.
 *
 * @author {@link.uri mailto:ball@hcf.dev Allen D. Ball}
 * @version $Revision$
 */
public abstract class DispatchSpliterator<T> extends AbstractSpliterator<T> {
    private Iterator<Supplier<Spliterator<T>>> spliterators = null;
    private Spliterator<T> spliterator = Spliterators.emptySpliterator();

    /**
     * See {@link AbstractSpliterator} constructor.
     *
     * @param   estimate        The estimated size of {@link.this}
     *                          {@link Spliterator} if known, otherwise
     *                          {@link Long#MAX_VALUE}.
     * @param   characteristics Properties of this {@link Spliterator}'s
     *                          source or elements.  If {@link #SIZED} is
     *                          reported then {@link.this}
     *                          {@link Spliterator} will additionally report
     *                          {@link #SUBSIZED}.
     */
    protected DispatchSpliterator(long estimate, int characteristics) {
        super(estimate, characteristics);
    }

    /**
     * Method to provide the {@link Spliterator} {@link Supplier}s.  This
     * method is not called until the first call of
     * {@link #tryAdvance(Consumer)}.
     *
     * @return  The {@link Iterator} of {@link Spliterator}
     *          {@link Supplier}s.
     */
    protected abstract Iterator<Supplier<Spliterator<T>>> spliterators();

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
        boolean accepted = spliterator.tryAdvance(consumer);

        if (! accepted) {
            if (spliterators == null) {
                spliterators = spliterators();
            }

            if (spliterators.hasNext()) {
                spliterator = spliterators.next().get();
                accepted = tryAdvance(consumer);
            }
        }

        return accepted;
    }
}
