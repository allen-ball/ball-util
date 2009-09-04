/*
 * $Id: NamingEnumerationImpl.java,v 1.3 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.naming;

import java.util.ArrayList;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * {@link NamingEnumeration} implementation.
 *
 * @param       <T>     Type of element stored in this
 *                      {@link NamingEnumeration}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class NamingEnumerationImpl<T extends NameClassPair>
             extends ArrayList<T> implements NamingEnumeration<T> {
    private static final long serialVersionUID = -1399634524762373885L;

    private NamingException exception = null;

    /**
     * Sole constructor.
     *
     * @param   exception       If non-<code>null</code>, the
     *                          {@link NamingException} instance thrown by
     *                          {@link #hasMore()} when the enumeration is
     *                          exhausted.
     *
     * @see #hasMore()
     * @see #setNamingException(NamingException)
     */
    public NamingEnumerationImpl(NamingException exception) {
        super();

        setNamingException(exception);
    }

    /**
     * {@link NamingException} getter method.  Thrown by {@link #hasMore()}
     * (if non-<code>null</code>) when the enumeration is exhausted.
     *
     * @return  The {@link NamingException} instance.
     */
    protected NamingException getNamingException() { return exception; }

    /**
     * {@link NamingException} setter method.  Thrown by {@link #hasMore()}
     * (if non-<code>null</code>) when the enumeration is exhausted.
     *
     * @param   exception       The {@link NamingException} instance.
     */
    protected void setNamingException(NamingException exception) {
        this.exception = exception;
    }

    public boolean hasMore() throws NamingException {
        boolean hasMore = hasMoreElements();

        if (! hasMore) {
            NamingException exception = getNamingException();

            if (exception != null) {
                throw exception;
            }
        }

        return hasMore;
    }

    public T next() throws NamingException {
        hasMore();

        return nextElement();
    }

    public void close() throws NamingException { }

    public boolean hasMoreElements() { return (! isEmpty()); }

    public T nextElement() { return remove(0); }
}
/*
 * $Log: not supported by cvs2svn $
 */
