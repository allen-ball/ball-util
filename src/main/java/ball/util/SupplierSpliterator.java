/*
 * $Id$
 *
 * Copyright 2019 Allen D. Ball.  All rights reserved.
 */
package ball.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * {@link Spliterator} abstract base class that dispatches to
 * {@link Spliterator}s supplied through a {@link Collection} of
 * {@link Spliterator} {@link Supplier}s.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class SupplierSpliterator<T> extends AbstractSpliterator<T> {
    private Iterator<Supplier<Spliterator<T>>> spliterators = null;
    private Spliterator<T> spliterator = Spliterators.emptySpliterator();

    /**
     * See {@link AbstractSpliterator} corresponding constructor.
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
    protected SupplierSpliterator(long estimate, int characteristics) {
        super(estimate, characteristics);
    }

    /**
     * Method to provide the {@link Spliterator} {@link Supplier}s.
     *
     * @return  The {@link Collection} of {@link Spliterator}
     *          {@link Supplier}s.
     */
    protected abstract Collection<Supplier<Spliterator<T>>> spliterators();

    /**
     * Convenience method to convert a {@link Collection} to a
     * {@link Spliterator} {@link Supplier}.
     *
     * @param   collection      The {@link Collection} to supply.
     *
     * @return  The {@link Spliterator} {@link Supplier}.
     */
    protected Supplier<Spliterator<T>> supplier(Collection<T> collection) {
        return () -> Spliterators.spliterator(collection, characteristics());
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> consumer) {
        synchronized (this) {
            if (spliterators == null) {
                spliterators = spliterators().iterator();
            }
        }

        boolean accepted = spliterator.tryAdvance(consumer);

        if (! accepted) {
            if (spliterators.hasNext()) {
                spliterator = spliterators.next().get();
                accepted = tryAdvance(consumer);
            }
        }

        return accepted;
    }
}
