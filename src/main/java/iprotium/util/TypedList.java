/*
 * $Id: TypedList.java,v 1.2 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util;

import java.util.AbstractList;
import java.util.List;

/**
 * {@link List} implementation that wraps and provides a "generic typed"
 * view to an underlying {@link List}.
 *
 * @param       <E>             The type of {@link Object} this {@link List}
 *                              contains.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class TypedList<E> extends AbstractList<E> {
    private final Class<? extends E> type;
    private final List<?> list;

    /**
     * Sole constructor.
     *
     * @param   type            The underlying type of the {@link List}
     *                          elements.
     * @param   list            The {@link List} to wrap.
     */
    public TypedList(Class<? extends E> type, List<?> list) {
        super();

        if (type != null) {
            this.type = type;
        } else {
            throw new NullPointerException("type");
        }

        if (list != null) {
            this.list = list;
        } else {
            throw new NullPointerException("list");
        }
    }

    @Override
    public E get(int index) { return type.cast(list.get(index)); }

    @Override
    public int size() { return list.size(); }
}
/*
 * $Log: not supported by cvs2svn $
 */
