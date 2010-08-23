/*
 * $Id: DIR.java,v 1.2 2010-08-23 03:43:55 ball Exp $
 *
 * Copyright 2009, 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * See {@link POSIX#opendir(File)}, {@link POSIX#closedir(DIR)}, and
 * {@link POSIX#readdir(DIR)}.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.2 $
 */
public class DIR implements Closeable {

    /**
     * Opaque
     * <a href="http://www.opengroup.org/onlinepubs/000095399/basedefs/dirent.h.html">
     *  <code>DIR *</code>
     * </a> pointer returned from JNI code.
     */
    protected long peer = 0;

    /**
     * Sole constructor.
     *
     * @param   peer            Opaque DIR pointer returned from JNI
     *                          interface.
     */
    protected DIR(long peer) { this.peer = peer; }

    /**
     * Method to iteratively call {@link POSIX#readdir(DIR)} and return a
     * {@link List} of the remaining entry names.
     */
    public List<String> list() {
        List<String> list = new ArrayList<String>();

        for (;;) {
            String name = POSIX.readdir(this);

            if (name != null) {
                list.add(name);
            } else {
                break;
            }
        }

        return list;
    }

    @Override
    public void close() { POSIX.closedir(this); }

    @Override
    protected void finalize() { close(); }
}
/*
 * $Log: not supported by cvs2svn $
 */
