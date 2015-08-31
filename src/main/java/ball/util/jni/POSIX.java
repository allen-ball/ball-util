/*
 * $Id$
 *
 * Copyright 2008 - 2015 Allen D. Ball.  All rights reserved.
 */
package ball.util.jni;

import ball.io.FileImpl;
import ball.util.JNILib;
import java.io.File;

/**
 * Provides wrappers to native
 * {@link.uri http://www.opengroup.org/onlinepubs/000095399/idx/functions.html POSIX}
 * functions.
 *
 * @author {@link.uri mailto:ball@iprotium.com Allen D. Ball}
 * @version $Revision$
 */
public abstract class POSIX {
    static { JNILib.loadFor(POSIX.class); }

    private POSIX() { }

    /**
     * {@link.uri http://www.opengroup.org/onlinepubs/000095399/functions/link.html link}
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
     * {@link.uri http://www.opengroup.org/onlinepubs/000095399/functions/symlink.html symlink}
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
     * {@link.uri http://www.opengroup.org/onlinepubs/000095399/functions/readlink.html readlink}
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
     * {@link.uri http://www.opengroup.org/onlinepubs/000095399/functions/opendir.html opendir}
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
