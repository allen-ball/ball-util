/*
 * $Id: POSIX.java,v 1.2 2008-11-04 07:42:13 ball Exp $
 *
 * Copyright 2008 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Class whose static methods provide wrappers to native POSIX interfaces.
 *
 * @@author <a href="mailto:ball@@iprotium.com">Allen D. Ball</a>
 * @@version $Revision: 1.2 $
 */
public class POSIX {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(POSIX.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    private POSIX() { }

    /**
     * Wrapper to the POSIX link(2) call.
     *
     * @@param   source          The File to be linked.
     * @@param   target          The link target File.
     *
     * @@return  <code>true</code> if successful;
     *          <code>false</code> otherwise.
     */
    public static boolean link(File source, File target) {
        return link(source.getPath(), target.getPath());
    }

    private static native boolean link(String source, String target);

    /**
     * Wrapper to the POSIX symlink(2) call.
     *
     * @@param   source          The File to be linked.
     * @@param   target          The link target File.
     *
     * @@return  <code>true</code> if successful;
     *          <code>false</code> otherwise.
     */
    public static boolean symlink(File source, File target) {
        return symlink(source.getPath(), target.getPath());
    }

    private static native boolean symlink(String source, String target);
}
/*
 * $Log: not supported by cvs2svn $
 */
