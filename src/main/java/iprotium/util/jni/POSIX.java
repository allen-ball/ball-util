/*
 * $Id: POSIX.java,v 1.10 2010-08-23 03:43:55 ball Exp $
 *
 * Copyright 2008 - 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import iprotium.io.FileImpl;
import java.io.File;
import java.util.ResourceBundle;

/**
 * Provides wrappers to native
 * <a href="http://www.opengroup.org/onlinepubs/000095399/idx/functions.html">
 *   POSIX
 * </a> functions.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.10 $
 */
public abstract class POSIX {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(POSIX.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    private POSIX() { }

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/link.html">link</a>
     *
     * @param   from            The source {@link File}.
     * @param   to              The target {@link File}.
     *
     * @return  {@code true} if successful; {@code false} otherwise.
     */
    public static boolean link(File from, File to) {
        return link(from.getPath(), to.getPath());
    }

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/symlink.html">symlink</a>
     *
     * @param   from            The source {@link File}.
     * @param   to              The target {@link File}.
     *
     * @return  {@code true} if successful; {@code false} otherwise.
     */
    public static boolean symlink(File from, File to) {
        return symlink(from.getPath(), to.getPath());
    }

    private static native boolean link(String from, String to);
    private static native boolean symlink(String from, String to);

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/readlink.html">readlink</a>
     *
     * @param   from            The source {@link File}.
     *
     * @return  A {@link FileImpl} representing the link target if
     *          {@code from} is a symlink; {@code null} otherwise.
     */
    public static FileImpl readlink(File from) {
        String to = readlink(from.getPath());

        return (to != null) ? new FileImpl(to) : null;
    }

    private static native String readlink(String from);

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/opendir.html">opendir</a>
     *
     * @param   directory       The directory ({@link File}) to open.
     *
     * @return  A {@link DIR} representing the opened directory.
     */
    public static DIR opendir(File directory) {
        return new DIR(opendir(directory.getPath()));
    }

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/closedir.html">closedir</a>
     *
     * @param   dirp            The {@link DIR} to close.
     */
    public static void closedir(DIR dirp) {
        synchronized (dirp) {
            long peer = dirp.peer;

            dirp.peer = 0;

            if (peer != 0) {
                closedir(peer);
            }
        }
    }

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/readdir.html">readdir</a>
     *
     * @param   dirp            The {@link DIR} to read an entry from.
     *
     * @return  The name of the next directory entry or {@code null} if
     *          there are no more entries to read.
     */
    public static String readdir(DIR dirp) {
        String name = null;

        synchronized (dirp) {
            name = (dirp.peer != 0) ? readdir(dirp.peer) : null;
        }

        return name;
    }

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/rewinddir.html">rewinddir</a>
     *
     * @param   dirp            The {@link DIR} to rewind.
     */
    public static void rewinddir(DIR dirp) {
        synchronized (dirp) {
            if (dirp.peer != 0) {
                rewinddir(dirp.peer);
            }
        }
    }

    private static native long opendir(String path);
    private static native void closedir(long peer);
    private static native String readdir(long peer);
    private static native void rewinddir(long peer);
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2010/07/28 05:04:57  ball
 * Changed readlink(File) method to return FileImpl.
 *
 * Revision 1.8  2009/10/25 14:57:29  ball
 * Added opendir(File), closedir(DIR), readdir(DIR), and
 * rewinddir(DIR) methods.
 *
 */
