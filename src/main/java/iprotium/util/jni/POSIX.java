/*
 * $Id: POSIX.java,v 1.7 2009-09-04 17:13:43 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Provides wrappers to native
 * <a href="http://www.opengroup.org/onlinepubs/000095399/idx/functions.html">
 *   POSIX
 * </a> functions.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.7 $
 */
public class POSIX {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(POSIX.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    private POSIX() { }

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/link.html">link</a>
     *
     * @param   from            The source File.
     * @param   to              The target File.
     *
     * @return  <code>true</code> if successful;
     *          <code>false</code> otherwise.
     */
    public static boolean link(File from, File to) {
        return link(from.getPath(), to.getPath());
    }

    private static native boolean link(String from, String to);

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/readlink.html">readlink</a>
     *
     * @param   from            The source File.
     *
     * @return  A File representing the link target if <code>from</code> is
     *          a symlink; <code>null</code> otherwise.
     */
    public static File readlink(File from) {
        String to = readlink(from.getPath());

        return (to != null) ? new File(to) : null;
    }

    private static native String readlink(String from);

    /**
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/symlink.html">symlink</a>
     *
     * @param   from            The source File.
     * @param   to              The target File.
     *
     * @return  <code>true</code> if successful;
     *          <code>false</code> otherwise.
     */
    public static boolean symlink(File from, File to) {
        return symlink(from.getPath(), to.getPath());
    }

    private static native boolean symlink(String from, String to);
}
/*
 * $Log: not supported by cvs2svn $
 */
