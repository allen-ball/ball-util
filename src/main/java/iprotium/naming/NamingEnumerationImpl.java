/*
 * $Id: NamingEnumerationImpl.java,v 1.2 2009-08-19 04:20:38 ball Exp $
 *
 * Copyright 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.naming;

import java.util.ArrayList;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/**
 * NamingEnumeration implementation.
 *
 * @param       <T>     Type of element stored in this NamingEnumeration.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class NamingEnumerationImpl<T extends NameClassPair>
             extends ArrayList<T> implements NamingEnumeration<T> {
    private static final long serialVersionUID = -1399634524762373885L;

    private NamingException exception = null;

    /**
     * Sole constructor.
     *
     * @param   exception       If non-null, the NamingException instance
     *                          thrown by hasMore() when the enumeration is
     *                          exhausted.
     *
     * @see #hasMore()
     */
    public NamingEnumerationImpl(NamingException exception) {
        super();

        setNamingException(exception);
    }

    /**
     * NamingException getter method.
     *
     * @return  The NamingException instance.
     */
    protected NamingException getNamingException() { return exception; }

    /**
     * NamingException setter method.
     *
     * @param   exception       The NamingException instance.
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