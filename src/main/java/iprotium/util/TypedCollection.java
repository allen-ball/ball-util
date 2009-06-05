/*
 * $Id: TypedCollection.java,v 1.1 2009-06-05 06:31:40 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

/**
 * Collection implementation that wraps and provides a "generic typed" view
 * to an underlying Collection.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.1 $
 */
public class TypedCollection<E> extends AbstractCollection<E> {
    private final Class<? extends E> type;
    private final Collection<?> collection;

    /**
     * Sole constructor.
     *
     * @param   type            The underlying type of the Collection
     *                          elements.
     * @param   collection      The Collection to wrap.
     */
    public TypedCollection(Class<? extends E> type, Collection<?> collection) {
        super();

        if (type != null) {
            this.type = type;
        } else {
            throw new NullPointerException("type");
        }

        if (collection != null) {
            this.collection = collection;
        } else {
            throw new NullPointerException("collection");
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new TypedIterator(type, collection.iterator());
    }

    @Override
    public int size() { return collection.size(); }

    private class TypedIterator implements Iterator<E> {
        private final Class<? extends E> type;
        private final Iterator<?> iterator;

        public TypedIterator(Class<? extends E> type, Iterator<?> iterator) {
            if (type != null) {
                this.type = type;
            } else {
                throw new NullPointerException("type");
            }

            if (iterator != null) {
                this.iterator = iterator;
            } else {
                throw new NullPointerException("iterator");
            }
        }

        public boolean hasNext() { return iterator.hasNext(); }
        public E next() { return type.cast(iterator.next()); }
        public void remove() { throw new UnsupportedOperationException(); }
    }
}
/*
 * $Log: not supported by cvs2svn $
 */
