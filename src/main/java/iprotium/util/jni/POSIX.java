/*
 * $Id: POSIX.java,v 1.5 2009-04-29 04:40:27 ball Exp $
 *
 * Copyright 2008, 2009 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Class whose static methods provide wrappers to native POSIX interfaces.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class POSIX {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(POSIX.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    private POSIX() { }

    /**
     * Wrapper to the POSIX link(2) call.
     *
     * http://www.opengroup.org/onlinepubs/000095399/functions/link.html
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
     * Wrapper to the POSIX readlink(2) call.
     *
     * http://www.opengroup.org/onlinepubs/000095399/functions/readlink.html
     *
     * @param   from            The source File.
     *
     * @return  A File representing the link target if from is a symlink;
     *          <code>null</code> otherwise.
     */
    public static File readlink(File from) {
        String to = readlink(from.getPath());

        return (to != null) ? new File(to) : null;
    }

    private static native String readlink(String from);

    /**
     * Wrapper to the POSIX symlink(2) call.
     *
     * http://www.opengroup.org/onlinepubs/000095399/functions/symlink.html
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
