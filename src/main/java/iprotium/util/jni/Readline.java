/*
 * $Id: Readline.java,v 1.3 2010-12-26 18:30:09 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import java.util.ResourceBundle;

/**
 * Provides wrappers to the native Readline functions.  See the
 * {@code readline(3)} manual page.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.3 $
 */
public class Readline {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(Readline.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    /**
     * {@code rl_readline_name} value holder used by native methods.
     */
    private volatile long pointer = 0;

    /**
     * See the {@code readline(3)} manual page.
     *
     * @param   rl_readline_name
     *                          The name of the invoking command.
     */
    public Readline(String rl_readline_name) { init(rl_readline_name); }

    private native void init(String rl_readline_name);

    /**
     * See the {@code readline(3)} manual page.
     */
    public native String readline(String prompt);

    /**
     * See the {@code readline(3)} manual page.
     */
    public native void add_history(String line);

    @Override
    protected void finalize() { end(); }
    private native void end();
}
/*
 * $Log: not supported by cvs2svn $
 */
