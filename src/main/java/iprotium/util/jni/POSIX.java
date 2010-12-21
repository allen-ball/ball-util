/*
 * $Id: POSIX.java,v 1.11 2010-12-21 17:28:02 ball Exp $
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
 * @version $Revision: 1.11 $
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

    private static native boolean link(String from, String to);

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

    private static native long opendir(String path);
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2010/07/28 05:04:57  ball
 * Changed readlink(File) method to return FileImpl.
 *
 */
