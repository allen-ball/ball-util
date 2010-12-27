/*
 * $Id: EditLine.java,v 1.5 2010-12-27 01:57:43 ball Exp $
 *
 * Copyright 2010 Allen D. Ball.  All rights reserved.
 */
package iprotium.util.jni;

import java.io.File;
import java.util.ResourceBundle;

/**
 * Provides wrappers to the native EditLine (libedit) functions.  See the
 * editline(3) manual page.
 *
 * @author <a href="mailto:ball@iprotium.com">Allen D. Ball</a>
 * @version $Revision: 1.5 $
 */
public class EditLine {
    private static final ResourceBundle BUNDLE =
        ResourceBundle.getBundle(EditLine.class.getName());

    static { System.loadLibrary(BUNDLE.getString("jnilib").trim()); }

    /**
     * {@code EditLine *} holder used by native methods.
     */
    private volatile long pointer = 0;

    /**
     * See the el_init(3) manual page.
     *
     * @param   prog            The name of the invoking command.
     */
    public EditLine(String prog) { init(prog); }
    private native void init(String prog);

    /**
     * GNU readline compatilble method.  See the {@code readline(3)} manual
     * page.
     */
    public native String readline(String prompt);

    /**
     * GNU readline compatilble method.  See the {@code readline(3)} manual
     * page.
     */
    public native void add_history(String line);

    /**
     * See the el_reset(3) manual page.
     */
    protected native void reset();

    /**
     * See the el_gets(3) manual page.
     */
    protected native String gets();

    /**
     * See the el_getc(3) manual page.
     */
    protected native int getc();

    /**
     * See the el_push(3) manual page.
     *
     * @param   string          The argument {@link String}.
     */
    protected native void push(String string);

    /**
     * See the el_parse(3) manual page.
     *
     * @param   argv            The argument {@code argv}.
     */
    protected native int parse(String[] argv);

    /**
     * See the el_source(3) manual page.
     *
     * @param   file            The {@link File} to source.
     */
    protected int source(File file) { return source(file.getAbsolutePath()); }
    private native int source(String path);

    @Override
    protected void finalize() { end(); }
    private native void end();
}
/*
 * $Log: not supported by cvs2svn $
 */
