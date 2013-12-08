/*
 * $Id$
 *
 * Copyright 2009 - 2013 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import iprotium.util.AbstractIterator;
import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See {@link POSIX#opendir(File)}.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public class DIR implements Iterable<String>, Closeable {

    /**
     * Opaque
     * <a href="http://www.opengroup.org/onlinepubs/000095399/basedefs/dirent.h.html">
     *  <code>DIR *</code>
     * </a> pointer returned from JNI code.
     */
    private long pointer = 0;

    /**
     * Sole constructor.
     *
     * @param   pointer         Opaque DIR pointer returned from JNI
     *                          interface.
     */
    protected DIR(long pointer) { this.pointer = pointer; }

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/closedir.html">closedir</a>
     */
    public native void closedir();

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/readdir.html">readdir</a>
     *
     * @return  The name of the next directory entry or {@code null} if
     *          there are no more entries to read.
     */
    public native String readdir();

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/rewinddir.html">rewinddir</a>
     */
    public native void rewinddir();

    @Override
    public Iterator<String> iterator() { return new IteratorImpl(); }

    @Override
    public void close() { closedir(); }

    @Override
    protected void finalize() { close(); }

    private class IteratorImpl extends AbstractIterator<String> {
        private String next;

        public IteratorImpl() {
            super();

            this.next = readdir();
        }

        @Override
        public boolean hasNext() { return (next != null); }

        @Override
        public String next() {
            String next = null;

            if (hasNext()) {
                next = this.next;
                this.next = readdir();
            } else {
                throw new NoSuchElementException();
            }

            return next;
        }
    }
}
