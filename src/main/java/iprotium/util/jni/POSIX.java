/*
 * $Id: POSIX.java,v 1.6 2009-06-21 03:46:45 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Class whose static methods provide wrappers to native POSIX functions.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.6 $
 */
public class POSIX {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(POSIX.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    private POSIX() { }

    /**
     * Wrapper to POSIX
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/link.html">link(2)</a>
     * function.
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
     * Wrapper to POSIX
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/readlink.html">readlink(2)</a>
     * function.
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
     * Wrapper to POSIX
     * <a href="http://www.opengroup.org/onlinepubs/000095399/functions/symlink.html">symlink(2)</a>
     * function.
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
